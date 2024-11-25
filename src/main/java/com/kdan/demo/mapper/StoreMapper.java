package com.kdan.demo.mapper;

import java.time.LocalTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.kdan.demo.dto.MaskDTO;
import com.kdan.demo.dto.StoreDTO;

public interface StoreMapper {
	List<StoreDTO> findStoreByTime(@Param("weekDay") String weekDay, @Param("time") LocalTime time);
	List<MaskDTO> findMaskByStoreName(@Param("storeName") String storeName, @Param("sortBy") String sortBy);
	List<StoreDTO> fineStoreByCountAndPriceRange(
			@Param("minPrice") double minPrice, 
			@Param("maxPrice") double maxPrice, 
			@Param("comparison") String comparison,
			@Param("comparisonNumber") int comparisonNumber);
}
