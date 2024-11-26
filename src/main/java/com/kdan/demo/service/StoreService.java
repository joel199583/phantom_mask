package com.kdan.demo.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kdan.demo.controller.StoreController.SEARCH_TYPE;
import com.kdan.demo.dto.MaskDTO;
import com.kdan.demo.dto.StoreDTO;
import com.kdan.demo.mapper.StoreMapper;

@Service
public class StoreService {
	
	@Autowired
	StoreMapper storeMapper;

	public List<StoreDTO> getOpenStoreAtTime(String weekDay, LocalTime time) {
		return storeMapper.findStoreByTime(weekDay, time);
	}

	public List<MaskDTO> getMaskByStoreName(String storeName, String sortBy) {
		return storeMapper.findMaskByStoreName(storeName, sortBy);
	}

	public List<StoreDTO> getStoreByCountAndPriceRange(double minPrice, double maxPrice, String comparison, int comparisonNumber) {
		return storeMapper.fineStoreByCountAndPriceRange(minPrice, maxPrice, comparison, comparisonNumber);
	}

	public List<StoreDTO> searchStore(String keyword, SEARCH_TYPE searchBy) {
		switch(searchBy) {
		case STORE:
			return storeMapper.fuzzySearchStoreByStoreName(keyword);
		case MASK:
			return storeMapper.fuzzySearchStoreByMaskName(keyword);
		default: return null;
		}
	}

}
