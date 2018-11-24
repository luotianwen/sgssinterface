package com.sgss.www.conmon;

import com.jfinal.kit.StrKit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    static SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");//指定时间格式
    static SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");//指定时间格式
    static SimpleDateFormat sdfmmdd=new SimpleDateFormat("MM-dd");//指定时间格式
    static SimpleDateFormat sdfhh=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//指定时间格式
    static SimpleDateFormat mmdd=new SimpleDateFormat("yyyy/MM");//指定时间格式
    /**
     * MM/dd
     * 当前时间
     * @return
     */
    public static String FormatDateMMDD(){
        return mmdd.format(new Date());
    }
    /**
     *
     * yyyy-MM-dd
     * @param time
     * @return
     */
    public static String FormatDate2(Date time){
        if(null==time) {
            return "";
        }
        return sdf2.format(time);
    }
    /**
     * yyyy/MM/dd
     * @param time
     * @return
     */
    public static String FormatDate(Date time){
        if(null==time) {
            return "";
        }
        return sdf.format(time);
    }/**
     * MM-dd
     * @param time
     * @return
     */
    public static String FormatDateMMdd(Date time){
        if(null==time) {
            return "";
        }
        return sdfmmdd.format(time);
    }
    /**
     * MM-dd
     * @param time
     * @return
     */
    public static String FormatDateMMddHH(Date time){
        if(null==time) {
            return "";
        }
        return sdfhh.format(time);
    }
    /**
     * 由过去的某一时间,计算距离当前的时间
     * */
    public static String CalculateTime(String time){
        if(StrKit.isBlank(time)){
            return "";
        }
        long nowTime=System.currentTimeMillis();  //获取当前时间的毫秒数
        String msg="刚刚";
        Date setTime = null;  //指定时间
        try {
            setTime = sdfhh.parse(time);  //将字符串转换为指定的时间格式
        } catch (ParseException e) {

            e.printStackTrace();
        }
        long reset=setTime.getTime();   //获取指定时间的毫秒数
        long dateDiff=nowTime-reset;
        if(dateDiff>0) {
            long dateTemp1=dateDiff/1000; //秒
            long dateTemp2=dateTemp1/60; //分钟
            long dateTemp3=dateTemp2/60; //小时
            long dateTemp4=dateTemp3/24; //天数
            long dateTemp5=dateTemp4/30; //月数
            long dateTemp6=dateTemp5/12; //年数
            if(dateTemp6>0){
                msg = dateTemp6+"年前";
            }else if(dateTemp5>0){
                msg = dateTemp5+"个月前";
            }else if(dateTemp4>0){
                msg = dateTemp4+"天前";
            }else if(dateTemp3>0){
                msg = dateTemp3+"小时前";
            }else if(dateTemp2>0){
                msg = dateTemp2+"分钟前";
            }else if(dateTemp1>0){
                msg = "刚刚";
            }
        }
        return msg;

    }

    /**
     * 现在时间的几个小时后
     * @param hour
     * @return
     */
    public static String  addHour(int hour){
        Calendar now=Calendar.getInstance();
        now.add(Calendar.HOUR,hour);
        String dateStr=sdfhh.format(now.getTimeInMillis());
        return dateStr;
    }
    /**
     * 现在时间的多少天后
     * @param date
     * @return
     */
    public static String  addDate(int date){
        Calendar now=Calendar.getInstance();
        now.add(Calendar.DATE,date);
        String dateStr=sdfhh.format(now.getTimeInMillis());
        return dateStr;
    }
    /**
     * 现在时间的多少天后
     * @param year
     * @return
     */
    public static String  addYear(int year){
        Calendar now=Calendar.getInstance();
        now.add(Calendar.YEAR,year);
        String dateStr=sdfhh.format(now.getTimeInMillis());
        return dateStr;
    }
    /**
     * 现在时间的多少天前
     * @param date
     * @return
     */
    public static String  reduceDate(int date){
        Calendar now=Calendar.getInstance();
        now.add(Calendar.DATE,date);
        String dateStr=sdfhh.format(now.getTimeInMillis());
        return dateStr;
    }
    /**
     * 现在时间的多少天前
     * @param date
     * @return
     */
    public static String  reduceDateYYYYMMDD(int date){
        Calendar now=Calendar.getInstance();
        now.add(Calendar.DATE,-date);
        String dateStr=sdf2.format(now.getTimeInMillis());
        return dateStr;
    }
    /**
     * 当天10点
     * @return
     */
    public static String  get22(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String dateStr=sdfhh.format(calendar.getTimeInMillis());
        return dateStr;
    }
    public static boolean  compareDate(String date){

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date sd1=sdf.parse(date);
            Date sd2=new Date();
            return sd2.before(sd1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;

    }
    public static void main(String[] args) {
        System.out.println(get22());
            //Scanner input=new Scanner(System.in);
            //String time=input.nextLine();
            //String ss=DateUtil.CalculateTime("2018-05-24 14:10:9");
            //System.out.println(ss);

    }
}
