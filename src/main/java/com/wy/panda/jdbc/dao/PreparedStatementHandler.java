package com.wy.panda.jdbc.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementHandler <T> {
    @Nullable
    T handle(PreparedStatement ps) throws SQLException, DataAccessException, IllegalArgumentException, IllegalAccessException, InstantiationException;
}
