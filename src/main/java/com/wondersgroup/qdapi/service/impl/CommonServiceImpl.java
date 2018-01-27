package com.wondersgroup.qdapi.service.impl;

import com.wondersgroup.qdapi.dao.CommonDao;
import com.wondersgroup.qdapi.dto.Ac01VO;
import com.wondersgroup.qdapi.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl implements CommonService {
    @Autowired
    private CommonDao commonDao;

    public CommonDao getCommonDao() {
        return commonDao;
    }

    public void setCommonDao(CommonDao commonDao) {
        this.commonDao = commonDao;
    }

    /***
     * 根据身份证号，姓名查询人员信息
     * @param aac147
     * @param aac003
     * @return
     */
    public Ac01VO queryPsnInfo(String aac147, String aac003){
        return this.commonDao.queryPsnInfo(aac147,aac003);
    }
}
