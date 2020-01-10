package com.hp.docker_base.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {

    public static String Date2Str(){
        // 创建日期对象
        Date d = new Date();
        //给定输出格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //调用方法
        String dateDir = sdf.format(d);
        return  dateDir;
    }
}
