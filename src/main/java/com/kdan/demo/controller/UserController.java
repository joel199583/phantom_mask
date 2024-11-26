package com.kdan.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
	
	@Operation(
        summary = "取得特定日期範圍內的最高交易用戶",
        description = "根據提供的起始日期與結束日期，並過濾出交易額超過指定金額的用戶，返回交易金額最高的用戶列表。"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功返回用戶列表"),
        @ApiResponse(responseCode = "400", description = "無效的日期格式或數據範圍")
    })
	@GetMapping(value = "/top-transactions")
    public List<UserDTO> getTopUserByDateRange(
    		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
    		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate, 
            @RequestParam int greaterNumber) {
    	return userService.getTopUserByDateRange(startDate, endDate, greaterNumber);
    }
	
	@Operation(
        summary = "取得特定日期範圍內的交易資訊",
        description = "根據提供的日期範圍，返回該範圍內的交易統計資訊。"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功返回交易資訊"),
        @ApiResponse(responseCode = "400", description = "無效的日期格式")
    })
	@GetMapping(value = "/date-range-transaction-info")
    public MaskTransactionDTO getTransactionInfoByDateRange(
    		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
    		@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
    	return userService.getTransactionInfoByDateRange(startDate, endDate);
    }
	
	@Operation(
        summary = "用戶購買口罩",
        description = "讓指定的用戶根據提供的口罩資訊進行購買操作。"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "成功執行購買操作"),
        @ApiResponse(responseCode = "400", description = "請求資料無效，例如商店ID或口罩類型ID為空")
    })
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
