package com.kdan.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.kdan.demo.mapper")
public class KdanDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KdanDemoApplication.class, args);
	}

}
