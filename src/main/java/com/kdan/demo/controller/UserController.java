package com.kdan.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kdan.demo.dto.BuyMaskDTO;
import com.kdan.demo.dto.MaskTransactionDTO;
import com.kdan.demo.dto.UserDTO;
import com.kdan.demo.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@GetMapping(value = "/top-transactions")
    public List<UserDTO> getTopUserByDateRange(
    		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
    		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate, 
            @RequestParam int greaterNumber) {
    	return userService.getTopUserByDateRange(startDate, endDate, greaterNumber);
    }
	
	@GetMapping(value = "/date-range-transaction-info")
    public MaskTransactionDTO getTransactionInfoByDateRange(
    		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
    		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
    	return userService.getTransactionInfoByDateRange(startDate, endDate);
    }
	
	@PostMapping(value = "/buy-mask/{userId}")
	public void buyMask(
			@PathVariable int userId,
			@RequestBody List<BuyMaskDTO> buyMaskList) {

		if (buyMaskList.stream().anyMatch(buyMaskInfo -> buyMaskInfo.getStoreId() == null || buyMaskInfo.getMaskTypeId() == null)) {
		    throw new IllegalArgumentException("Invalid buymask data");
		}
		
		userService.processBuyMask(userId, buyMaskList);
	}
}
