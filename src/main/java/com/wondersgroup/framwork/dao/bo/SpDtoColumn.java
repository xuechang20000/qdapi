package com.wondersgroup.framwork.dao.bo;

import com.wondersgroup.framwork.dao.annotation.SpParamType;

public class SpDtoColumn<T> {
private int order;//顺序
private String name;//属性名
private SpParamType inOrOut;//出或入参
private Class<T> type;//属性类型
    private String spName;//过程名称
private  Object value;//属性值

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getSpName() {
        return spName;
    }

    public void setSpName(String spName) {
        this.spName = spName;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpParamType getInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(SpParamType inOrOut) {
        this.inOrOut = inOrOut;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }
}
