package com.kdan.demo;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@MapperScan("com.kdan.demo.mapper")
@RestController
public class KdanDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(KdanDemoApplication.class, args);
	}
	
	@RequestMapping(value = {"/", ""})
    public String Hello() {
    	return "<h1>Welcome<h1>";
    }

}
