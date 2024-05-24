package com.monitoring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import com.monitoring.dto.CpuUsageAnalysis;
import com.monitoring.entity.CpuUsage;
import com.monitoring.exception.DataCollectionException;
import com.monitoring.exception.InvalidParameterException;
import com.monitoring.repository.CpuUsageRepository;

@SpringBootTest
public class CpuUsageServiceTest {
	
	@Mock
    private CpuUsageRepository cpuUsageRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CpuUsageService cpuUsageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("POST CPU 사용률 수집 및 저장 - 정상 케이스")
    public void collectAndSaveCpuUsageTest() {
        // Given
        String url = "http://127.0.0.1:8080/actuator/metrics/system.cpu.usage";
        Map<String, Object> response = Map.of(
            "measurements", List.of(Map.of("value", 0.5))
        );

        when(restTemplate.getForObject(url, Map.class)).thenReturn(response);

        // When
        cpuUsageService.collectAndSaveCpuUsage();

        // Then
        verify(cpuUsageRepository).save(any(CpuUsage.class));
    }

    @Test
    @DisplayName("POST CPU 사용률 수집 및 저장 - 예외 케이스 : 데이터 수집 실패")
    public void collectAndSaveCpuUsageExceptionTest() {
        // Given
        String url = "http://127.0.0.1:8080/actuator/metrics/system.cpu.usage";

        when(restTemplate.getForObject(url, Map.class)).thenThrow(new RuntimeException());

        // When & Then
        assertThrows(DataCollectionException.class, () -> cpuUsageService.collectAndSaveCpuUsage());
    }

    @Test
    @DisplayName("GET CPU 사용률 분 단위 조회 - 정상 케이스")
    public void getCpuUsagePerMinuteTest() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();
        List<CpuUsage> cpuUsages = new ArrayList<>();
        cpuUsages.add(new CpuUsage(null, LocalDateTime.now(), 50.0));

        when(cpuUsageRepository.getCpuUsagePerMinute(start, end, LocalDateTime.now().minus(1, ChronoUnit.WEEKS)))
            .thenReturn(cpuUsages);

        // When
        List<CpuUsage> result = cpuUsageService.getCpuUsagePerMinute(start, end);

        // Then
        assertEquals(cpuUsages, result);
    }

    @Test
    @DisplayName("GET CPU 사용률 분 단위 조회 - 예외 케이스 : 파라미터가 null일 때")
    public void getCpuUsagePerMinuteNullParameterTest() {
        // When & Then
        assertThrows(InvalidParameterException.class, () -> cpuUsageService.getCpuUsagePerMinute(null, LocalDateTime.now()));
        assertThrows(InvalidParameterException.class, () -> cpuUsageService.getCpuUsagePerMinute(LocalDateTime.now(), null));
    }

    @Test
    @DisplayName("GET CPU 사용률 분 단위 조회 - 예외 케이스 : start가 end보다 이후일 때")
    public void getCpuUsagePerMinuteStartAfterEndTest() {
        // Given
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().minusHours(1);

        // When & Then
        assertThrows(InvalidParameterException.class, () -> cpuUsageService.getCpuUsagePerMinute(start, end));
    }

    @Test
    @DisplayName("GET CPU 사용률 시 단위 최소/최대/평균 조회 - 정상 케이스")
    public void getCpuUsagePerHourTest() {
        // Given
        String day = "2024-05-24";
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{12, 10.0, 80.0, 45.0});
        list.add(new Object[]{13, 15.0, 85.0, 50.0});

        when(cpuUsageRepository.getCpuUsagePerHour(any(LocalDateTime.class), any(LocalDateTime.class), any(LocalDateTime.class)))
        	.thenReturn(list);

        // When
        List<CpuUsageAnalysis> result = cpuUsageService.getCpuUsagePerHour(day);

        // Then
        assertEquals(2, result.size());
        assertEquals(12, result.get(0).getHour());
        assertEquals(10.0, result.get(0).getMinUsage());
        assertEquals(80.0, result.get(0).getMaxUsage());
        assertEquals(45.0, result.get(0).getAvgUsage());
    }

    @Test
    @DisplayName("GET CPU 사용률 시 단위 최소/최대/평균 조회 - 예외 케이스 : 파라미터가 null일 때")
    public void getCpuUsagePerHourNullParameterTest() {
        // When & Then
        assertThrows(InvalidParameterException.class, () -> cpuUsageService.getCpuUsagePerHour(null));
    }

    @Test
    @DisplayName("GET CPU 사용률 일 단위 최소/최대/평균 조회 - 정상 케이스")
    public void getCpuUsagePerDayTest() {
        // Given
        String start = "2024-05-01";
        String end = "2024-05-24";
        List<Object[]> list = new ArrayList<>();
        list.add(new Object[]{2024, 5, 1, 10.0, 80.0, 45.0});
        list.add(new Object[]{2024, 5, 2, 15.0, 85.0, 50.0});

        when(cpuUsageRepository.getCpuUsagePerDay(any(LocalDateTime.class), any(LocalDateTime.class), any(LocalDateTime.class)))
        	.thenReturn(list);

        // When
        List<CpuUsageAnalysis> result = cpuUsageService.getCpuUsagePerDay(start, end);

        // Then
        assertEquals(2, result.size());
        assertEquals(2024, result.get(0).getYear());
        assertEquals(5, result.get(0).getMonth());
        assertEquals(1, result.get(0).getDay());
        assertEquals(10.0, result.get(0).getMinUsage());
        assertEquals(80.0, result.get(0).getMaxUsage());
        assertEquals(45.0, result.get(0).getAvgUsage());
    }

    @Test
    @DisplayName("GET CPU 사용률 일 단위 최소/최대/평균 조회 - 예외 케이스 : 파라미터가 null일 때")
    public void getCpuUsagePerDayNullParameterTest() {
        // When & Then
        assertThrows(InvalidParameterException.class, () -> cpuUsageService.getCpuUsagePerDay(null, "2024-05-24"));
        assertThrows(InvalidParameterException.class, () -> cpuUsageService.getCpuUsagePerDay("2024-05-01", null));
    }

    @Test
    @DisplayName("GET CPU 사용률 일 단위 최소/최대/평균 조회 - 예외 케이스 : start가 end보다 이후일 때")
    public void getCpuUsagePerDayStartAfterEndTest() {
        // Given
        String start = "2024-05-24";
        String end = "2024-05-01";

        // When & Then
        assertThrows(InvalidParameterException.class, () -> cpuUsageService.getCpuUsagePerDay(start, end));
    }
	
}
