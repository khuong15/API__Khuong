package com.bework.config;

import java.util.List;

public interface IUserDAO extends BaseDAO{
	public User getUser(String username, String countryCode);
	
	public List<String> getListClass(int id, String userType);
	
	public int updateUserStatus(int id, String status);
}
