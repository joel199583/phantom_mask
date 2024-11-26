package com.kdan.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.beans.PropertyEditorSupport;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kdan.demo.dto.MaskDTO;
import com.kdan.demo.dto.StoreDTO;
import com.kdan.demo.service.StoreService;

@RestController
@RequestMapping("/store")
public class StoreController {
	
	public enum WEEKDAY {
        MON, TUE, WED, THUR, FRI, STA, SUN
    }
	
	public enum MASK_SORTBY {
        NAME, PRICE
    }
	
	public enum COMPARISON {
	    GREATER(">"),
	    LESS("<"),
	    EQUAL("=");

	    private final String symbol;

	    COMPARISON(String symbol) {
	        this.symbol = symbol;
	    }

	    public String getSymbol() {
	        return symbol;
	    }
	}
	
	public enum SEARCH_TYPE {
        MASK, STORE
    }
	
    @Autowired
    private StoreService storeService;

    @Operation(
        summary = "獲取在特定時間營業的商店",
        description = "根據指定的星期幾與時間，檢索目前正在營業的商店列表。",
        parameters = {
            @Parameter(name = "weekDay", description = "需要檢查的星期幾 (例如：MON, TUE)", schema = @Schema(implementation = WEEKDAY.class), required = true),
            @Parameter(name = "time", description = "需要檢查的時間，格式為 HH:mm", schema = @Schema(type = "string", format = "time"), required = true)
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "成功獲取商店列表", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StoreDTO.class))),
            @ApiResponse(responseCode = "400", description = "參數錯誤")
        }
    )
    @GetMapping(value = "/open")
    public List<StoreDTO> getOpenStore(
    		@RequestParam WEEKDAY weekDay, 
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time) {
    	return storeService.getOpenStoreAtTime(weekDay.name(), time);
    }

    @Operation(
        summary = "查詢商店的口罩資訊",
        description = "從指定的商店檢索口罩資訊，並根據名稱或價格進行排序。",
        parameters = {
            @Parameter(name = "storeName", description = "商店名稱", required = true),
            @Parameter(name = "sortBy", description = "按 NAME 或 PRICE 排序 (預設為 PRICE)", schema = @Schema(implementation = MASK_SORTBY.class))
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "成功獲取口罩資訊", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MaskDTO.class))),
            @ApiResponse(responseCode = "400", description = "參數錯誤")
        }
    )
    @GetMapping(value = "/maskinfo")
    public List<MaskDTO> getMaskInfo(
    		@RequestParam String storeName,
            @RequestParam(defaultValue = "PRICE") MASK_SORTBY sortBy) {
        return storeService.getMaskByStoreName(storeName, sortBy.name());
    }
    
    @Operation(
        summary = "根據價格範圍與口罩數量條件檢索商店",
        description = "檢索口罩價格位於指定範圍內，且符合口罩數量條件的商店列表。",
        parameters = {
            @Parameter(name = "minPrice", description = "口罩價格的最小值", required = true),
            @Parameter(name = "maxPrice", description = "口罩價格的最大值", required = true),
            @Parameter(name = "comparison", description = "比較操作符 (GREATER, LESS, EQUAL)", schema = @Schema(implementation = COMPARISON.class), required = true),
            @Parameter(name = "comparisonNumber", description = "用於比較口罩數量的參考值", required = true)
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "成功獲取商店列表", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StoreDTO.class))),
            @ApiResponse(responseCode = "400", description = "參數錯誤")
        }
    )
    @GetMapping(value = "/price-range-masktype-count")
    public List<StoreDTO> getStoreWithinCountAndPrice(
    		@RequestParam double minPrice, 
    		@RequestParam double maxPrice,
            @RequestParam COMPARISON comparison,
            @RequestParam int comparisonNumber) {
        return storeService.getStoreByCountAndPriceRange(minPrice, maxPrice, comparison.getSymbol(), comparisonNumber);
    }
    
    @Operation(
        summary = "依關鍵字模糊搜尋商店或口罩",
        description = "根據關鍵字執行模糊搜尋，搜尋範圍包括商店或口罩。",
        parameters = {
            @Parameter(name = "keyword", description = "搜尋的關鍵字", required = true),
            @Parameter(name = "searchBy", description = "搜尋範圍，MASK 或 STORE (預設為 MASK)", schema = @Schema(implementation = SEARCH_TYPE.class))
        },
        responses = {
            @ApiResponse(responseCode = "200", description = "成功獲取搜尋結果", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StoreDTO.class))),
            @ApiResponse(responseCode = "400", description = "參數錯誤")
        }
    )
    @GetMapping(value = "/search-store")
    public List<StoreDTO> fuzzySearchStore(
    		@RequestParam String keyword, 
    		@RequestParam(defaultValue = "MASK") SEARCH_TYPE searchBy) {
        return storeService.searchStore(keyword, searchBy);

    }
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm[:ss]");
        binder.registerCustomEditor(LocalTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    setValue(LocalTime.parse(text, formatter));
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Illegal time");
                }
            }
        });
        binder.registerCustomEditor(MASK_SORTBY.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(MASK_SORTBY.valueOf(text.toUpperCase())); // 忽略大小寫
            }
        });
        binder.registerCustomEditor(WEEKDAY.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(WEEKDAY.valueOf(text.toUpperCase())); // 忽略大小寫
            }
        });
        binder.registerCustomEditor(COMPARISON.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(COMPARISON.valueOf(text.toUpperCase())); // 忽略大小寫
            }
        });
        binder.registerCustomEditor(SEARCH_TYPE.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(SEARCH_TYPE.valueOf(text.toUpperCase())); // 忽略大小寫
            }
        });
    }
}