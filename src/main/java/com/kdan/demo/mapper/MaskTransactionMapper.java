package com.kdan.demo.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Param;

import com.kdan.demo.dto.MaskTransactionDTO;

public interface MaskTransactionMapper {
	MaskTransactionDTO queryTransactionInfoByDateRange(
			@Param("startDate") LocalDateTime startDate, 
			@Param("endDate") LocalDateTime endDate);
	int insertTransactionHistory(int userId, int storeId, int maskTypeId, LocalDateTime transactionDate);
}
