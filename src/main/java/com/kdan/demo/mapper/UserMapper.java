package com.kdan.demo.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.kdan.demo.dto.UserDTO;

public interface UserMapper {
	List<UserDTO> fineTopUserByDateRange(
			@Param("startDate") LocalDateTime startDate, 
			@Param("endDate") LocalDateTime endDate, 
			@Param("greaterNumber") int greaterNumber);
	double fineCashBalanceByUserId(@Param("userId") int userId);
	int updateUserCashBalanceByUserId(@Param("userId") int userId, @Param("cashBalance") double cashBalance);
}
