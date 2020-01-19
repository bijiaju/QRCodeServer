package com.hp.docker_base.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;


//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import com.hp.docker_base.util.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;
//import service.BufferedImageLuminanceSource;

@Service
public class QRServiceImpl {
	
	public static int processSum = 0;     //总处理数
	public static int weiweiCount = 0;   //付费解析
	public static int freeCount = 0;        //免费解析
	public static double saveFee = 0;           //计费
	
	/**
	 * 计算费用
	 * @return
	 */
	public static String calFee(){
		StringBuilder sb = new StringBuilder();
		sb.append("截止本次共解析生成二维码个数"+processSum+"个，");
		sb.append("付费个数"+weiweiCount+"个，");
		sb.append("免费个数"+freeCount+"个，");
		//sb.append("为您节省"+freeCount*0.001+"元");
		return sb.toString();
		
	}

	/**
	 *  二维码程序入口
	 * @param srcAddress
	 * @param desAddress
	 * @param width
	 * @param height
	 * @param error
	 * @param whiteEdge
	 * @return
	 */
	public  String transQR(String srcAddress,String desAddress,int width,int height,String error,int whiteEdge){
		long startTime=System.currentTimeMillis();
		String result = "";
		result+= transPNG(srcAddress,desAddress)+"我是分隔符哈哈哈";
		//2.执行二维码处理
		  ArrayList<String> lossInfos = new ArrayList<String>();//记录失败的个数
		  File[] files = new File(srcAddress).listFiles();
		  int sucNum = 0;
		  int losNum = 0;
		  for (int i = 0; i < files.length; i++)
	      {
			  String picName = files[i].getName();
			  if(picName.replaceAll("(.png)+","").length() != picName.length()){//是jpg图片替换成png
	        		  try {
	  					processQR(picName,desAddress,width,height,error,whiteEdge);
	  					 sucNum++;
	  				} catch (Exception e) {
	  					e.printStackTrace();
	  					lossInfos.add(picName);
	  					losNum++;
	  				}
	        	}
	      }	
     	   result+="提示:识别图片总数"+(sucNum+losNum)+"个，成功"+sucNum+"个，失败"+(losNum)+"个。 ";
     	
	        if(lossInfos.size()!=0){
	        	result+="识别失败图片为:";
	        	 for(int i=0;i<lossInfos.size();i++){
	        		 result+=lossInfos.get(i)+"、\n";
	        	 }
	        }
	        long endTime=System.currentTimeMillis();
	        float excTime=(float)(endTime-startTime)/1000;
	        result+="执行"+excTime+"秒";
	        //result+=calFee();
	        return result;
	}
	
	/**
	 * 判断图片是否已经解析过了
	 * false代表不用处理
	 * @return
	 */
	public static boolean isTransed(int width,int height,String error,int whiteEdge){
		if(CommonContants.IMG_WIDTH==width
				&& CommonContants.IMG_HEIGHT == height
				&&CommonContants.ERROR.equals(error)
				&&CommonContants.whiteEdge == whiteEdge){
			return false;//说明不需要处理了
		}
		return true;
		
	}
	
