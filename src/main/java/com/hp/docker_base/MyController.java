package com.hp.docker_base;

import com.hp.docker_base.bean.OrderDir;
import com.hp.docker_base.config.Contants;
import com.hp.docker_base.util.DateUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 * Created by kemp on 2018/8/15.
 */
@Controller
public class MyController {

    @Value("${serverPath}")
    public  String serverPath;

    private static Logger logger = LoggerFactory.getLogger(MyController.class);


    @RequestMapping("/index")
    public String index(Model model){
        return "index";
    }



    @RequestMapping("/deleteView")
    public String deleteView(Model model){
        String res = setDownDateList(model);
        return res;
    }



    @RequestMapping("/p2p")
    public String execSQL(Model model, @RequestParam(defaultValue = "")String name, String message){
        model.addAttribute("name",name);
        model.addAttribute("message",message);
        System.out.println(message);
        return "p2p";
    }


    /**
     * 删除文件
     * @return
     */
    @RequestMapping("/delete/{fileDir}")
    public String deleteFile(@PathVariable("fileDir") String fileDir,Model model) {
        File orderDir = new File(serverPath+"/"+DateUtil.Date2Str()+"/"+fileDir);
        deleteFile(orderDir);
        String downList = setDownList(model,null);
        return downList;
    }


    /**
     * 删除文件
     * @return
     */
    @RequestMapping("/deleteDirByDate/{fileDir}")
    public String deleteDirByDate(@PathVariable("fileDir") String fileDir,Model model) {
        File orderDir = new File(serverPath+"/"+fileDir);
        deleteFile(orderDir);
        String downList = setDownDateList(model);
        return downList;
    }




    /**
     * 查看当前文件夹下的图片
     * @param model
     * @return
     */
    @RequestMapping("/list/{fileDir}")
    public String list(@PathVariable("fileDir") String fileDir,Model model){
        File orderDirs11 = new File(serverPath+"/"+DateUtil.Date2Str()+"/"+fileDir);
        File[] pics = orderDirs11.listFiles();
        // 目录下，所有文件名
        ArrayList<String> pics1 = new ArrayList<String>();
        for(File file:pics){
            pics1.add(file.getName());
        }
        model.addAttribute("pics",pics1);
        model.addAttribute("fileDir",fileDir);
        return "list";
    }

