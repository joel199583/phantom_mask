package com.kdan.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.kdan.demo.mapper.DefaultMapper;

@SpringBootTest
class KdanDemoApplicationTests {
	
	@Autowired
	DefaultMapper testMapper;

	@Test
	void contextLoads() {
	}

}
