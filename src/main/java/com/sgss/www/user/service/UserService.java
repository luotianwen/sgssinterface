package com.sgss.www.user.service;

import com.aliyuncs.exceptions.ClientException;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.sgss.www.conmon.*;

import java.io.IOException;
import java.util.List;

public class UserService extends BaseService {
    @Before(Tx.class)
    public Record weixinlogin(String nickName, String code, String avatarUrl, String agentId) {
        WeiXinXCXUserInfo wd = null;
        try {
            wd = WeiXin.xcxlogin(code);

        } catch (IOException e) {
            throw new BusinessException("获取用户信息失败");
        }
        String openId=wd.getOpenid();
        Record record=getUserInfoByOpenId(openId);
        String dlId="";
        //没有用户
        if(null==record){
            //查找代理
            if(StrKit.notBlank(agentId)){
                String agentId2= (String) RedisTool.getObject(agentId);
                Record record2=Db.findFirst(Db.getSqlPara("user.getAgent",agentId2));
                //
                if(null!=record2){
                    dlId=agentId2;
                }
            }
            Db.update(Db.getSqlPara("user.saveUser", IdGen.uuid(),openId,nickName,avatarUrl,dlId));
            record=getUserInfoByOpenId(openId);
        }
        String key=record.get("id");
        if(RedisTool.exists(key)){
            RedisTool.del(key);
        }
        String tokenId = JwtUtil.sign(key, StaticPublic.USERTOKENTIME);
        //缓存用户token
        RedisTool.setexObject(tokenId, StaticPublic.USERTOKENTIME, key);
       /* //缓存用户信息*/
        RedisTool.setexObject(key, StaticPublic.USERTOKENTIME, record);
        record.remove("id");
        record.set("tokenId",tokenId);
        return record;
    }
    private Record getUserInfoByOpenId(String openId){
        Record record= Db.findFirst(Db.getSqlPara("user.getUserByopenId",openId));
        doImgPath(record);
        return record;
    }

    public List<Record> addresslist(String userId) {
        return Db.find(Db.getSqlPara("user.addresslist",userId));
    }
    @Before(Tx.class)
    public void deleteAddress(String userId, String addressId) {
        Db.update(Db.getSqlPara("user.deleteAddress",userId,addressId));
    }
    @Before(Tx.class)
    public void saveAddress(String userId, String addressId, String isDefault, String consignee, String phone, String address, String provinceId, String cityId, String areaId) {
       if(isDefault.equals("1")){
           Db.update(Db.getSqlPara("user.updateAddressIsDefault",userId));
       }
       Kv cond= Kv.create();
        cond.set("userId",userId);
        cond.set("addressId",addressId);
        cond.set("isDefault",isDefault);
        cond.set("consignee",consignee);
        cond.set("phone",phone);
        cond.set("address",address);
        cond.set("provinceId",provinceId);
        cond.set("cityId",cityId);
        cond.set("areaId",areaId);
        if(StrKit.notBlank(addressId)){
           Db.update(Db.getSqlPara("user.updateAddress",cond));
       }
       else{
            cond.set("addressId",IdGen.uuid());
            Db.update(Db.getSqlPara("user.saveAddress",cond));
        }
    }

    public Page<Record> getCouponList(String userId, int pageNumber, String type) {

        Page<Record>  records = Db.paginate(pageNumber, StaticPublic.PAGESIZE, Db.getSqlPara("user.getCouponList",userId,type));

        return records;
    }
    @Before(Tx.class)
    public void removeCoupon(String userId, String couponId) {
        Db.update(Db.getSqlPara("user.removeCoupon",userId,couponId));
    }

