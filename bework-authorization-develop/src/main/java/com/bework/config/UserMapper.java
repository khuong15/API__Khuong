package com.bework.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class UserMapper implements RowMapper<User> {
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User();
		user.setId(rs.getLong("id"));
		user.setPassword(rs.getString("password"));
		user.setUsername(rs.getString("username"));
		user.setType(rs.getString("type"));
		user.setCreatedAt(rs.getTimestamp("created_at"));
		user.setUpdatedAt(rs.getTimestamp("updated_at"));
		user.setRfrId(rs.getLong("rfr_id"));
		user.setStatus(rs.getString("status"));
		return user;
	}
}