	/**
	 * 将解析和生成操作合并
	 * @return
	 * @throws Exception 
	 */
	public static void processQR(String fileName,String desAddress, int width,int height,String error,int whiteEdge) throws Exception{
		        String text = "";
		     	String format = "png";  //二维码图片的格
	        	String filePath = desAddress+"/"+fileName;//输入图片路径
	            text = parseQRCode(filePath);//免费(filePath);//解析二维码til
	        	if(StringUtils.isEmpty(text)||"null".equals(text)){
	        		text =decodeQrCode(filePath);//付费解析二维码til
	        		  System.out.println("执行付费解析图片:"+fileName);
	        		if(StringUtils.isEmpty(text)||"null".equals(text)){//抛出异常
	                    System.out.println("付费图片为解析内容失败的是:"+text);
	        			throw new Exception("付费图片为解析内容失败"); 
	        		}
	        		generateQRCode(text, width, height, format,fileName,desAddress,error,whiteEdge);
	        	
	        		//}
	        		/*System.out.println("text:-----------------:"+text);
	        		System.out.println("执行付费服务的图片是:"+fileName);
*/	        		
	        	}else{
	        		//System.out.println("text:-----------------:"+text);
	        		//生成二维码图片，并返回图片路径
	        		//if(isTransed(width, height, error, whiteEdge)){
	        		//	System.out.println("我生成图片了:-----------:");
	        			generateQRCode(text, width, height, format,fileName,desAddress,error,whiteEdge);
	        		//}
	        	}
	            processSum++;
	
	}
	
	
	  /**
     * 解析指定路径下的二维码图片
     *
     * @param filePath 二维码图片路径
     * @return
     */
    private static String parseQRCode(String filePath) {
        String content = "";
        try {
            File file = new File(filePath);
            BufferedImage image = ImageIO.read(file);
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            MultiFormatReader formatReader = new MultiFormatReader();
            Result result = formatReader.decode(binaryBitmap, hints);

          /*  System.out.println("result 为：" + result.toString());
            System.out.println("resultFormat 为：" + result.getBarcodeFormat());
            System.out.println("resultText 为：" + result.getText());*/
            //设置返回值
            content = result.getText();
            freeCount++;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
    /**
	 * 难点是以base6传输数据
	 * imgFile 传输的是I一个地址下的图片文件
	 */
	public static String decodeQrCode(String imgFile) {
		String host = "http://qrapi.market.alicloudapi.com";
	    String path = "/yunapi/qrdecode.html";
	    String method = "POST";
	    String appcode = CommonContants.AppCode;
	    Map<String, String> headers = new HashMap<String, String>();
	    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
	    headers.put("Authorization", "APPCODE " + appcode);
	    //根据API的要求，定义相对应的Content-Type
	    headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	    Map<String, String> querys = new HashMap<String, String>();
	    Map<String, String> bodys = new HashMap<String, String>();
	    bodys.put("imgurl", "");//因为互联网跨域问题为空
	    bodys.put("imgdata", "data:image/jpeg;base64,"+ CommonUtil.imgToBase64(imgFile));
	    bodys.put("version", "1.1");


	    try {
	    	/**
	    	* 重要提示如下:
	    	* HttpUtils请从
	    	* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
	    	* 下载
	    	*
	    	* 相应的依赖请参照
	    	* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
	    	*/
	    	HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
	    	//System.out.println(response.toString());
	    	//System.out.println("------------------------------------");
	    	//获取response的body
	    	//System.out.println(EntityUtils.toString(response.getEntity()));
	    	String result = EntityUtils.toString(response.getEntity());
	    	//System.out.println(result);
	    	JSONObject object = JSONObject.fromObject(result);
	    	String status = object.getString("status");
	    	// status = object.getString("status");
	    	weiweiCount++;
	    	if("1".equals(status)){
	    		String data = object.getString("data");
		        String raw_text = JSONObject.fromObject(data).getString("raw_text");

		        return raw_text;
	    	}
	    	return null;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }

	}


	
	/**
     * 根据内容，生成指定宽高、指定格式的二维码图片
     *
     * @param text   内容
     * @param width  宽
     * @param height 高
     * @param format 图片格式
     * @return 生成的二维码图片路径
	 * @throws IOException 
	 * @throws WriterException 
     * @throws Exception
     */
	public static  String generateQRCode(String text, int width, int height, String format,String fileName,String desAddress,String error,int whiteEdge) throws NotFoundException, IOException, WriterException {
		freeCount++;
		processSum++;
		Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
	      	 if("7%".equals(error)){
	        	 hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);//容错率
	        }else if("15%".equals(error)){
	        	hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);//容错率
	        }else if("25%".equals(error)){
	        	hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);//容错率
	        }else if("30%".equals(error)){
	        	hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);//容错率
	        }
	        hints.put(EncodeHintType.MARGIN, 1);//边距
	        BitMatrix bitMatrix1 = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
	        BitMatrix bitMatrix = updateBit(bitMatrix1, whiteEdge);//自定义边框，数字越小，边距越小
	        String pathName = desAddress+"/"+(fileName.substring(0,fileName.indexOf("."))+".png");
	     
	        File outputFile = new File(pathName);
	        MatrixToImageWriter.writeToFile(bitMatrix, format, outputFile);
	        return pathName;
    }
	
	/**
     * 解决自定义边距问题
     * @param matrix
     * @param margin
     * @return
     */
    @SuppressWarnings("unused")
    private static BitMatrix updateBit(BitMatrix matrix, int margin){
        int tempM = margin*2;
       int[] rec = matrix.getEnclosingRectangle();   //获取二维码图案的属性
            int resWidth = rec[2] + tempM;
            int resHeight = rec[3] + tempM;
            BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // 按照自定义边框生成新的BitMatrix
            resMatrix.clear();
        for(int i= margin; i < resWidth- margin; i++){   //循环，将二维码图案绘制到新的bitMatrix中
            for(int j=margin; j < resHeight-margin; j++){
                if(matrix.get(i-margin + rec[0], j-margin + rec[1])){
                    resMatrix.set(i,j);
                }
            }
        }
         return resMatrix;
    }
	public static String transPNG(String srcAddress, String desAddress) {
		String result ="";
		//long startTime=System.currentTimeMillis();
		int sucNum = 0;
		  int losNum = 0;
		 ArrayList<String> lossInfos = new ArrayList<String>();//记录失败的个数
		 File[] files = new File(srcAddress).listFiles();	 
		 for (int i = 0; i < files.length; i++)
		  {
			 try {
				  if(files[i].getName().replaceAll("(.jpg)+","").length() != files[i].getName().length()){//是jpg图片替换成png
					  convert(srcAddress+"/"+files[i].getName(), "png", desAddress+"/"+files[i].getName().substring(0, files[i].getName().indexOf("."))+".png");
					//  System.out.println(files[i].getName()+"替换文件，删除jpg");
					  files[i].delete();//删除图片
					  sucNum++;
				  }
		      } catch (Exception e) {
					e.printStackTrace();
					lossInfos.add(files[i].getName());
					losNum++;
				}
		 }
     	result+="提示:转换png图片总数"+(sucNum+losNum)+"个，成功"+sucNum+"个，失败"+(losNum)+"个\n";
	        if(lossInfos.size()!=0){
	        	result+="转换失败图片为：\n";
	        	 for(int i=0;i<lossInfos.size();i++){
	        		 result+=lossInfos.get(i)+"、\n";
	        	 }
	        }
	       // long endTime=System.currentTimeMillis();
	      //  float excTime=(float)(endTime-startTime)/1000;
	       // result+="执行时间："+excTime+"秒";
	        return result;		
	}

	 /**
    *
    * @param source 源图片路径
    * @param formatName 将要转换的图片格式
    * @param result 目标图片路径
	 * @throws Exception 
    */
   public static void convert(String source, String formatName, String result) throws Exception { 
   
           File f = new File(source); 
           f.canRead(); 
           BufferedImage src = ImageIO.read(f); 
           ImageIO.write(src, formatName, new File(result)); 
   } 

}
