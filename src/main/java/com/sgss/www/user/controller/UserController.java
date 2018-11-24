package com.sgss.www.user.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.sgss.www.conmon.BaseController;
import com.sgss.www.conmon.BusinessException;
import com.sgss.www.conmon.ReqResponse;
import com.sgss.www.pay.controller.PayController;
import com.sgss.www.swagger.annotation.*;
import com.sgss.www.user.service.UserService;

@Api(tag = "user", description = "用户")
public class UserController extends BaseController {
    Log log = Log.getLog(PayController.class);
    @Inject
    UserService userService;
    @ApiOperation(url = "/v1/user/weixinlogin", tag = "user", httpMethod = "post", description = "微信小程序登录")
    @Params({
            @Param(name = "nickName", description = "昵称", required = true, dataType = "string"),
            @Param(name = "openId", description = "微信openid", required = true, dataType = "string"),
            @Param(name = "avatarUrl", description = "头像地址", required = true, dataType = "string"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功", responseHeaders = {
                    @ResponseHeader(name = "code", description = " 0成功 1失败"),
                    @ResponseHeader(name = "data", description = " tokenId  name 名称 headImg 头像 integral积分 nickname 昵称 phone 手机号"),
                    @ResponseHeader(name = "msg", description = "失败原因")})
    })
    public void weixinlogin() {
        String nickName=getPara("nickName");
        String openId=getPara("openId");
        String avatarUrl=getPara("avatarUrl");
        ReqResponse<Record> r = new ReqResponse();
        if(StrKit.isBlank(nickName)){
            r.setCode(1);
            r.setMsg("昵称不能为空");
            renderJson(r);
        }
        if(StrKit.isBlank(openId)){
            r.setCode(1);
            r.setMsg("openId不能为空");
            renderJson(r);
        }
        if(StrKit.isBlank(avatarUrl)){
            r.setCode(1);
            r.setMsg("头像地址不能为空");
            renderJson(r);
        }

        Record user;
        try {
            user =userService.weixinlogin(nickName,openId,avatarUrl);
            r.setData(user);
        }catch (BusinessException e){
            log.error("昵称"+nickName+" openId"+openId+" 头像地址"+avatarUrl);
            r.setCode(1);
            r.setMsg("登录失败");
        }
        renderJson(r);
    }

}
