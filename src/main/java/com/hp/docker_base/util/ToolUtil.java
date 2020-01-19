package com.hp.docker_base.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolUtil {

    /**
     * 判断是否是淘宝订单
     * @return
     */
    public static boolean isTaobaoOrder(String orderId){
        String pat1 = "^\\d{18}$";
        Pattern pattern1 = Pattern.compile(pat1);
        Matcher match1 = pattern1.matcher(orderId);
        boolean isMatch1 = match1.matches();
        if(isMatch1){
            return true;
        }
        return false;
    }





    public static void main(String [] args){
        System.out.println(isTaobaoOrder("757087010213722311"));
    }
}
