package com.kdan.demo;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kdan.demo.mapper.DefaultMapper;

@Service
public class DataInitializer {
	
	@Autowired
	DefaultMapper defaultMapper;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@PostConstruct
    public void initData() {
        try {
        	ObjectMapper objectMapper = new ObjectMapper();
             
            File file1 = new File("data/pharmacies.json");
            File file2 = new File("data/users.json");

            List<Map<String, Object>> pharmacies = objectMapper.readValue(file1, new TypeReference<List<Map<String, Object>>>() {});
            List<Map<String, Object>> users = objectMapper.readValue(file2, new TypeReference<List<Map<String, Object>>>() {});

            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0;");
            jdbcTemplate.execute("TRUNCATE TABLE transaction_history;");
            jdbcTemplate.execute("TRUNCATE TABLE mask;");
            jdbcTemplate.execute("TRUNCATE TABLE store_hours;");
            jdbcTemplate.execute("TRUNCATE TABLE user;");
            jdbcTemplate.execute("TRUNCATE TABLE mask_type;");
            jdbcTemplate.execute("TRUNCATE TABLE store;");
            jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1;");
            
            // store init data
            int insertStoreRowcount = 
	            pharmacies.stream()
	            	.map(data -> Map.of("name", data.get("name"), "cashBalance", data.get("cashBalance"), "openingHours", data.get("openingHours")))
	            	.peek(System.out::println)
	            	.mapToInt(defaultMapper::insertStore)
	            	.sum();
            System.out.println("insert store rowcount: " + insertStoreRowcount);
            
            // user init data
            int insertUserRowcount = 
            	users.stream()
	            	.map(data -> Map.of("name", data.get("name"), "cashBalance", data.get("cashBalance")))
	            	.peek(System.out::println)
	            	.mapToInt(defaultMapper::insertUser)
	            	.sum();
            System.out.println("insert user rowcount: " + insertUserRowcount);
            
            // mask_type init data
            @SuppressWarnings("unchecked")
			int insertMaskTypeRowcount = 
	            pharmacies.stream()
	            	.flatMap(data -> ((List<Map<String, Object>>) data.get("masks")).stream())
	            	.map(data -> convertMaskName((String) data.get("name")))
	            	.filter(Objects::nonNull)
	            	.distinct()
		        	.peek(System.out::println)
		        	.mapToInt(defaultMapper::insertMaskType)
		        	.sum();
            System.out.println("insert mask_type rowcount: " + insertMaskTypeRowcount);
            
            // mask init data
            int insertMaskRowcount = 
	            pharmacies.stream()
		        	.mapToInt(data -> {
		        		
		        		@SuppressWarnings("unchecked")
						List<Map<String, Object>> masks = (List<Map<String, Object>>) data.get("masks");
		        		
		        		return 
	        				masks.stream()
	        					.map(maskData -> {
	        						Map<String, Object> insertData = convertMaskName((String) maskData.get("name"));
	        						if (Objects.nonNull(insertData)) {
	        							insertData.put("storeName", data.get("name"));
	            						insertData.put("price", maskData.get("price"));
	        						}
	        						return insertData;
	        					})
	        					.filter(Objects::nonNull)
	        					.peek(System.out::println)
	        					.mapToInt(defaultMapper::insertMask)
	        					.sum();
		        	})
		        	.sum();
            System.out.println("insert mask rowcount: " + insertMaskRowcount);
            
            // store_hours init data
            int insertStoreHoursRowcount = 
	            pharmacies.stream()
	            	.mapToInt(data -> {
	            		List<Map<String, Object>> insertData = parseOpeningHours((String) data.get("openingHours"));
	            		return 
	            			insertData
	            				.stream()
	            				.map(openingHourData -> {
	            					openingHourData.put("storeName", data.get("name"));
	            					return openingHourData;
	            				})
	            				.peek(System.out::println)
	            				.mapToInt(defaultMapper::insertStoreHours)
	            				.sum();
	            	})
	            	.sum();
            System.out.println("insert store_hours rowcount: " + insertStoreHoursRowcount);
            
            // transaction_history init data
            int insertTransactionHistoryRowcount = 
	            users.stream()
	            	.flatMap(data -> {
	            		@SuppressWarnings("unchecked")
						List<Map<String, Object>> purchaseHistories = ((List<Map<String, Object>>) data.get("purchaseHistories"));
	            		
	            		return 
            				purchaseHistories
            					.stream()
            					.map(purchaseHistory -> {
            						Map<String, Object> map = new HashMap<>();
            						map.put("userName", data.get("name"));
            						map.put("storeName", purchaseHistory.get("pharmacyName"));
            						map.put("maskType", convertMaskName((String) purchaseHistory.get("maskName")));
            						map.put("transactionAmount", purchaseHistory.get("transactionAmount"));
            						map.put("transactionDate", purchaseHistory.get("transactionDate"));
            						
            						return map;
            					});
	            	})
	            	.peek(System.out::println)
	            	.mapToInt(defaultMapper::insertTransactionHistory)
		        	.sum();
            System.out.println("insert transaction_history rowcount: " + insertTransactionHistoryRowcount);
            
            System.out.println("Data initialization completed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private static Map<String, Object> convertMaskName(String orgName) {

		// 正則表達式
        String regex = "^(.*?)\\s\\((.*?)\\)\\s\\((\\d+)\\s.*\\)$";
		
		// 編譯正則表達式
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(orgName);
        
        // 判斷是否匹配
        if (matcher.find()) {
        	Map<String, Object> map = new HashMap<>();
        	
            Object name = matcher.group(1);
            Object color = matcher.group(2);
            Object quantityPerPack = matcher.group(3);
            
            map.put("name", name);
            map.put("color", color);
            map.put("quantityPerPack", quantityPerPack);
            
            return map;
        }
        
		return null;
	}
	
	public static List<Map<String, Object>> parseOpeningHours(String openingHours) {
        List<Map<String, Object>> storeHoursList = new ArrayList<>();

        // 分割多個時間段（例如 "Mon - Fri 08:00 - 17:00 / Sat, Sun 08:00 - 12:00"）
        String[] timeRanges = openingHours.split(" / ");

        for (String timeRange : timeRanges) {
            // 分析每個時間段，例如 "Mon - Fri 08:00 - 17:00"
            Matcher matcher = Pattern.compile("([a-zA-Z,\\s-]+) (\\d{2}:\\d{2}) - (\\d{2}:\\d{2})").matcher(timeRange);
            
            if (matcher.matches()) {
                String weekdays = matcher.group(1).trim();
                String openTime = matcher.group(2);
                String closeTime = matcher.group(3);

                // 處理星期範圍（例如 "Mon - Fri" 或 "Mon, Wed, Fri"）
                String[] weekdayList = weekdays.split(",\\s*");
                if (weekdayList.length == 1 && weekdayList[0].contains("-")) {
                    // 解析星期範圍（例如 "Mon - Fri"）
                    String[] range = weekdayList[0].split(" - ");
                    String startDay = range[0];
                    String endDay = range[1];
                    weekdayList = getWeekdaysInRange(startDay, endDay);
                }

                // 為每個星期生成時間段
                for (String weekday : weekdayList) {
                    Map<String, Object> storeHourMap = new HashMap<>();

                    // 處理跨過午夜的時間（例如 "20:00 - 02:00"）
                    if (openTime.compareTo(closeTime) > 0) {
                        // 跨過午夜時，先將開放時間存為當天，並將關閉時間設為 23:59
                        storeHourMap.put("weekday", weekday);
                        storeHourMap.put("openTime", openTime);
                        storeHourMap.put("closeTime", "23:59:59");
                        storeHoursList.add(storeHourMap);

                        // 第二天的開放時間設為 "00:00"，不需要重複週日
                        String nextDay = getNextWeekday(weekday);
                        storeHourMap = new HashMap<>();
                        storeHourMap.put("weekday", nextDay);
                        storeHourMap.put("openTime", "00:00:00");
                        storeHourMap.put("closeTime", closeTime);
                        storeHoursList.add(storeHourMap);
                    } else {
                        // 非跨過午夜的時間段，直接插入
                        storeHourMap.put("weekday", weekday);
                        storeHourMap.put("openTime", openTime);
                        storeHourMap.put("closeTime", closeTime);
                        storeHoursList.add(storeHourMap);
                    }
                }
            }
        }

        return storeHoursList;
    }

    // 解析星期範圍為具體的星期天
    private static String[] getWeekdaysInRange(String startDay, String endDay) {
        Map<String, Integer> weekdayMap = new HashMap<>();
        weekdayMap.put("Mon", 1);
        weekdayMap.put("Tue", 2);
        weekdayMap.put("Wed", 3);
        weekdayMap.put("Thur", 4);
        weekdayMap.put("Fri", 5);
        weekdayMap.put("Sat", 6);
        weekdayMap.put("Sun", 7);

        int start = weekdayMap.get(startDay);
        int end = weekdayMap.get(endDay);

        List<String> weekdays = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            for (Map.Entry<String, Integer> entry : weekdayMap.entrySet()) {
                if (entry.getValue() == i) {
                    weekdays.add(entry.getKey());
                    break;
                }
            }
        }
        return weekdays.toArray(new String[0]);
    }

    // 獲取下一天的星期
    private static String getNextWeekday(String currentDay) {
        Map<String, Integer> weekdayMap = new HashMap<>();
        weekdayMap.put("Mon", 1);
        weekdayMap.put("Tue", 2);
        weekdayMap.put("Wed", 3);
        weekdayMap.put("Thur", 4);
        weekdayMap.put("Fri", 5);
        weekdayMap.put("Sat", 6);
        weekdayMap.put("Sun", 7);

        int current = weekdayMap.get(currentDay);
        int next = (current % 7) + 1;

        for (Map.Entry<String, Integer> entry : weekdayMap.entrySet()) {
            if (entry.getValue() == next) {
                return entry.getKey();
            }
        }
        return currentDay; // 預防錯誤，實際上應該永遠不會達到這裡
    }
}
