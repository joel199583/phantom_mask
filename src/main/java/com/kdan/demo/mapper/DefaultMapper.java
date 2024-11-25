package com.kdan.demo.mapper;

import java.util.Map;

public interface DefaultMapper {
	void truncateAllTables();
	int insertStore(Map<String, Object> store);
	int insertUser(Map<String, Object> user);
	int insertMaskType(Map<String, Object> maskType);
	int insertMask(Map<String, Object> mask);
	int insertStoreHours(Map<String, Object> openingHour);
	int insertTransactionHistory(Map<String, Object> transactionHistory);
}
