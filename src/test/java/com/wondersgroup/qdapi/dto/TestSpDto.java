package com.wondersgroup.qdapi.dto;

import com.wondersgroup.framwork.dao.annotation.Callable;
import com.wondersgroup.framwork.dao.annotation.InOrOut;
import com.wondersgroup.framwork.dao.annotation.SpParamType;

@Callable(name = "SPK_TEST.sp_xxx")
public class TestSpDto {
    @InOrOut(type = SpParamType.IN ,order = 1)
    private Integer id;
    @InOrOut(type = SpParamType.IN ,order = 2)
    private String name;
    @InOrOut(type = SpParamType.Out ,order = 3)
    private Integer retCode;
    @InOrOut(type =SpParamType.Out ,order = 4)
    private String retMsg;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRetCode() {
        return retCode;
    }

    public void setRetCode(Integer retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }
}
