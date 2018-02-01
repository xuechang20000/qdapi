package com.wondersgroup.qdapi.dao;

import com.wondersgroup.qdapi.dto.Ac01VO;

import java.util.List;

public interface CommonDao {
    public Ac01VO queryPsnInfo(String aac147, String aac003);
    public List<Ac01VO> queryPsnInfo(String aac147);
}
