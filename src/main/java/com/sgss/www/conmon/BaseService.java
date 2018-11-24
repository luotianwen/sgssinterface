package com.sgss.www.conmon;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;

public class BaseService {
    public void phone(Record r) {
        if (null == r) {
            return;
        }
        String phone = r.getStr("phone");
        phone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        r.set("phone", phone);
    }

    //生成很多个*号
    public String createAsterisk(int length) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            stringBuffer.append("*");
        }
        return stringBuffer.toString();
    }

    public void doName(Record r) {
        if (null == r) {
            return;
        }
        String uName = r.getStr("uName");
        //uName= uName.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");

        if (uName.length() > 1) {
            uName = uName.replaceAll("([\\u4e00-\\u9fa5]{1})(.*)", "$1" + createAsterisk(uName.length() - 1));
        }

        r.set("uName", uName);
    }

    public void doImgPath(Record r) {
        if (null == r) {
            return;
        }
        String headImg = r.getStr("headImg");
        if (StrKit.notBlank(headImg) && headImg.indexOf("http") < 0) {
            r.set("headImg", PropKit.get("fileServer") + headImg);
        }
        String logo = r.getStr("logo");
        if (StrKit.notBlank(logo) && logo.indexOf("http") < 0) {
            r.set("logo", PropKit.get("fileServer") + logo);
        }
    }
}
