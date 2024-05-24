package com.monitoring.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.monitoring.dto.CpuUsageAnalysis;
import com.monitoring.entity.CpuUsage;
import com.monitoring.exception.DataCollectionException;
import com.monitoring.exception.InvalidParameterException;
import com.monitoring.repository.CpuUsageRepository;

@Service
public class CpuUsageService {

	private static final Logger logger = LoggerFactory.getLogger(CpuUsageService.class);
	
	@Autowired
	private CpuUsageRepository cpuUsageRepository;
	
	@Autowired
    private RestTemplate restTemplate;

	// CPU 사용률 수집 및 저장 (1분 단위)
	@Scheduled(fixedRate = 60000) // 매 분마다 실행
	public void collectAndSaveCpuUsage() {
		String url = "http://127.0.0.1:8080/actuator/metrics/system.cpu.usage";
		try {
			Map<String, Object> response = restTemplate.getForObject(url, Map.class);

	        if (response != null && response.containsKey("measurements")) {
	            List<Map<String, Object>> measurements = (List<Map<String, Object>>) response.get("measurements");
	            if (!measurements.isEmpty()) {
	                double cpuUsageData = (double) measurements.get(0).get("value") * 100;
	                if (cpuUsageData>0 && cpuUsageData<100) { // 서버 실행 직후에는 CPU 사용률이 0이나 100으로 기록되기 때문에 제외
	                	CpuUsage cpuUsage = new CpuUsage(null, LocalDateTime.now(), cpuUsageData);
	                    cpuUsageRepository.save(cpuUsage);
	                }
	            }
	        }
		} catch (Exception e) { // CPU 사용률 데이터 수집 및 저장 예외처리
			logger.error("CPU 사용률 데이터 수집 및 저장에 실패했습니다.", e);
            throw new DataCollectionException("CPU 사용률 데이터 수집 및 저장에 실패했습니다.", e);
		}
	}

	// CPU 사용률 분 단위 조회 (최근 1주 데이터 제공)
	public List<CpuUsage> getCpuUsagePerMinute(LocalDateTime start, LocalDateTime end) {
		// 파라미터 예외처리
		if (start == null || end == null) {
            throw new InvalidParameterException("요구되는 파라미터가 null 값입니다.");
        }
        if (start.isAfter(end)) {
            throw new InvalidParameterException("시작 날짜가 종료 날짜보다 이후의 날짜입니다.");
        }
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime weekAgo = now.minus(1, ChronoUnit.WEEKS);
		return cpuUsageRepository.getCpuUsagePerMinute(start, end, weekAgo);
	}

	// CPU 사용률 시 단위 최소/최대/평균 조회 (최근 3달 데이터 제공)
	public List<CpuUsageAnalysis> getCpuUsagePerHour(String day) {
		// 파라미터 예외처리
		if (day == null) {
            throw new InvalidParameterException("요구되는 파라미터가 null 값입니다.");
        }
		
		// 파라미터 날짜 형식 에외처리
        try {
            LocalDate.parse(day);
        } catch (DateTimeParseException e) {
            throw new InvalidParameterException("날짜 포맷이 잘못된 값입니다.");
        }
        
		LocalDate localDate = LocalDate.parse(day);
		LocalDateTime dayStart = localDate.atStartOfDay();
    	LocalDateTime dayEnd = localDate.atTime(LocalTime.MAX);
    	
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime monthsAgo = now.minus(3, ChronoUnit.MONTHS);
		
		// DB에서 가져온 데이터 DTO에 정리
		List<Object[]> list = cpuUsageRepository.getCpuUsagePerHour(dayStart, dayEnd, monthsAgo);
		List<CpuUsageAnalysis> analysisList = new ArrayList<>();
		for (Object[] row : list) {
			CpuUsageAnalysis analysis = new CpuUsageAnalysis();
			analysis.setHour((int) row[0]);
			analysis.setMinUsage(Double.parseDouble(row[1].toString()));
			analysis.setMaxUsage(Double.parseDouble(row[2].toString()));
			analysis.setAvgUsage(Double.parseDouble(row[3].toString()));
			analysisList.add(analysis);
		}
		
		return analysisList;
	}

	// CPU 사용률 일 단위 최소/최대/평균 조회 (최근 1년 데이터 제공)
	public List<CpuUsageAnalysis> getCpuUsagePerDay(String start, String end) {
		// 파라미터 null 예외처리
		if (start == null || end == null) {
            throw new InvalidParameterException("요구되는 파라미터가 null 값입니다.");
        }
        
		// 파라미터 날짜 형식 에외처리
        try {
            LocalDate.parse(start);
            LocalDate.parse(end);
        } catch (DateTimeParseException e) {
            throw new InvalidParameterException("날짜 포맷이 잘못된 값입니다.");
        }
		
		LocalDate startLocalDate = LocalDate.parse(start);
		LocalDateTime startDay = startLocalDate.atStartOfDay();
		
		LocalDate endLocalDate = LocalDate.parse(end);
    	LocalDateTime endDay = endLocalDate.atTime(LocalTime.MAX);
    	
    	if (startLocalDate.isAfter(endLocalDate)) {
            throw new InvalidParameterException("시작 날짜가 종료 날짜보다 이후의 날짜입니다.");
        }
    	
    	LocalDateTime now = LocalDateTime.now();
		LocalDateTime yearAgo = now.minus(1, ChronoUnit.YEARS);
    	
    	// DB에서 가져온 데이터 DTO에 정리
    	List<Object[]> list = cpuUsageRepository.getCpuUsagePerDay(startDay, endDay, yearAgo);
    	List<CpuUsageAnalysis> analysisList = new ArrayList<>();
    	for (Object[] row : list) {
    		CpuUsageAnalysis analysis = new CpuUsageAnalysis();
    		analysis.setYear((int) row[0]);
    		analysis.setMonth((int) row[1]);
    		analysis.setDay((int) row[2]);
			analysis.setMinUsage(Double.parseDouble(row[3].toString()));
			analysis.setMaxUsage(Double.parseDouble(row[4].toString()));
			analysis.setAvgUsage(Double.parseDouble(row[5].toString()));
    		analysisList.add(analysis);
    	}
		
		return analysisList;
	}

}
