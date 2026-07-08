// handler/StringListTypeHandler.java
// MyBatis TypeHandler：将数据库 GROUP_CONCAT 返回的逗号分隔字符串 <-> List<String>
package com.kunshan.graduates.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringListTypeHandler implements TypeHandler<List<String>> {

    @Override
    public void setParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null || parameter.isEmpty()) {
            ps.setString(i, null);
        } else {
            ps.setString(i, String.join(",", parameter));
        }
    }

    @Override
    public List<String> getResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public List<String> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public List<String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private List<String> parse(String value) {
        if (value == null || value.isEmpty()) return Collections.emptyList();
        return new ArrayList<>(Arrays.asList(value.split(",")));
    }

    /** 将此 handler 注册到全局 TypeHandlerRegistry（可选，供检查用） */
    public static void registerTo(TypeHandlerRegistry registry) {
        registry.register(List.class, JdbcType.VARCHAR, new StringListTypeHandler());
    }
}
