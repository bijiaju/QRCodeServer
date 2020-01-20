package com.hp.docker_base;

import com.hp.docker_base.bean.OrderDir;
import com.hp.docker_base.bean.User;
import com.hp.docker_base.service.OrderService;
import com.hp.docker_base.service.QRServiceImpl;
import com.hp.docker_base.service.UserService;
import com.hp.docker_base.util.DateUtil;
import com.hp.docker_base.util.ToolUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;


import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by kemp on 2018/8/15.
 */
@Controller
public class MyController {

    @Value("${serverPath}")
    public  String serverPath;

    @Autowired
    private QRServiceImpl qrService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    private static Logger logger = LoggerFactory.getLogger(MyController.class);



    @RequestMapping("/test")
    @ResponseBody
    public List<OrderDir> test(Model model){

        return orderService.selectAll();
    }


    @RequestMapping("/index")
    public String index(Model model){
        return "index";
    }

    @RequestMapping("/login")
    public String login(Model model){
        return "login";
    }

    @RequestMapping("/canlogin")
    public String  canlogin(Model model, @RequestParam(value = "zhanghao") String name, @RequestParam(value = "pw") String password, HttpServletResponse servletResponse,HttpServletRequest request){

        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);
        if(StringUtils.isEmpty(name)||StringUtils.isEmpty(password)){
           // return "账号或密码不能为空";
        }
        User user = userService.selectUserByNameAndPassword(name, password);
        if(user==null){
          //  return "账号密码或错误请重试";
        }else{

            HttpSession session = request.getSession();
            session.setAttribute("userName",name);
            return setDownList(model,DateUtil.Date2Str(),null,null);
        }
        return "";

    }

    @RequestMapping("/register")
    public String register(Model model){
        return "register";
    }

    @RequestMapping("/loginout")
    public String loginout(Model model,HttpServletRequest request){
        HttpSession session=request.getSession();
        session.removeAttribute("userName");
        session.invalidate();
        return "login";
    }

    @RequestMapping("/upload")
    //@ResponseBody
    public String index2(Model model){
        return "upload";
    }

    @RequestMapping("/successUP")
    public String successUP(Model model){
        return "successUP";
    }

    @RequestMapping("/deleteView")
    public String deleteView(Model model){
        String res = setDownDateList(model);
        return res;
    }




    /**
     * 删除文件夹文件
     * @return
     */
    @RequestMapping("/deleteDir/{date}/{fileDir}")
    public String deleteDir(@PathVariable("date") String date,@PathVariable("fileDir") String fileDir,Model model) {
        if(StringUtils.isEmpty(date)){
            date = DateUtil.Date2Str();
        }
        File orderDir = new File(serverPath+"/"+date+"/"+fileDir);
        deleteFile(orderDir);
        String downList = setDownList(model,date,null,null);
        return downList;
    }

    /**
     * 查询文件
     * @return
     */
    @RequestMapping("/selectDir/{date}/{search_value}")
    public String selectDir(@PathVariable("date") String date,@PathVariable("search_value") String search_value,Model model){
        String res = setDownList(model,date,search_value,null);
        return res;
    }

    /**
     * 删除文件
     * @return
     */
    @RequestMapping("/selectDir/{date}/")
    public String selectDir1(@PathVariable("date") String date,Model model){
        String res = setDownList(model,date,null,null);
        return res;
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
    @RequestMapping("/list/{date}/{fileDir}")
    public String list(@PathVariable("date") String date,@PathVariable("fileDir") String fileDir,Model model){
        if(StringUtils.isEmpty(date)){
             date = DateUtil.Date2Str();
        }
        File orderDirs11 = new File(serverPath+"/"+date+"/"+fileDir);
        File[] pics = orderDirs11.listFiles();
        // 目录下，所有文件名
        ArrayList<String> pics1 = new ArrayList<String>();
        for(File file:pics){
            pics1.add(file.getName());
        }
        model.addAttribute("pics",pics1);
        model.addAttribute("fileDir",fileDir);
        model.addAttribute("date",date);
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
    @RequestMapping("/dw/{date}/{fileDir}")
    public String downLoad1(@PathVariable("date") String date,@PathVariable("fileDir") String fileDir,HttpServletResponse response,Model model,HttpServletRequest request) throws Exception{

        try{
            String zipName = fileDir+".zip";
            response.setContentType("application/x-msdownload");
            response.setHeader("content-disposition", "attachment;filename="+ URLEncoder.encode(zipName, "utf-8"));

            if(StringUtils.isEmpty(date)){
                date = DateUtil.Date2Str();
            }
            File orderDir = new File(serverPath+"/"+date+"/"+fileDir);
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
        }finally {
            HttpSession session=request.getSession();
            OrderDir orderDir = new OrderDir();
            orderDir.setOrderName(fileDir);
            String updateUser = (String) session.getAttribute("userName");
            orderDir.setUpdateUser(updateUser);
            orderDir.setDwState("已经处理");

            orderService.updateOrderByOrderId(orderDir);
            orderService.updateOrderByOrderId(orderDir);
            return null;
          //  return setDownList(model,date,null,fileDir);
        }

       /* OrderDir orderDir = new OrderDir();
        orderDir.setOrderName(fileDir);
        orderDir.setDwState("已经处理");
        orderService.updateOrderByOrderId(orderDir);
        return setDownList(model,date,null,fileDir);*/
    }


    /**
     * 删除单个文件
     * @return
     */
    @RequestMapping("/deleteFile/{date}/{fileDir}/{fileName}")
    public String deleteSingleFile(@PathVariable("date") String date,@PathVariable("fileDir") String fileDir,Model model,@PathVariable("fileName") String fileName) {
        if(StringUtils.isEmpty(date)){
            date = DateUtil.Date2Str();
        }
        File file111 = new File(serverPath+"/"+date+"/"+fileDir+"/"+fileName);
        file111.delete();
        //String downList = setDownDateList(model);
        File orderDirs11 = new File(serverPath+"/"+date+"/"+fileDir);
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
    @RequestMapping("/dwsingle/{date}/{fileDir}/{fileName}")
    public String downLoad(@PathVariable("date") String date,@PathVariable("fileName") String fileName,HttpServletResponse response,@PathVariable("fileDir") String fileDir) throws Exception{
        if(StringUtils.isEmpty(date)){
            date = DateUtil.Date2Str();
        }
        File file = new File(serverPath+"/"+date+"/"+fileDir+"/"+fileName);
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
        String res = setDownList(model,null,null,null);
        return res;
    }


    /**
     * 下载单个文件
     * @return
     */
    @RequestMapping("/listDate/{date}")
    public String listDate(@PathVariable("date") String date,Model model) throws Exception{
        String res = setDownList(model,date,null,null);
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
    private String setDownList(Model model,String date,String searchValue,String orderId) {
        //1. 初始化map集合Map<包含唯一标记的文件名, 简短文件名>  ;
        ArrayList<OrderDir> orderDir = new ArrayList<OrderDir>();
        if(StringUtils.isEmpty(date)){
            date = DateUtil.Date2Str();
        }
        File serverDir = new File(serverPath+"/"+date);
        if (!serverDir.exists()) {
            serverDir.mkdir();
        }
        // 目录下，所有文件名
        File[] filesDir = serverDir.listFiles();//二级目录
        for(File fileDir :filesDir){
            System.out.println(fileDir.getName());//D:\All_Files\testFile\阿斯顿发
            if(StringUtils.isEmpty(searchValue)){//查询全部
                OrderDir order = new OrderDir();
                order.setOrderName(fileDir.getName());
                order.setOrderDate(DateUtil.getModifiedTime(fileDir));

                OrderDir tmp = orderService.selectOrderByOrderId(fileDir.getName());
                if(tmp==null){
                    order.setDwState("未处理");
                    order.setUpdateUser("无");
                    order.setOrderType("无套餐");
                }else{
                    System.out.println(fileDir.getName()+"----"+tmp.getDwState());
                    order.setDwState(tmp.getDwState());
                    order.setUpdateUser(tmp.getUpdateUser());
                    order.setOrderType(tmp.getOrderType());
                }
                orderDir.add(order);
            }else{
                if(searchValue.equals(fileDir.getName())){
                    OrderDir order = new OrderDir();
                    order.setOrderName(fileDir.getName());
                    order.setOrderDate(DateUtil.getModifiedTime(fileDir));
                    orderDir.add(order);
                    OrderDir tmp = orderService.selectOrderByOrderId(fileDir.getName());

                    if(tmp==null){
                        order.setDwState("未处理");
                        order.setUpdateUser("无");
                        order.setOrderType("无套餐");
                    }else{
                        System.out.println(fileDir.getName()+"----"+tmp.getDwState());
                        order.setDwState(tmp.getDwState());
                        order.setUpdateUser(tmp.getUpdateUser());
                        order.setOrderType(tmp.getOrderType());
                    }
                    break;
                }
            }

        }
        model.addAttribute("orderDir",orderDir);
        if(StringUtils.isEmpty(date)){
            model.addAttribute("date",DateUtil.Date2Str());
        }else{
            model.addAttribute("date",date);
        }

        return "downList";
    }

    /**
     * 实现多文件上传
     * */
    @RequestMapping(value="multifileUpload",method= RequestMethod.POST)
    @ResponseBody
    public  String multifileUpload(@RequestParam(defaultValue = "")String orderId,String remark,HttpServletRequest request,Model model) throws UnsupportedEncodingException {
        if(ToolUtil.isTaobaoOrder(orderId)){
            List<MultipartFile> files = new ArrayList<>();
            //判断订单Id
            List<MultipartFile> file1 = ((MultipartHttpServletRequest)request).getFiles("file1");
            if(file1.isEmpty()){
                return "您没有选择必传文件，请选择后再提交";
            }
            List<MultipartFile> file2 = ((MultipartHttpServletRequest)request).getFiles("file2");
            List<MultipartFile> file3 = ((MultipartHttpServletRequest)request).getFiles("file3");
            files.addAll(file1);
            files.addAll(file2);
            files.addAll(file3);

            if(files.isEmpty()){
                return "您没有上传文件";
            }

            for(MultipartFile file:files){
                String fileName = new String(file.getOriginalFilename().getBytes(),"UTF-8");///修复中文乱码
                int size = (int) file.getSize();
                System.out.println(fileName + "-->" + size);

                if(file.isEmpty()){
                    return "successUP";
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
                        qrService.transQR(serverPath + "/" + DateUtil.Date2Str()+"/"+orderId,serverPath + "/" + DateUtil.Date2Str()+"/"+orderId,400,400,"25%",10);
                        //插入数据库记录
                        OrderDir orderDir1 = orderService.selectOrderByOrderId(orderId);
                        if(orderDir1==null){
                            insertOrder(orderId);
                        }else {
                            orderService.delOrderInfo(orderId);
                            insertOrder(orderId);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                        return "文件上传错误，请重新上传";
                    }
                }
            }
            return "文件上传成功";
        }else{
            return "请输入正确订单号";
        }

    }


    /**
     * 实现多文件上传
     * */
    @RequestMapping(value="upload",method= RequestMethod.POST)
    @ResponseBody
    public  String Upload2(@RequestParam(defaultValue = "")String orderId,HttpServletRequest request) throws UnsupportedEncodingException {
        //判断订单Id
        if(ToolUtil.isTaobaoOrder(orderId)){
            List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles("file1");
            if(files.isEmpty()){
                return "您没有上传文件";
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
                        qrService.transQR(serverPath + "/" + DateUtil.Date2Str()+"/"+orderId,serverPath + "/" + DateUtil.Date2Str()+"/"+orderId,400,400,"25%",10);

                        //插入数据库记录
                        OrderDir orderDir1 = orderService.selectOrderByOrderId(orderId);
                        if(orderDir1==null){
                           insertOrder(orderId);
                        }else {
                            orderService.delOrderInfo(orderId);
                            insertOrder(orderId);
                        }


                    }catch (Exception e) {
                        e.printStackTrace();
                        return "文件上传错误，请重新上传";
                    }
                }
            }
            return "文件上传成功";
        }else {
            return "请输入正确订单号";
        }

    }


    private int insertOrder(String orderId){
        OrderDir orderDir = new OrderDir();
        orderDir.setId(UUID.randomUUID().toString());
        orderDir.setOrderDate(DateUtil.Date2StrLong());
        orderDir.setOrderName(orderId);
        orderDir.setDwState("未处理");
        orderDir.setOrderType("套餐一");
        orderDir.setUpdateUser("无处理人");
        return orderService.insertOrder(orderDir);
    }
}