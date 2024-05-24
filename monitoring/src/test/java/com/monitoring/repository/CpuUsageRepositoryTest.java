package com.monitoring.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import com.monitoring.entity.CpuUsage;
import com.monitoring.exception.InvalidParameterException;

@DataJpaTest
@Transactional
public class CpuUsageRepositoryTest {
	
	@Autowired
    private CpuUsageRepository cpuUsageRepository;

    @BeforeEach
    @Sql({"/test-schema.sql", "/test-data.sql"})
    public void setUp() {
        // 각 테스트 전에 test-data.sql 파일의 내용을 데이터베이스에 로드
    }

    @Test
    @DisplayName("GET CpuUsagePerMinute - 정상 케이스")
    public void getCpuUsagePerMinuteTest() {
        // Given
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);

        // When
        List<CpuUsage> result = cpuUsageRepository.getCpuUsagePerMinute(start, end, weekAgo);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size()); // test-data.sql에 해당 시간 범위 내에 3개의 엔트리가 있다고 가정
    }

    @Test
    @DisplayName("GET CpuUsagePerMinute - 예외 케이스 : 파라미터가 null일 때")
    public void getCpuUsagePerMinute_NullParameterTest() {
    	LocalDateTime start = LocalDateTime.now().minusDays(1);
    	LocalDateTime end = LocalDateTime.now();
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);

        // start가 null일 때
        assertThrows(InvalidParameterException.class, () -> {
            cpuUsageRepository.getCpuUsagePerMinute(null, end, weekAgo);
        });

        // end가 null일 때
        assertThrows(InvalidParameterException.class, () -> {
            cpuUsageRepository.getCpuUsagePerMinute(start, null, weekAgo);
        });
    }
    
    @Test
    @DisplayName("GET CpuUsagePerHour - 정상 케이스")
    public void getCpuUsagePerHourTest() {
        // Given
        LocalDateTime dayStart = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime dayEnd = LocalDateTime.now().minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999);
        LocalDateTime monthsAgo = LocalDateTime.now().minusMonths(3);

        // When
        List<Object[]> result = cpuUsageRepository.getCpuUsagePerHour(dayStart, dayEnd, monthsAgo);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // test-data.sql에 해당 시간 범위 내에 2개의 서로 다른 시간이 있다고 가정
        assertEquals(0, result.get(0)[0]);
        assertEquals(10.0, (Double) result.get(0)[1]);
        assertEquals(80.0, (Double) result.get(0)[2]);
        assertEquals(45.0, (Double) result.get(0)[3]);
    }

    @Test
    @DisplayName("getCpuUsagePerHour - 예외 케이스 : 파라미터가 null일 때")
    public void getCpuUsagePerHour_NullParameterTest() {
    	LocalDateTime dayStart = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    	LocalDateTime dayEnd = LocalDateTime.now().minusDays(1).withHour(23).withMinute(59).withSecond(59).withNano(999);
        LocalDateTime monthsAgo = LocalDateTime.now().minusMonths(3);

        // dayStart가 null일 때
        assertThrows(InvalidParameterException.class, () -> {
            cpuUsageRepository.getCpuUsagePerHour(null, dayEnd, monthsAgo);
        });

        // dayEnd가 null일 때
        assertThrows(InvalidParameterException.class, () -> {
            cpuUsageRepository.getCpuUsagePerHour(dayStart, null, monthsAgo);
        });
    }
    
    @Test
    @DisplayName("GET CpuUsagePerDay - 정상 케이스")
    public void getCpuUsagePerDayTest() {
        // Given
        LocalDateTime startDay = LocalDateTime.now().minusDays(5).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999);
        LocalDateTime yearAgo = LocalDateTime.now().minusYears(1);

        // When
        List<Object[]> result = cpuUsageRepository.getCpuUsagePerDay(startDay, endDay, yearAgo);

        // Then
        assertNotNull(result);
        assertEquals(5, result.size()); // Assuming test-data.sql contains 5 distinct days within the given time range
        assertEquals(2024, result.get(0)[0]); // First entry year
        assertEquals(5, result.get(0)[1]); // First entry month
        assertEquals(1, result.get(0)[2]); // First entry day
        assertEquals(10.0, (Double) result.get(0)[3]); // First entry minUsage
        assertEquals(80.0, (Double) result.get(0)[4]); // First entry maxUsage
        assertEquals(45.0, (Double) result.get(0)[5]); // First entry avgUsage
    }
    
    @Test
    @DisplayName("getCpuUsagePerDay - 예외 케이스 : 파라미터가 null일 때")
    public void getCpuUsagePerDay_NullParameterTest() {
    	LocalDateTime startDay = LocalDateTime.now().minusDays(5).withHour(0).withMinute(0).withSecond(0).withNano(0);
    	LocalDateTime endDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999);
        LocalDateTime yearAgo = LocalDateTime.now().minusYears(1);

        // startDay가 null일 때
        assertThrows(InvalidParameterException.class, () -> {
            cpuUsageRepository.getCpuUsagePerDay(null, endDay, yearAgo);
        });

        // endDay가 null일 때
        assertThrows(InvalidParameterException.class, () -> {
            cpuUsageRepository.getCpuUsagePerDay(startDay, null, yearAgo);
        });
    }
}
