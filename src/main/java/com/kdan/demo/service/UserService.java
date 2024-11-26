package com.kdan.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kdan.demo.dto.BuyMaskDTO;
import com.kdan.demo.dto.MaskTransactionDTO;
import com.kdan.demo.dto.UserDTO;
import com.kdan.demo.mapper.MaskTransactionMapper;
import com.kdan.demo.mapper.StoreMapper;
import com.kdan.demo.mapper.UserMapper;

@Service
public class UserService {
	
	@Autowired
	UserMapper userMapper;
	
	@Autowired
	MaskTransactionMapper maskTransactionMapper;
	
	@Autowired
	StoreMapper storeMapper;
 
	public List<UserDTO> getTopUserByDateRange(LocalDate startDate, LocalDate endDate, int greaterNumber) {
		return userMapper.fineTopUserByDateRange(
				startDate.atStartOfDay(), 
				endDate.plusDays(1).atStartOfDay(), 
				greaterNumber);
	}

	public MaskTransactionDTO getTransactionInfoByDateRange(LocalDate startDate, LocalDate endDate) {
		return maskTransactionMapper.queryTransactionInfoByDateRange(
				startDate.atStartOfDay(), 
				endDate.plusDays(1).atStartOfDay());
	}
	
	@Transactional(rollbackFor = Exception.class)
    public void processBuyMask(int userId, List<BuyMaskDTO> buyMaskList) {
		// 處理各藥局購買金額資料
		Map<Integer, Double> storeSum = 
			buyMaskList.stream()
				.map(data -> {
					double maskPrice = storeMapper.findPriceByStoreIdAndMaskTypeId(data.getStoreId(), data.getMaskTypeId());
					return Map.entry(data.getStoreId(), maskPrice);
				})
				.collect(Collectors.groupingBy(
	                Map.Entry::getKey, // 分組依據
	                Collectors.summingDouble(Map.Entry::getValue) // 累積金額
	            ));
		
		System.out.println(storeSum);
		
		// 購買總額
		double buyTotal = storeSum.values().stream().mapToDouble(Double::valueOf).sum();
		// 使用者餘額
		double userCashBalance = userMapper.fineCashBalanceByUserId(userId);
		
		// 若使用者餘額少於購買總額, 返回error
		if (buyTotal > userCashBalance)
			throw new IllegalArgumentException("User has insufficient balance.");
		
		// 新增購買紀錄
		LocalDateTime now = LocalDateTime.now();
		int insertTransactionHistoryRowcount =
			buyMaskList.stream()
				.mapToInt(data -> maskTransactionMapper.insertTransactionHistory(userId, data.getStoreId(), data.getMaskTypeId(), now))
				.sum();
		System.out.println("insert transaction_history row count : " + insertTransactionHistoryRowcount);
		
		// 更新 使用者餘額
		double newUserCashBalance = userCashBalance - buyTotal;
		int updateUserRowcount = userMapper.updateUserCashBalanceByUserId(userId, newUserCashBalance);
		System.out.println("update user row count : " + updateUserRowcount);
		
		// 更新 各商家餘額
		int updateStoreRowcount = 
			storeSum.entrySet()
				.stream()
				.mapToInt(entry -> {
					int storeId = entry.getKey();
					double currentStoreByTotal = entry.getValue();
					double storeCashBalance = storeMapper.fineCashBalanceByStoreId(storeId);
					double newStoreCashBalance = currentStoreByTotal + storeCashBalance;
					
					return storeMapper.updateStoreCashBalanceByStoreId(storeId, newStoreCashBalance);
				})
				.sum();
		System.out.println("update store row count : " + updateStoreRowcount);
    }

}