    public static void deleteFile(File file){
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()){
            System.out.println("文件删除失败,请检查文件路径是否正确");
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f: files){
            //打印文件名
            String name = file.getName();
            System.out.println(name);
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()){
                deleteFile(f);
            }else {
                f.delete();
            }
        }
        //删除空文件夹  for循环已经把上一层节点的目录清空。
        file.delete();
    }
    /**
     * 压缩多个文件下载
     * @param response
     * @return
     */
    @RequestMapping("/dw/{fileDir}")
    public String downLoad1(@PathVariable("fileDir") String fileDir,HttpServletResponse response) throws Exception{

        try{
            String zipName = fileDir+".zip";
            response.setContentType("application/x-msdownload");
            response.setHeader("content-disposition", "attachment;filename="+ URLEncoder.encode(zipName, "utf-8"));

            File orderDir = new File(serverPath+"/"+DateUtil.Date2Str()+"/"+fileDir);
            File orderFiles[] = orderDir.listFiles();

            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());
            BufferedOutputStream bos = new BufferedOutputStream(zos);

            for(File file :orderFiles){

                System.out.println("源文件名： "+file.getName());

                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                String fileName = new String(file.getName().getBytes(),"UTF-8");///修复中文乱码
                System.out.println("下载文件名： "+fileName);
                zos.putNextEntry(new ZipEntry(fileName));

                //获取系统默认编码
                System.out.println("系统默认编码：" + System.getProperty("file.encoding")); //查询结果GBK
                //系统默认字符编码
                System.out.println("系统默认字符编码：" + Charset.defaultCharset()); //查询结果GBK
                //操作系统用户使用的语言
                System.out.println("系统默认语言：" + System.getProperty("user.language")); //查询结果zh


                int len = 0;
                byte[] buf = new byte[10 * 1024];
                while( (len=bis.read(buf, 0, buf.length)) != -1){
                    bos.write(buf, 0, len);
                }
                zos.setEncoding("UTF-8");
                bis.close();
                bos.flush();
            }
            bos.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 删除单个文件
     * @return
     */
    @RequestMapping("/delete/{fileDir}/{fileName}")
    public String deleteSingleFile(@PathVariable("fileDir") String fileDir,Model model,@PathVariable("fileName") String fileName) {
        File file111 = new File(serverPath+"/"+DateUtil.Date2Str()+"/"+fileDir+"/"+fileName);
        file111.delete();
        //String downList = setDownDateList(model);
        File orderDirs11 = new File(serverPath+"/"+DateUtil.Date2Str()+"/"+fileDir);
        File[] pics = orderDirs11.listFiles();
        // 目录下，所有文件名
        ArrayList<String> pics1 = new ArrayList<String>();
        for(File file:pics){
            pics1.add(file.getName());
        }
        model.addAttribute("pics",pics1);
        model.addAttribute("fileDir",fileDir);
        return "list";
    }

    /**
     * 从项目文件升级文件
     * @param response
     * @return
     */
    @RequestMapping("/dwsingle/{fileDir}/{fileName}")
    public String downLoad(@PathVariable("fileName") String fileName,HttpServletResponse response,@PathVariable("fileDir") String fileDir) throws Exception{
        File file = new File(serverPath+"/"+DateUtil.Date2Str()+"/"+fileDir+"/"+fileName);
        String filename = file.getName();
        try {
            filename = new String(filename.getBytes(), "ISO-8859-1");//为了解决中文下载乱码问题
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;fileName=" + filename);

        OutputStream os = response.getOutputStream();
        FileInputStream fis = new FileInputStream(file);
        IOUtils.copy(fis, os);
        return null;

    }


    /**
     * 从项目文件升级文件
     * @param response
     * @return
     */
    @RequestMapping("/download/{fileDir}")
    public String downLoad(@PathVariable("fileDir") String fileDir,HttpServletResponse response) throws Exception{
        // 目录
        File orderDir = new File(serverPath+"/"+DateUtil.Date2Str()+"/"+fileDir);
        File orderFiles[] = orderDir.listFiles();
        if (orderFiles != null){
            for(File file:orderFiles){
                if(file.exists()) { //判断文件父目录是否存在
                    //下面是通知浏览器以下载的形式，down下来
                    String filename = file.getName();
                    try {
                        filename = new String(filename.getBytes(), "ISO-8859-1");//为了解决中文下载乱码问题
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    response.setContentType("application/force-download");
                    response.setHeader("Content-Disposition", "attachment;fileName=" + filename);

                    OutputStream os = response.getOutputStream();
                    FileInputStream fis = new FileInputStream(file);
                    IOUtils.copy(fis, os);

                }
            }
        }

        return null;
    }

    /**
     * 下载单个文件
     * @return
     */
    @RequestMapping("/download/downLoadList")
    public String downLoadList(Model model) throws Exception{
        String res = setDownList(model,null);
        return res;
    }


    /**
     * 下载单个文件
     * @return
     */
    @RequestMapping("/listDate/{date}")
    public String listDate(@PathVariable("date") String date,Model model) throws Exception{
        String res = setDownList(model,date);
        return res;
    }



    /**
     *  返回下载列表view
     * @param model
     * @return
     */
    private String setDownDateList(Model model) {
        //1. 初始化map集合Map<包含唯一标记的文件名, 简短文件名>  ;
        ArrayList<String> dateDir = new ArrayList<String>();
        // 目录
        File serverDir = new File(serverPath);
        // 目录下，所有文件名
        File[] filesDir = serverDir.listFiles();//二级目录
        for(File fileDir :filesDir){
            System.out.println(fileDir.getName());//D:\All_Files\testFile\阿斯顿发
            dateDir.add(fileDir.getName());
        }
        model.addAttribute("dateDir",dateDir);

        return "downDateList";
    }

    /**
     *  返回下载列表view
     * @param model
     * @return
     */
    private String setDownList(Model model,String date) {
        //1. 初始化map集合Map<包含唯一标记的文件名, 简短文件名>  ;
        ArrayList<OrderDir> orderDir = new ArrayList<OrderDir>();

        File serverDir = null;
        if(StringUtils.isEmpty(date)){
            serverDir = new File(serverPath+"/"+DateUtil.Date2Str());
        }else{
            serverDir = new File(serverPath+"/"+date);
        }
        // 目录
        File datedest = null;
        if(StringUtils.isEmpty(date)){
            datedest = new File(serverPath + "/" + DateUtil.Date2Str()+"/"+"xxxx");
        }else{
            datedest = new File(serverPath + "/" + DateUtil.Date2Str()+"/"+"xxxx");
        }
      //  File datedest = new File(serverPath + "/" + DateUtil.Date2Str()+"/"+"xxxx");
        if(!datedest.getParentFile().exists()){ //判断文件父目录是否存在
            datedest.getParentFile().mkdir();
        }
        // 目录下，所有文件名
        File[] filesDir = serverDir.listFiles();//二级目录
        for(File fileDir :filesDir){
            System.out.println(fileDir.getName());//D:\All_Files\testFile\阿斯顿发
            OrderDir order = new OrderDir();
            order.setOrderName(fileDir.getName());
            order.setOrderDate(DateUtil.getModifiedTime(fileDir));
            orderDir.add(order);
        }
        model.addAttribute("orderDir",orderDir);
        model.addAttribute("date",DateUtil.Date2Str());
        return "downList";
    }

    /**
     * 实现多文件上传
     * */
    @RequestMapping(value="multifileUpload",method= RequestMethod.POST)
    public @ResponseBody
    String multifileUpload(@RequestParam(defaultValue = "")String orderId,String remark,HttpServletRequest request,Model model) throws UnsupportedEncodingException {

        //判断订单Id

        List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles("fileName");
        if(files.isEmpty()){
            return "false";
        }

        for(MultipartFile file:files){
            String fileName = new String(file.getOriginalFilename().getBytes(),"UTF-8");///修复中文乱码
            int size = (int) file.getSize();
            System.out.println(fileName + "-->" + size);

            if(file.isEmpty()){
                return "false";
            }else{
                File dest = new File(serverPath + "/" + DateUtil.Date2Str() +"/"+orderId+"/"+ fileName);
                File datedest = new File(serverPath + "/" + DateUtil.Date2Str()+"/"+orderId);
                if(!datedest.getParentFile().exists()){ //判断文件父目录是否存在
                    datedest.getParentFile().mkdir();
                }
                if(!dest.getParentFile().exists()){ //判断文件父目录是否存在
                    dest.getParentFile().mkdir();
                }
                try {
                    file.transferTo(dest);
                }catch (Exception e) {
                    e.printStackTrace();
                    return "false";
                }
            }
        }
        return "true";
    }

}