package com.xuechen.web.utils;


import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import sun.misc.BASE64Decoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileUtils {

     private  static String filePath_mail;
     private  static String filePath_head;
     private  static String filePath_collect;
    private  static String filePath_licence;
    private  static String filePath_spec;
    private  static String filePath_school;
    private static String[] IEBrowserSignals = {"MSIE", "Trident", "Edge"};
     public FileUtils(){
     }
     public FileUtils(String filePath_collect,String filePath_head,String filePath_mail,String filePath_licence,String filePath_spec,String filePath_school){
         this.filePath_collect=filePath_collect;
         this.filePath_head=filePath_head;
         this.filePath_mail=filePath_mail;
         this.filePath_licence=filePath_licence;
         this.filePath_spec=filePath_spec;
         this.filePath_school=filePath_school;
     }

    /**
     * 根据上传类型上传
     * @param request
     * @param uploadType
     * @return
     */
     public static Map uploadFilesByType(HttpServletRequest request, String uploadType){
         if ("MAIL".equals(uploadType)){
             return uploadFiles(request,filePath_mail);
         }
         if ("COLL".equals(uploadType)){
             return uploadFiles(request,filePath_collect);
         }
         if ("HEAD".equals(uploadType)){
             return uploadFiles(request,filePath_head);
         }
         if ("LICE".equals(uploadType)){
             return uploadFiles(request,filePath_licence);
         }
         if ("SPEC".equals(uploadType)){
             return uploadFiles(request,filePath_spec);
         }
         if ("SCHOOL".equals(uploadType)){
             return uploadFiles(request,filePath_school);
         }
         return  null;
     }
    /**
     * 批量上传
     * @return
     */
    public static Map uploadFiles(HttpServletRequest request, String filePath){
        //ShiroHttpServletRequest shiroRequest = (ShiroHttpServletRequest) request;
        Map map =new HashMap();
        //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver=
                new CommonsMultipartResolver(request.getSession().getServletContext());

        //当前时间文件名
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSSS");
        if(multipartResolver.isMultipart(request))
        {
            //将request变成多部分request
            MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(request);
            //获取multiRequest 中所有的文件名
            Iterator iter=multipartRequest.getFileNames();

            while(iter.hasNext())
            {
                //一次遍历所有文件
                MultipartFile file=multipartRequest.getFile(iter.next().toString());
                if(file!=null)
                {
                    String fileExt=getFileExt(file.getOriginalFilename());
                    String fileName=sdf.format(new Date())+getRandm();
                    String path=filePath+fileName+"."+fileExt;
                    String thumbPath=filePath+"thumb_"+fileName+"."+fileExt;
                    //上传
                    try {
                        file.transferTo(new File(path));

                        if (path.indexOf("CollectAttachment")>=0||path.indexOf("LicenceAttachment")>=0){//如果是采样点\营业执照片
                            //压缩图片
                            Thumbnails.of(new File(path)).size(800,600).toFile(thumbPath);
                            map.put("filePath",thumbPath);
                        }else{
                            map.put("filePath",path);
                        }
                        map.put("fileExtName",fileExt);
                        map.put("fileName",file.getOriginalFilename());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

            }

        }
        return map;
    }
    public static List<MultipartFile> getMutipartFileFromRequest(HttpServletRequest request){
        List<MultipartFile> multipartFiles =new ArrayList<MultipartFile>();
        //将当前上下文初始化给  CommonsMutipartResolver （多部分解析器）
        CommonsMultipartResolver multipartResolver=
                new CommonsMultipartResolver(request.getSession().getServletContext());

        //当前时间文件名
        if(multipartResolver.isMultipart(request))
        {
            //将request变成多部分request
            MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(request);
            //获取multiRequest 中所有的文件名
            Iterator iter=multipartRequest.getFileNames();

            while(iter.hasNext())
            {
                //一次遍历所有文件
                MultipartFile file=multipartRequest.getFile(iter.next().toString());
                multipartFiles.add(file);
            }

        }
        return multipartFiles;
    }
    public static void downLoadFile(HttpServletRequest request,HttpServletResponse response,String filePath){
        File file=new File(filePath);
        InputStream inputStream;
        try {
            //设置文件MIME类型
            response.setContentType(request.getSession().getServletContext().getMimeType(filePath));
            //设置Content-Disposition
            String fileName=file.getName();
            response.setHeader("Content-Disposition", "attachment;filename="+fileName);
            inputStream=new BufferedInputStream(new FileInputStream(file));
            OutputStream os= new BufferedOutputStream(response.getOutputStream());
            //写文件
            int b;
            while((b=inputStream.read())!= -1)
            {
                os.write(b);
            }
            os.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void downLoadFile(HttpServletRequest request,HttpServletResponse response,String filePath,String fileName){
        File file=new File(filePath);
        InputStream inputStream;
        try {
            //设置文件MIME类型
            response.setContentType(request.getSession().getServletContext().getMimeType(filePath));
            //设置Content-Disposition
            response.setCharacterEncoding("UTF-8");
            boolean isMSIE = isMSBrowser(request);
            if (isMSIE) {
                //IE浏览器的乱码问题解决
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {
                //万能乱码问题解决
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
            response.setHeader("Content-Disposition", "attachment;filename="+fileName);
            inputStream=new BufferedInputStream(new FileInputStream(file));
            OutputStream os= new BufferedOutputStream(response.getOutputStream());
            //写文件
            int b;
            while((b=inputStream.read())!= -1)
            {
                os.write(b);
            }
            os.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void downLoadFileFromBase64(HttpServletRequest request,HttpServletResponse response,String fileStr,String fileName){
        if (fileStr==null) return;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            //设置文件MIME类型
            response.setContentType(request.getSession().getServletContext().getMimeType(fileName));
            //设置Content-Disposition
            response.setHeader("Content-Disposition", "attachment;filename="+fileName);
            OutputStream os= new BufferedOutputStream(response.getOutputStream());
            //写文件
            byte[] bs = decoder.decodeBuffer(fileStr);
            os.write(bs);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileExt(String fileName){
        if (fileName!=null&&fileName.lastIndexOf(".")>0){
            return fileName.substring(fileName.lastIndexOf(".")+1);
        }
        return "";
    }
    public static int getRandm(){
        Random random=new Random();
        return  random.nextInt(1000);
    }


    public static boolean isMSBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        for (String signal : IEBrowserSignals) {
            if (userAgent.contains(signal))
                return true;
        }
        return false;
    }
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
        //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    /**
     * 转移文件
     * @param sourceFile
     * @param targetDir
     * @param targetFileName
     * @return
     */
    public static int moveFile(String sourceFile,String targetDir,String targetFileName)  {
        File file=new File(targetDir);
        if (!file.exists()) file.mkdir();

        file=new File(sourceFile);
        if (!file.exists()) return 0;

        try {
            FileInputStream fileInputStream=new FileInputStream(file);
            FileOutputStream fileOutputStream=new FileOutputStream(targetDir+"/"+targetFileName);
            int i=0;
            while((i=fileInputStream.read())!=-1){
                fileOutputStream.write(i);
            }
            fileInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }
    public static String getFilePath_mail() {
        return filePath_mail;
    }

    public static void setFilePath_mail(String filePath_mail) {
        FileUtils.filePath_mail = filePath_mail;
    }

    public static String getFilePath_head() {
        return filePath_head;
    }

    public static void setFilePath_head(String filePath_head) {
        FileUtils.filePath_head = filePath_head;
    }

    public static String getFilePath_collect() {
        return filePath_collect;
    }

    public static void setFilePath_collect(String filePath_collect) {
        FileUtils.filePath_collect = filePath_collect;
    }
    public static String getFilePath_licence() {
        return filePath_licence;
    }

    public static void setFilePath_licence(String filePath_licence) {
        FileUtils.filePath_licence = filePath_licence;
    }
    public static String getFilePath_spec() {
        return filePath_spec;
    }

    public static void setFilePath_spec(String filePath_spec) {
        FileUtils.filePath_spec = filePath_spec;
    }
}
