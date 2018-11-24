package com.sgss.www.conmon;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BigNumberFormat {
    public static DecimalFormat df   = new DecimalFormat("######0");
    public static DecimalFormat df1   = new DecimalFormat("######0.0");
    public static DecimalFormat df2   = new DecimalFormat("######0.00");
    public static String  dataFormat(int d){
        StringBuffer sb=new StringBuffer();

        if(d<10000)
        {
            sb.append(d+"");
        }
       /* else if(d<10000.0)
        {
            sb.append(d/1000.0).append("千");
        }*/
        else if(d<10000000)
        {

                sb.append(df.format(d / 10000.0)).append("万");

        }
        else if(d<100000000.0)
        {
            sb.append(df.format(d/10000000)).append("千万");
        }
        else
        {
            sb.append(df.format(d/100000000)).append("亿");
        }
            return sb.toString();
    }
    public static String  dataFormatTenThousand(int d){
        StringBuffer sb=new StringBuffer();


            sb.append(df2.format(d / 10000.0));


        return sb.toString();
    }

    /**
     * 格式化折扣保留一位小数
     * @param activityPrice 活动价格
     * @param integral 原价
     * @return
     */
    public static String  dataFormat1(int activityPrice,int integral){
        StringBuffer sb=new StringBuffer();


        sb.append(df1.format(activityPrice/(double)integral*10));


        return sb.toString();
    }
    public static String getAppCode() {

        int n = 6;
        StringBuilder code = new StringBuilder();
        Random ran = new Random();
        for (int i = 0; i < n; i++) {
            code.append(Integer.valueOf(ran.nextInt(10)).toString());
        }
        return code.toString();
    }
    public static boolean isPhone(String phone) {
        String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
        if (phone.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();
            return isMatch;
        }
    }
    public static void main(String[] args) {
        System.out.println(getAppCode());
       /* System.out.println("http://sssss".indexOf("http"));
        System.out.println("/sssss".indexOf("http"));
        System.out.println(dataFormatTenThousand(100+5));
        System.out.println(dataFormat(100*10));
        System.out.println(dataFormat(100*100));
        System.out.println(dataFormatTenThousand(100*1000+9));
        System.out.println(dataFormat(100*10000+9));
        System.out.println(dataFormat(100*100000+9));
        System.out.println(dataFormat(100*10000000+9));*/
    }
}
