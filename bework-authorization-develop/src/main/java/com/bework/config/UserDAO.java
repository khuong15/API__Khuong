package com.bework.config;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bework.common.Consts;

public class UserDAO implements IUserDAO {

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public User getUser(String username, String countryCode) {
		StringBuilder sqlBuider = new StringBuilder();
		sqlBuider.append("SELECT * FROM users WHERE username =:username AND country_code =:countryCode");
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("username", username);
		parameters.addValue("countryCode", countryCode);
		List<User> listUser = namedParameterJdbcTemplate.query(sqlBuider.toString(), parameters,
				new UserMapper());
		if (listUser != null && listUser.size() > 0) {
			for (User user : listUser) {
				if(Consts.USER_STATUS_ACTIVE.equals(user.getStatus())) {
					return user;
				}
			}
			return listUser.get(0);
		}
		return null;
	}

	@Override
	public List<String> getListClass(int id, String userType) {
		String sql = "select id from classes where created_by =:id";
		
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("id", id);
		
		List<String> listClassOwner = namedParameterJdbcTemplate.query(sql, parameters, new ClassIdMapper());
		
		parameters.addValue("userType", userType);
		
//		sql = "select class_id AS id from user_class where user_id =:id AND user_type =:userType AND role in('CO_TEACHER', 'OWNER')";
		
		sql = "select distinct(class_id) AS id from user_class where user_id =:id";
		
		List<String> listClass = namedParameterJdbcTemplate.query(sql, parameters, new ClassIdMapper());
		
		listClassOwner.addAll(listClass);
		
		return listClassOwner;
	}

	@Override
	public int updateUserStatus(int id, String status) {
		String sql = "update users set status =:status where id =:id";
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("id", id);
		parameters.addValue("status", status);
		
		return namedParameterJdbcTemplate.update(sql, parameters);
	}
}
