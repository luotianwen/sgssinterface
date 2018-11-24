package com.sgss.www.conmon;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class Kuaidi {

    public static void main(String[] args) {
        System.out.println(getKuaiDiInfo("zhongtong","260034815902"));
    }
    public static String getKuaiDiInfo( String courier,String courierNumber) {
        Document jsondoc;
        String jsonData=null;
        try {
            jsondoc = Jsoup.connect("http://www.kuaidi100.com/query?type=" + courier + "&postid=" + courierNumber).timeout(10000).get();
             jsonData = jsondoc.text();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonData;
    }
}
