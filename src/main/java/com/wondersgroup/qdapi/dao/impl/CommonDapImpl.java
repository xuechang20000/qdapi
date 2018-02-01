package com.wondersgroup.qdapi.dao.impl;

import com.wondersgroup.framwork.dao.CommonJdbcUtils;
import com.wondersgroup.qdapi.dao.CommonDao;
import com.wondersgroup.qdapi.dto.Ac01VO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommonDapImpl implements CommonDao {

    /***
     * 根据身份证号，姓名查询人员信息
     * @param aac147
     * @param aac003
     * @return
     */
    public Ac01VO queryPsnInfo(String aac147,String aac003){
        String sql="select * from ac01 where aac147=? and aac003=?";
        //List<Ac01VO> ac01VOs=
        return  CommonJdbcUtils.queryFirst(sql,Ac01VO.class,aac147,aac003);
        //return ac01VOs.get(0);
    }
    /***
     * 根据身份证号，姓名查询人员信息
     * @param aac147
     * @return
     */
    public List<Ac01VO> queryPsnInfo(String aac147){
        String sql="select * from ac01 where aac147=? ";
        //List<Ac01VO> ac01VOs=
        return  CommonJdbcUtils.queryList(sql,Ac01VO.class,aac147);
        //return ac01VOs.get(0);
    }
}
