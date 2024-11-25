package com.kdan.demo.controller;

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
        Mon, Tue, Wed, Thur, Fri, Sat, Sun
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
	
    @Autowired
    private StoreService storeService;

    @GetMapping(value = "/open")
    public List<StoreDTO> getOpenStore(
    		@RequestParam WEEKDAY weekDay, 
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime time) {
    	return storeService.getOpenStoreAtTime(weekDay.name(), time);
    }

    @GetMapping(value = "/maskinfo")
    public List<MaskDTO> getMaskInfo(
    		@RequestParam String storeName,
            @RequestParam MASK_SORTBY sortBy) {
        return storeService.getMaskByStoreName(storeName, sortBy.name());
    }
    
    @GetMapping(value = "/storeWithinCountAndPrice")
    public List<StoreDTO> getStoreWithinCountAndPrice(
    		@RequestParam double minPrice, 
    		@RequestParam double maxPrice,
            @RequestParam COMPARISON comparison,
            @RequestParam int comparisonNumber) {
        return storeService.getStoreByCountAndPriceRange(minPrice, maxPrice, comparison.getSymbol(), comparisonNumber);
    }
//
//    @GetMapping(value = "/searchPharmacies")
//    public List<PharmaciesResponse> searchPharmacies(@RequestParam String searchInfo, @RequestParam String searchBy) {
//
//        return pharmaciesService.searchPharmaciesInformations(searchInfo, searchBy);
//
//    }
    
    @InitBinder
    public void initBinder1(WebDataBinder binder) {
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
    }
    
    @InitBinder
    public void initBinder2(WebDataBinder binder) {
        binder.registerCustomEditor(MASK_SORTBY.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(MASK_SORTBY.valueOf(text.toUpperCase())); // 忽略大小寫
            }
        });
    }
    
    @InitBinder
    public void initBinder3(WebDataBinder binder) {
        binder.registerCustomEditor(WEEKDAY.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(WEEKDAY.valueOf(text.toUpperCase())); // 忽略大小寫
            }
        });
    }
    
    @InitBinder
    public void initBinder4(WebDataBinder binder) {
        binder.registerCustomEditor(COMPARISON.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(COMPARISON.valueOf(text.toUpperCase())); // 忽略大小寫
            }
        });
    }

}