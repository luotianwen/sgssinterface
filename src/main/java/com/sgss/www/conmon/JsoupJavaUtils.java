package com.sgss.www.conmon;

import com.jfinal.kit.PropKit;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupJavaUtils {
    public static String replaceImgStyle(String input){
        Document parse= Jsoup.parseBodyFragment(input);
        Elements imgs=parse.getElementsByTag("img");
        String imgSrc="";
        if(null!=imgs&&imgs.size()>0){
            Element e;
            for (Element img:imgs){
                e=img;
                imgSrc=e.attr("src");
                if(imgSrc.indexOf("http") < 0) {
                    if(imgSrc.endsWith("/")){
                        imgSrc=imgSrc.substring(0,imgSrc.length()-1);
                    }
                    e.attr("src", imgSrc.replaceAll("/userfiles/", PropKit.get("fileServer") + "/userfiles/"));

                }
                else{
                    e.attr("src",imgSrc);
                }

                e.removeAttr("style");
                e.attr("style","width:100%;");
                //e.attr("title","");
                e.removeAttr("title");
            }
        }
        String body=parse.body().toString();
        body=body.replace("<body>","").replace("</body>","");
        return body;
    }
}
