package com.wondersgroup.framwork.dao;

import com.wondersgroup.framwork.dao.bo.Page;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public interface CommonJdbcDao {
    public Connection getConnection();
    public  <T> List<T> queryList(String sql, Class<T> clazz, Object... arguments);
    public <T> void queryPageList(Page page, String sql, Class<T> clazz, Object ... arguments);
    public  <T> T queryFirst(String sql, Class<T> clazz, Object ... arguments);
    public long queryCount(String sql,Object...arguments);
    public <T> T queryObject(String sql,Class<T> clazz,Object...arguments);
    public Map<String,Object> queryMap(String sql, Object...arguments);
}
