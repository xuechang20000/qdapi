package com.wondersgroup.framwork.dao.utils;


import com.wondersgroup.framwork.dao.bo.DataBaseType;

public class SqlPageUtils  {

    /**
     * 语句添加分页
     * @param sql
     * @param dbType
     * @return
     */
    public static String handlerPagingSQL(String sql, String dbType) {
        if (null==sql||sql.trim().length()==0) return null;
        if(dbType.equalsIgnoreCase( DataBaseType.ORACLE)) return handlerOraclePaginSql(sql);
        return sql+" limit ?,?";
    }

    /**
     * oracle添加分页
     * @param sql
     * @return
     */
    public static String handlerOraclePaginSql(String sql){
        StringBuffer pagingSelect = new StringBuffer();
        pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        pagingSelect.append(sql);
        pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
        return pagingSelect.toString();
    }

    /**
     * 查询总数sql
     * @param sql
     * @param dbtype
     * @return
     */
    public static String getCountSql(String sql,String dbtype){
        sql=sql.toUpperCase();
        StringBuffer result = new StringBuffer();
        int opos = sql.indexOf("ORDER BY");
        if(opos!=-1){
            sql=sql.substring(0, opos);
        }
        result.append("SELECT COUNT('1') FROM (").append(sql).append(")");

        return result.toString();
    }
}
