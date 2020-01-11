package com.hp.docker_base.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {


    /**
     * 读取修改时间的方法2
     */
    public static String getModifiedTime(File f){
        Calendar cal = Calendar.getInstance();
        long time = f.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTimeInMillis(time);
       // System.out.println("修改时间[2] " + formatter.format(cal.getTime()));
        return formatter.format(cal.getTime());
        //输出：修改时间[2]    2009-08-17 10:32:38
    }

    public static String Date2Str(){
        // 创建日期对象
        Date d = new Date();
        //给定输出格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //调用方法
        String dateDir = sdf.format(d);
        return  dateDir;
    }

    public static String Date2StrLong(){
        // 创建日期对象
        Date d = new Date();
        //给定输出格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        //调用方法
        String dateDir = sdf.format(d);
        return  dateDir;
    }

}
