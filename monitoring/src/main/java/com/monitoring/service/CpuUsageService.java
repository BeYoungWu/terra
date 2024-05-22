package com.monitoring.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.monitoring.entity.CpuUsage;
import com.monitoring.repository.CpuUsageRepository;

@Service
public class CpuUsageService {

	@Autowired
	private CpuUsageRepository cpuUsageRepository;
	
	@Autowired
    private RestTemplate restTemplate;

	// CPU 사용률 수집 (1분 단위)
	@Scheduled(fixedRate = 60000) // 매 분마다 실행
	public void collectAndSaveCpuUsage() {
		String url = "http://127.0.0.1:8080/actuator/metrics/system.cpu.usage";
		Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("measurements")) {
            List<Map<String, Object>> measurements = (List<Map<String, Object>>) response.get("measurements");
            if (!measurements.isEmpty()) {
                double cpuUsageData = (double) measurements.get(0).get("value") * 100;
                if (cpuUsageData!=0) { // 서버 실행 직후에는 CPU 사용률이 0으로 기록되기 때문에
                	CpuUsage cpuUsage = new CpuUsage(null, LocalDateTime.now(), cpuUsageData);
                    cpuUsageRepository.save(cpuUsage);
                }
            }
        }
	}

	// CPU 사용률 분 단위 조회 (최근 1주 데이터 제공)
	public List<CpuUsage> getCpuUsagePerMinute(LocalDateTime start, LocalDateTime end) {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime weekAgo = now.minus(1, ChronoUnit.WEEKS);
		return cpuUsageRepository.getCpuUsagePerMinute(start, end, weekAgo);
	}

}
