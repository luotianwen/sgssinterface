package com.sgss.www.conmon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;

import java.io.IOException;

public class WeiXin {


    public static  WeiXinXCXUserInfo xcxlogin(String code) throws IOException {
        String url="https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";
        url=url.replace("JSCODE",code).replace("APPID",PropKit.get("weixin.AppID"))
                .replace("SECRET",PropKit.get("weixin.AppSecret"));
        String data= HttpKit.get(url);
        ObjectMapper mapper = new ObjectMapper();
        WeiXinXCXUserInfo wd = mapper.readValue(data, WeiXinXCXUserInfo.class);
        //WeiXinData wd=JsonKit.parse(data,WeiXinData.class);
        return  wd;
    }

    public static  WeiXinData weixinlogin(String code) throws IOException {
        String url="https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
        url=url.replace("CODE",code).replace("APPID",PropKit.get("weixin.AppID"))
        .replace("SECRET",PropKit.get("weixin.AppSecret"));
        String data= HttpKit.get(url);
        ObjectMapper mapper = new ObjectMapper();
         WeiXinData wd = mapper.readValue(data, WeiXinData.class);
        //WeiXinData wd=JsonKit.parse(data,WeiXinData.class);
        return  wd;
    }
    public static  WeiXinUserInfo weixingetuserinfo(String ACCESS_TOKEN,String OPENID) throws IOException {
        String url="https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";
        url=url.replace("OPENID",OPENID).replace("ACCESS_TOKEN",ACCESS_TOKEN);
        String data= HttpKit.get(url);
        ObjectMapper mapper = new ObjectMapper();
         WeiXinUserInfo wd = mapper.readValue(data,  WeiXinUserInfo.class);
       // WeiXinUserInfo wd=JsonKit.parse(data,WeiXinUserInfo.class);
        return  wd;
    }


}
