package com.wondersgroup.framwork.dao.utils;

import org.springframework.jdbc.core.PreparedStatementCreator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MyPreparedStatementCreator implements PreparedStatementCreator {
    @Override
    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        return null;
    }
}
