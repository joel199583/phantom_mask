package com.kdan.demo.mapper;

import java.util.Map;

public interface DefaultMapper {
	void truncateAllTables();
	int insertStore(Map<String, Object> store);
	int insertUser(Map<String, Object> store);
}
