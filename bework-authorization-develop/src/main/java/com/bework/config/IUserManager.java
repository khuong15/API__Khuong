package com.bework.config;

import java.util.List;

public interface IUserManager extends IBaseManager{
	public User getUser(String username, String countryCode);
	
	public List<String> getListClass(int id, String userType);
}
