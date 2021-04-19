package com.bework.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public interface IBaseManager {
	public ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
}
