package com.wondersgroup.qdapi.contrallor;

import com.alibaba.fastjson.JSON;
import com.wondersgroup.qdapi.dto.Ac01VO;
import com.wondersgroup.qdapi.service.CommonService;
import com.wondersgroup.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class AppContrallor {

    @Autowired
    private CommonService commonService;

    public CommonService getCommonService() {
        return commonService;
    }

    public void setCommonService(CommonService commonService) {
        this.commonService = commonService;
    }

    @RequestMapping("/app/queryPsnInfo")
    public String queryPsnInfo(String aac147, String aac003){
        Ac01VO ac01VO=this.commonService.queryPsnInfo(aac147,aac003);
        return JSON.toJSONString(ac01VO);
    }
    @RequestMapping("/app/receiveLog")
    public void receiveLog(HttpServletRequest request, MultipartFile file1) throws IOException {
        String name=request.getParameter("name");
        String age=request.getParameter("age");
        InputStream fileStream=request.getInputStream();
        FileUtils.uploadFiles(request,"D:/");
    }
}
