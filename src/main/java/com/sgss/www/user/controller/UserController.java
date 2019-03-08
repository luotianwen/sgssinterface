package com.sgss.www.user.controller;

import com.google.common.collect.Lists;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.sgss.www.conmon.BaseController;
import com.sgss.www.conmon.BusinessException;
import com.sgss.www.conmon.DateUtil;
import com.sgss.www.conmon.ReqResponse;
import com.sgss.www.swagger.annotation.*;
import com.sgss.www.user.service.UserService;

import java.util.List;

@Api(tag = "user", description = "用户")
public class UserController extends BaseController {
    Log log = Log.getLog(UserController.class);
    @Inject
    UserService userService;
    @ApiOperation(url = "/v1/user/weixinlogin", tag = "user", httpMethod = "post", description = "微信小程序登录")
    @Params({
            @Param(name = "nickName", description = "昵称", required = true, dataType = "string"),
            @Param(name = "code", description = "微信code", required = true, dataType = "string"),
            @Param(name = "avatarUrl", description = "头像地址", required = true, dataType = "string"),
            @Param(name = "agentId", description = "agentId", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " tokenId  name 名称 headImg 头像 integral积分 nickname 昵称 phone 手机号"),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void weixinlogin() {
        String nickName=getPara("nickName");
        String code=getPara("code");
        String avatarUrl=getPara("avatarUrl");
        String agentId=getPara("agentId");
        ReqResponse<Record> r = new ReqResponse();
        if(StrKit.isBlank(nickName)){
            r.setCode(1);
            r.setMsg("昵称不能为空");
            renderJson(r);
        }
        if(StrKit.isBlank(code)){
            r.setCode(1);
            r.setMsg("code不能为空");
            renderJson(r);
        }
        if(StrKit.isBlank(avatarUrl)){
            r.setCode(1);
            r.setMsg("头像地址不能为空");
            renderJson(r);
        }

        Record user;
        try {
            user =userService.weixinlogin(nickName,code,avatarUrl,agentId);
            r.setData(user);
        }catch (BusinessException e){
            log.error("昵称"+nickName+" openId"+code+" 头像地址"+avatarUrl);
            r.setCode(1);
            r.setMsg("登录失败");
        }
        renderJson(r);
    }
    @ApiOperation(url = "/v1/user/addresslist", tag = "user", httpMethod = "post", description = "地址列表")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void addresslist() {
        String userId = getAttr("userId");
        ReqResponse<List<Record>> r = new ReqResponse();
        r.setData(userService.addresslist(userId));
        renderJson(r);
    }
    @ApiOperation(url = "/v1/user/deleteAddress", tag = "user", httpMethod = "post", description = "地址列表")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "addressId", description = "当前用户id", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void deleteAddress() {
        String userId = getAttr("userId");
        String addressId =getPara("addressId");
        ReqResponse<String> r = new ReqResponse();
         userService.deleteAddress(userId,addressId);
        renderJson(r);
    }
    @ApiOperation(url = "/v1/user/getUserData", tag = "user", httpMethod = "post", description = "个人中心数据")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void getUserData() {
        String userId = getAttr("userId");
        ReqResponse<Record> r = new ReqResponse();
        r.setData(userService.getUserData(userId));
        renderJson(r);
    }

    @ApiOperation(url = "/v1/user/saveUserAgentData", tag = "user", httpMethod = "post", description = "申请代理")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "mobile", description = "手机号", required = true, dataType = "string"),
            @Param(name = "name", description = "name", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void saveUserAgentData() {
        String userId = getAttr("userId");
        String mobile =getPara("mobile");
        String name =getPara("name");
        ReqResponse<Record> r = new ReqResponse();
         userService.saveUserAgentData(userId,mobile,name);
        renderJson(r);
    }
    @ApiOperation(url = "/v1/user/getUserAgentData", tag = "user", httpMethod = "post", description = "申请代理")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void getUserAgentData() {
        String userId = getAttr("userId");
        ReqResponse<Record> r = new ReqResponse();
        r.setData(userService.getUserAgentData(userId));
        renderJson(r);
    }
    @ApiOperation(url = "/v1/user/saveAddress", tag = "user", httpMethod = "post", description = "地址列表")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "addressId", description = "当前用户id", required = false, dataType = "string"),
            @Param(name = "isDefault", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "consignee", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "phone", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "address", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "areaId", description = "当前用户id", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void saveAddress() {
        String userId = getAttr("userId");
        String addressId =getPara("addressId");
        String isDefault =getPara("isDefault");
        String consignee =getPara("consignee");
        String phone =getPara("phone");
        String address =getPara("address");
            //110101
        String areaId =getPara("areaId");
        String provinceId =areaId.substring(0,2)+"0000";
        String cityId =areaId.substring(0,4)+"00";

        ReqResponse<String> r = new ReqResponse();
        userService.saveAddress(userId,addressId,isDefault,consignee,phone,address,provinceId,cityId,areaId);
        renderJson(r);
    }
    @ApiOperation(url = "/v1/user/getCouponList", tag = "user", httpMethod = "post", description = "优惠券列表")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "pageNumber", description = "页码", required = true, dataType = "string"),
            @Param(name = "type", description = "类型 1未使用 2 已使用 3已过期 ", required = false, dataType = "string"),

    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "  "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void getCouponList() {
        String userId = getAttr("userId");
        int pageNumber =getParaToInt("pageNumber");
        String type = getPara("type");
        ReqResponse<Page<Record>> r = new ReqResponse();
        r.setData(userService.getCouponList(userId,pageNumber,type));
        renderJson(r);
    }
    @ApiOperation(url = "/v1/user/gainCouponById", tag = "user", httpMethod = "post", description = "领取优惠券")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "couponId", description = "优惠券", required = false, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "   "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void gainCouponById() {
        String userId = getAttr("userId");
        String couponId = getPara("couponId");
        ReqResponse<Record> r = new ReqResponse();
        try {
            userService.gainCouponById(userId, couponId);
        } catch (Exception e) {
            r.setCode(1);
            r.setMsg(e.getMessage());
        }
        renderJson(r);
    }
    @ApiOperation(url = "/v1/user/removeCoupon", tag = "user", httpMethod = "post", description = "优惠券删除")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "couponId", description = "优惠券", required = false, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "   "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void removeCoupon() {
        String userId = getAttr("userId");
        String couponId = getPara("couponId");
        ReqResponse<Record> r = new ReqResponse();
        try {
            userService.removeCoupon(userId, couponId);
        } catch (Exception e) {
            r.setCode(1);
            r.setMsg("请求失败");
        }
        renderJson(r);
    }
    @ApiOperation(url = "/v1/user/feedback", tag = "user", httpMethod = "post", description = "用户反馈")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "content", description = "内容", required = true, dataType = "string"),
            @Param(name = "imgs", description = "图片数组", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "{  }"),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })

    public void feedback() {
        List<UploadFile> fileList = getFiles();
        getResponse().addHeader("Access-Control-Allow-Origin", "*");
        String userId = getAttr("userId");
        String content = getPara("content");
        List<String> ls = Lists.newArrayList();
        String imgs = "";
        String base = "";
        for (UploadFile uf : fileList) {
            base = USERFILES_BASE_URL + DateUtil.FormatDateMMDD() + "/";
            imgs = getFilePath(base, uf);
            ls.add(imgs);
        }

        ReqResponse<Record> r = new ReqResponse();
        try {
            userService.feedback(userId, ls, content);

        } catch (Exception e) {
            e.printStackTrace();
            r.setCode(1);

        }
        renderJson(r);
    }
    public static final String USERFILES_BASE_URL = "/userfiles/feedback";

    @ApiOperation(url = "/v1/user/bindPhone", tag = "user", httpMethod = "post", description = "绑定手机号")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "phone", description = "手机号", required = false, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "   "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void bindPhone() {
        String userId = getAttr("userId");
        String phone = getPara("phone");
        ReqResponse<Record> r = new ReqResponse();
        try {
            userService.bindPhone(userId, phone);
        } catch (Exception e) {
            r.setCode(1);
            r.setMsg("请求失败");
        }
        renderJson(r);
    }
    @ApiOperation(url = "/v1/user/verificationBind", tag = "user", httpMethod = "post", description = "绑定手机号")
    @Params({
            @Param(name = "tokenId", description = "当前用户id", required = true, dataType = "string"),
            @Param(name = "phone", description = "手机号", required = false, dataType = "string"),
            @Param(name = "verification", description = "验证码", required = false, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = "   "),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void verificationBind() {
        String userId = getAttr("userId");
        String phone = getPara("phone");
        String verification = getPara("verification");
        ReqResponse<Record> r = new ReqResponse();
        try {
            userService.verificationBind(userId, phone,verification);
        } catch (BusinessException e) {
            r.setCode(1);
            r.setMsg(e.getErrMsg());
        }
        renderJson(r);
    }

}
