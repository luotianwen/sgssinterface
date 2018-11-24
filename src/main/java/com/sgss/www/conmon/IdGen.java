package com.sgss.www.conmon;

import java.util.Random;
import java.util.UUID;

/**
 * @author martins
 */
public class IdGen {
    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    public static String getOrderIdByUUId() {
        int machineId = 1;//最大支持1-9个集群机器部署
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if(hashCodeV < 0) {//有可能是负数
            hashCodeV = - hashCodeV;
        }
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        return machineId + String.format("%010d", hashCodeV);
    }
    public static void main(String[] args) {
          int totalMoney=10000;
          int number=3;
          int money=0;
        money=totalMoney/number;
        Random rand = new Random();
        money=rand.nextInt(money) + 1;

        System.out.println(money);


    }
}