    @Before(Tx.class)
    public void feedback(String userId, List<String> imgs, String content) {
        String id = IdGen.uuid();
        Db.update(Db.getSqlPara("user.feedback", id, userId, content));
        for (String s : imgs
        ) {
            Db.update(Db.getSqlPara("user.feedbackImgs", IdGen.uuid(), id, s));
        }
    }
    /**
     * @param mobile
     * @param type             1注册 2登录 4绑定手机号
     * @return
     * @throws BusinessException
     */
    public Record sendmessage(String mobile,    String type) throws BusinessException {
        Record record = new Record();
        String key = String.format(StaticPublic.PRESHORTMESSAGE, mobile);
        String code="";
        if (StrKit.isBlank(mobile)) {
            record.set("verificationCode", "");
            record.set("data", "手机错误");
            throw new BusinessException("手机错误");
        }
        if (!BigNumberFormat.isPhone(mobile)) {
            record.set("verificationCode", "");
            record.set("data", "手机错误");
            throw new BusinessException("手机错误");
        }

        //注册
        if ("1".equals(type)) {
            SqlPara sqlPara = Db.getSqlPara("user.findByMobile", mobile);
            Record r = Db.findFirst(sqlPara);
            if (null != r) {
                throw new BusinessException("用户已存在");
            }
            RedisTool.del(key);
            code = getCode();
            try {
                SendMessage.sendRegisterMessageCode(mobile, code);
            } catch (ClientException e) {
                throw new BusinessException("短信发送失败");
            }
        }
        //登录
        else if ("2".equals(type)) {
            SqlPara sqlPara = Db.getSqlPara("user.findByMobile", mobile);
            Record r = Db.findFirst(sqlPara);
            if (null == r) {
                throw new BusinessException("用户不存在");
            }
            RedisTool.del(key);
            code = getCode();
            try {
                SendMessage.sendLoginMessageCode(mobile, code);
            } catch (ClientException e) {
                throw new BusinessException("短信发送失败");
            }
        }
        //忘记密码
        else if ("3".equals(type)) {
            SqlPara sqlPara = Db.getSqlPara("user.findByMobile", mobile);
            Record r = Db.findFirst(sqlPara);
            if (null == r) {
                throw new BusinessException("用户不存在");
            }
            RedisTool.del(key);
            code = getCode();
            try {
                SendMessage.sendForgetMessageCode(mobile, code);
            } catch (ClientException e) {
                throw new BusinessException("短信发送失败");
            }
        }
        //修改手机号
        else if ("4".equals(type)) {
            SqlPara sqlPara = Db.getSqlPara("user.findByMobile", mobile);
            Record r = Db.findFirst(sqlPara);
            if (null != r) {
                throw new BusinessException("用户已存在");
            }
            RedisTool.del(key);
            code = getCode();
            try {
                System.out.println("code:"+code);
                SendMessage.sendUpdateMessageCode(mobile, code);
            } catch (ClientException e) {
                throw new BusinessException("短信发送失败");
            }
        }
        RedisTool.setexObject(key, StaticPublic.SHORTMESSAGE, code);
        record.set("verificationCode", code);

        return record;
    }

    private String getCode() {
        String code = BigNumberFormat.getAppCode();
        String redisCode = (String) RedisTool.getObject(code);
        if (StrKit.notBlank(redisCode)) {
            code = getCode();
        }
        return code;
    }

    public void bindPhone(String userId, String phone) {
        this.sendmessage(phone,"4");
    }

    @Before(Tx.class)
    public void verificationBind(String userId, String phone, String verification) {
        String key = String.format(StaticPublic.PRESHORTMESSAGE, phone);
        String code= (String)RedisTool.getObject(key);
        if (!verification.equals(code)) {
            throw new BusinessException("验证码错误");
        }
       Db.update(Db.getSqlPara("user.bindPhone",userId,phone));
    }

    public Record getUserData(String userId) {
        return  Db.findFirst(Db.getSqlPara("user.getUserData",userId));

    }
    @Before(Tx.class)
    public void gainCouponById(String userId, String couponId) {
        Record r=Db.findFirst(Db.getSqlPara("user.getCouponById",couponId));
        if(r==null){
            throw new BusinessException("优惠券已过期");
        }
        r.set("userId",userId);

        int i=Db.update(Db.getSqlPara("user.updateCouponNum",couponId));
       if(i==0){
           throw new BusinessException("优惠券已领完");
       }
       try {
           Db.update(Db.getSqlPara("user.gainCouponById", r));
       }catch (Exception e){
           throw new BusinessException("领取优惠券失败");
       }
    }

    public Record getUserAgentData(String userId) {
        Record r=Db.findFirst(Db.getSqlPara("user.getUserAgentData",userId));
        if(null==r){
            r.set("vshow","-1");
        }

        return r;
    }
    @Before(Tx.class)
    public void saveUserAgentData(String userId, String mobile, String name) {
        Record r=Db.findFirst(Db.getSqlPara("user.getUserAgentData",userId));
        if(null!=r){
            throw new BusinessException("已经申请了");
        }
        Db.update(Db.getSqlPara("user.saveUserAgentData",IdGen.uuid(),userId,mobile,name));
    }
}
