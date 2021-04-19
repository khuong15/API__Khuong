package com.bework.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ClassIdMapper implements RowMapper<String> {
	public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		String id = new String();
		id = rs.getString("id");
		return id;
	}
}