package com.sgss.www.conmon;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

public class AuthInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        Controller c = inv.getController();
        try {
            c.getFiles();
        } catch (Exception e) {
            //e.printStackTrace();
        }

        String token = c.getPara("tokenId");
        if (c.isParaExists("tokenId")) {
            if (StrKit.notBlank(token)) {
               if(!RedisTool.exists(token))
               {
                   ReqResponse<String> r=new ReqResponse();
                   r.setCode(1);
                   r.setMsg("用户未登录");
                   c.renderJson(r);
               }
               else {
                   //Map<String, Claim> map = JwtUtil.unsign(token);
                   String userId= (String) RedisTool.getObject(token);
                   if (StrKit.notBlank(userId)) {
                       c.setAttr("userId", userId);
                       inv.invoke();
                   } else {
                       ReqResponse<String> r = new ReqResponse();
                       r.setCode(1);
                       r.setMsg("用户未登录");
                       c.renderJson(r);
                   }
               }
            }
            else{
                ReqResponse<String> r=new ReqResponse();
                r.setCode(1);
                r.setMsg("用户未登录");
                  c.renderJson(r);
            }

        } else {
            inv.invoke();
        }


    }
}
