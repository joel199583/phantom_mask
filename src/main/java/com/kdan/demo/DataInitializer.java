package com.kdan.demo;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdan.demo.mapper.DefaultMapper;

@Service
public class DataInitializer {
	
	@Autowired
	DefaultMapper defaultMapper;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@PostConstruct
    public void initData() {
        try {
        	ObjectMapper objectMapper = new ObjectMapper();
             
            File file1 = new File("data/pharmacies.json");
            File file2 = new File("data/users.json");

            List<Map<String, Object>> pharmacies = objectMapper.readValue(file1, new TypeReference<List<Map<String, Object>>>() {});
            List<Map<String, Object>> users = objectMapper.readValue(file2, new TypeReference<List<Map<String, Object>>>() {});

            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
            jdbcTemplate.execute("TRUNCATE TABLE transaction_history;");
            jdbcTemplate.execute("TRUNCATE TABLE mask;");
            jdbcTemplate.execute("TRUNCATE TABLE store_hours;");
            jdbcTemplate.execute("TRUNCATE TABLE user;");
            jdbcTemplate.execute("TRUNCATE TABLE mask_type;");
            jdbcTemplate.execute("TRUNCATE TABLE store;");
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
            
            // store init data
            int insertStoreRowcount = 
	            pharmacies.stream()
	            	.map(data -> Map.of("name", data.get("name"), "cashBalance", data.get("cashBalance")))
	            	.peek(System.out::println)
	            	.mapToInt(defaultMapper::insertStore)
	            	.sum();
            System.out.println("insert store rowcount: " + insertStoreRowcount);
            
            // user init data
            int insertUserRowcount = 
            	users.stream()
	            	.map(data -> Map.of("name", data.get("name"), "cashBalance", data.get("cashBalance")))
	            	.peek(System.out::println)
	            	.mapToInt(defaultMapper::insertUser)
	            	.sum();
            System.out.println("insert user rowcount: " + insertUserRowcount);

            System.out.println("Data initialization completed!");
        } catch (IOException e) {
            System.err.println("Error loading JSON files: " + e.getMessage());
        }
    }
}
