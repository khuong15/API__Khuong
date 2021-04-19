package com.bework.config;

import java.util.List;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class UserManager implements IUserManager {

	private IUserDAO userDAO = (IUserDAO) context.getBean("UserDAO");

	public UserManager(DriverManagerDataSource dataSource) {
		userDAO.setDataSource(dataSource);
	}

	public UserManager() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//		dataSource.setUrl("jdbc:mysql://test-beedu-rds-mysql-instance.c0vduc4s8odp.ap-southeast-1.rds.amazonaws.com:3306/beedu-authentication-test?characterEncoding=utf8");
//		dataSource.setUsername("devbeedu");
//		dataSource.setPassword("devbeedu@2019");
		
//		dataSource.setUrl("jdbc:mysql://test-bework-rds-mysql-instance.c0vduc4s8odp.ap-southeast-1.rds.amazonaws.com:3306/bework-authentication-test?characterEncoding=utf8");
//		dataSource.setUsername("testbeworkrds");
//		dataSource.setPassword("bework2020");
		
//		dataSource.setUrl("jdbc:mysql://10.36.236.16:3306/lbg-authentication?characterEncoding=utf8");
//		dataSource.setUsername("root");
//		dataSource.setPassword("longbiengolf123$%^ROOT");
		
		dataSource.setUrl("jdbc:mysql://test-lienviet-rds-mysql-instance.c0vduc4s8odp.ap-southeast-1.rds.amazonaws.com:3306/bework-authentication-test?characterEncoding=utf8");
		dataSource.setUsername("testbeworkrds");
		dataSource.setPassword("bework2020");
		
		userDAO.setDataSource(dataSource);
	}
	
	public UserManager(Boolean authenDB) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
//		dataSource.setUrl("jdbc:mysql://test-beedu-rds-mysql-instance.c0vduc4s8odp.ap-southeast-1.rds.amazonaws.com:3306/testbeedudb?characterEncoding=utf8&verifyServerCertificate=false&useSSL=false&connectionCollation=utf8mb4_unicode_ci");
//		dataSource.setUsername("devbeedu");
//		dataSource.setPassword("devbeedu@2019");
		
//		dataSource.setUrl("jdbc:mysql://test-bework-rds-mysql-instance.c0vduc4s8odp.ap-southeast-1.rds.amazonaws.com:3306/bework-main-test?characterEncoding=utf8&verifyServerCertificate=false&useSSL=false&connectionCollation=utf8mb4_unicode_ci");
//		dataSource.setUsername("testbeworkrds");
//		dataSource.setPassword("bework2020");
		
//		dataSource.setUrl("jdbc:mysql://10.36.236.16:3306/lbg-main?characterEncoding=utf8");
//		dataSource.setUsername("root");
//		dataSource.setPassword("longbiengolf123$%^ROOT");
		
		dataSource.setUrl("jdbc:mysql://test-lienviet-rds-mysql-instance.c0vduc4s8odp.ap-southeast-1.rds.amazonaws.com:3306/bework-main-test?characterEncoding=utf8&verifyServerCertificate=false&useSSL=false&connectionCollation=utf8mb4_unicode_ci");
		dataSource.setUsername("testbeworkrds");
		dataSource.setPassword("bework2020");
		userDAO.setDataSource(dataSource);
	}

	@Override
	public User getUser(String username, String countryCode) {
		User user = userDAO.getUser(username, countryCode);
		return user;
	}

	@Override
	public List<String> getListClass(int id, String userType) {
		return userDAO.getListClass(id, userType);
	}

	public int updateUserStatus(int userId, String status) {
		return userDAO.updateUserStatus(userId, status);
	}
}
