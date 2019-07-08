package com.wondersgroup.utils;

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
    private static String[] IEBrowserSignals = {"MSIE", "Trident", "Edge"};
    /**
     * 批量上传
     * @return
     */
    public static Map uploadFiles(HttpServletRequest request, String filePath){

        Map map =new HashMap();
        Map<String,MultipartFile> fileMap=null;
        MultipartHttpServletRequest multipartRequests = (MultipartHttpServletRequest) request; // 获取文件map集合
        fileMap = multipartRequests.getFileMap();
        if(fileMap==null||fileMap.size()==0){
            CommonsMultipartResolver multipartResolver=
                    new CommonsMultipartResolver(request.getSession().getServletContext());
            //将request变成多部分request
            if(multipartResolver.isMultipart(request)){
                MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(request);
                fileMap=multipartRequest.getFileMap();
            }
        }
        if(fileMap==null||fileMap.size()==0) return map;
        for(String key:fileMap.keySet()){
            MultipartFile file=fileMap.get(key);
            map=saveMultipartFile(file,filePath);
        }
        return map;
    }
    public  static  Map saveMultipartFile(MultipartFile file,String filePath){
        Map map =new HashMap();
        //当前时间文件名
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        if(file!=null)
        {
            String fileExt=getFileExt(file.getOriginalFilename());
            String fileName=sdf.format(new Date())+getRandm();
            String path=filePath+fileName+"."+fileExt;
            //上传
            try {
                file.transferTo(new File(path));
                map.put("filePath",path);
                map.put("fileExtName",fileExt);
                map.put("fileName",file.getOriginalFilename());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return  map;
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
    public static void downLoadFile(HttpServletRequest request, HttpServletResponse response, String filePath){
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
    public static void downLoadFile(HttpServletRequest request, HttpServletResponse response, String filePath, String fileName){
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
    public static void downLoadFileFromBase64(HttpServletRequest request, HttpServletResponse response, String fileStr, String fileName){
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
}
