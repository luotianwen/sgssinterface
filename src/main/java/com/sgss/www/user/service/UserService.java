package com.sgss.www.user.service;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.sgss.www.conmon.*;

public class UserService extends BaseService {
    @Before(Tx.class)
    public Record weixinlogin(String nickName, String openId, String avatarUrl) {
        Record record=getUserInfoByOpenId(openId);
        //没有用户
        if(null==record){
            Db.update(Db.getSqlPara("user.saveUser", IdGen.uuid(),openId,nickName,avatarUrl));
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

}
