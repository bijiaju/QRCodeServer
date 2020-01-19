package com.hp.docker_base.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
/**
 * 通用的工具类
 */
public class CommonUtil {

	/* public static void main(String[] args) {
	    	String imgFile = "C:\\Users\\bee\\Desktop\\test1\\2.png";//待处理的图片
	        String imgbese=getImgStr(imgFile);
	        System.out.println(imgbese.length());
	        System.out.println(imgbese);
	}*/
    /**
     * 设置返回JSON数据
     * @param returncode
     * @param returnmessage
     * @param retMap
     * @return Map<String, Object>
     */
    public static Map<String, Object> setReturnMap(String returncode, String returnmessage, Map<String, Object> retMap) {
        retMap.put("returnCode", returncode);
        retMap.put("returnMessage", returnmessage);
        return retMap;
    }
    
    /**
     * 将图片转换成Base64编码
     * @param imgFile 待处理图片
     * @return
     */
    public static String imgToBase64(String imgFile){
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
  
        
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try 
        {
            in = new FileInputStream(imgFile);        
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return new String(Base64.encodeBase64(data));
    }

   
}
