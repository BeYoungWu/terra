package com.monitoring.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monitoring.entity.CpuUsage;
import com.monitoring.service.CpuUsageService;

@RestController
@RequestMapping("/api/cpu-usage")
public class CpuUsageController {

	@Autowired
    private CpuUsageService cpuUsageService;

	// CPU 사용률 수집 (분 단위)
	@PostMapping
    public ResponseEntity<Void> saveCpuUsage(@RequestBody CpuUsage cpuUsage) {
//        cpuUsageService.saveCpuUsage(cpuUsage);
        return ResponseEntity.ok().build();
    }
	
	// 분 단위 조회: 지정한 시간 구간의 분 단위 CPU 사용률을 조회합니다.
	// 분 단위 API : 최근 1주 데이터 제공
    @GetMapping("/minutes")
    public ResponseEntity<List<CpuUsage>> getCpuUsageByMinutes(@RequestParam LocalDateTime start, @RequestParam LocalDateTime end) {
    	
    	
//    	List<CpuUsage> cpuUsages = cpuUsageService.getCpuUsageByMinutes(start, end);
//    	return ResponseEntity.ok(cpuUsages);
    	return null;
    }

    // 시 단위 조회: 지정한 날짜의 시  단위 CPU 최소/최대/평균 사용률을 조회합니다.
    // 시 단위 API : 최근 3달 데이터 제공
//    @GetMapping("/hours")

    // 일 단위 조회: 지정한 날짜 구간의 일  단위 CPU 최소/최대/평균 사용률을 조회합니다.
    // 일 단위 API : 최근 1년 데이터 제공
//    @GetMapping("/days")
	
}
