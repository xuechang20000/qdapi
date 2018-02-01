package com.wondersgroup.framwork.dao.mapper;

import org.springframework.jdbc.core.CallableStatementCreator;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class SimpleCallableStatementCreator implements CallableStatementCreator {
    @Override
    public CallableStatement createCallableStatement(Connection connection) throws SQLException {
        return null;
    }
}
