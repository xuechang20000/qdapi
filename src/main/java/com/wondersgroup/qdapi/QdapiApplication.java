package com.wondersgroup.qdapi;

import com.wondersgroup.framwork.dao.CommonJdbcDao;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = "com.wondersgroup")
public class QdapiApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context=SpringApplication.run(QdapiApplication.class, args);
		CommonJdbcDao commonJdbcDao=context.getBean(CommonJdbcDao.class);
		System.out.println("已经加载类："+commonJdbcDao.getClass());
	}
}
