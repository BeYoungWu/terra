package com.monitoring.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monitoring.dto.CpuUsageAnalysis;
import com.monitoring.entity.CpuUsage;
import com.monitoring.service.CpuUsageService;

@RestController
@RequestMapping("/api/cpu-usage")
public class CpuUsageController {

	@Autowired
    private CpuUsageService cpuUsageService;

	// CPU 사용률 수집 (1분 단위)
	@PostMapping
    public ResponseEntity<Void> collectAndSaveCpuUsage() {
        cpuUsageService.collectAndSaveCpuUsage();
        return ResponseEntity.ok().build();
    }
	
	// CPU 사용률 분 단위 조회 : 지정한 시간 구간의 분 단위 CPU 사용률을 조회합니다.
	// 최근 1주 데이터 제공
    @GetMapping("/minute")
    public ResponseEntity<List<CpuUsage>> getCpuUsagePerMinute(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime start, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime end) {
    	List<CpuUsage> cpuUsages = cpuUsageService.getCpuUsagePerMinute(start, end);
    	return ResponseEntity.ok(cpuUsages);
    }

    // CPU 사용률 시 단위 조회: 지정한 날짜의 시  단위 CPU 최소/최대/평균 사용률을 조회합니다.
    // 최근 3달 데이터 제공
    @GetMapping("/hour")
    public ResponseEntity<List<CpuUsageAnalysis>> getCpuUsagePerHour(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")String day) {
    	List<CpuUsageAnalysis> list = cpuUsageService.getCpuUsagePerHour(day);
    	return ResponseEntity.ok(list);
    }

    // CPU 사용률 일 단위 조회: 지정한 날짜 구간의 일  단위 CPU 최소/최대/평균 사용률을 조회합니다.
    // 최근 1년 데이터 제공
    @GetMapping("/day")
    public ResponseEntity<List<CpuUsageAnalysis>> getCpuUsagePerDay(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")String start, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")String end) {
    	List<CpuUsageAnalysis> list = cpuUsageService.getCpuUsagePerDay(start, end);
    	return ResponseEntity.ok(list);
    }
	
}
