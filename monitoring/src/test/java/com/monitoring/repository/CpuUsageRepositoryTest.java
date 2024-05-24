package com.monitoring.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.monitoring.entity.CpuUsage;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
public class CpuUsageRepositoryTest {
	
	@Autowired
    private CpuUsageRepository cpuUsageRepository;
	
	private static CpuUsage cpuUsage1;
	private static CpuUsage cpuUsage2;
	private static CpuUsage cpuUsage3;
	private static CpuUsage cpuUsage4;
	private static CpuUsage cpuUsage5;

    @BeforeEach
    public void setUp() {
    	System.err.println("=============================== setup ==============================");
    	cpuUsage1 = CpuUsage.builder()
    				.id((long) 1)
    				.cpuUsage(30.0)
    				.useTime(LocalDateTime.of(2024, 5, 22, 12, 0, 0))
    				.build();
    	cpuUsage2 = CpuUsage.builder()
				.id((long) 2)
				.cpuUsage(50.1)
				.useTime(LocalDateTime.of(2024, 5, 23, 12, 0, 0))
				.build();
    	cpuUsage3 = CpuUsage.builder()
				.id((long) 3)
				.cpuUsage(11.1)
				.useTime(LocalDateTime.of(2024, 5, 23, 12, 1, 0))
				.build();
    	cpuUsage4 = CpuUsage.builder()
				.id((long) 4)
				.cpuUsage(45.4)
				.useTime(LocalDateTime.of(2024, 5, 24, 12, 0, 0))
				.build();
    	cpuUsage5 = CpuUsage.builder()
				.id((long) 5)
				.cpuUsage(70.7)
				.useTime(LocalDateTime.of(2024, 5, 24, 13, 0, 0))
				.build();
    }
    
    @Test
    @DisplayName("CPU 사용률 분 단위 조회 (최근 1주 데이터 제공)")
    @Transactional
    @Order(1)
    public void getCpuUsagePerMinuteTest(){

        // given
        cpuUsageRepository.save(cpuUsage1);
        cpuUsageRepository.save(cpuUsage2);
        cpuUsageRepository.save(cpuUsage3);
        cpuUsageRepository.save(cpuUsage4);
        cpuUsageRepository.save(cpuUsage5);

        // when
        LocalDateTime start = LocalDateTime.of(2024, 5, 23, 12, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 5, 23, 12, 1, 0);
        LocalDateTime weekAgo = LocalDateTime.now().minus(1, ChronoUnit.WEEKS);
    	List<CpuUsage> list = cpuUsageRepository.getCpuUsagePerMinute(start, end, weekAgo);
    	
        // then
    	assertThat(list).isNotEmpty();
    	assertThat(list.size()).isEqualTo(2);
    	
    }
    
    @Test
    @DisplayName("CPU 사용률 시 단위 최소/최대/평균 조회 (최근 3달 데이터 제공)")
    @Transactional
    @Order(2)
    public void getCpuUsagePerHourTest(){

        // given
        cpuUsageRepository.save(cpuUsage1);
        cpuUsageRepository.save(cpuUsage2);
        cpuUsageRepository.save(cpuUsage3);
        cpuUsageRepository.save(cpuUsage4);
        cpuUsageRepository.save(cpuUsage5);

        // when
        LocalDateTime dayStart = LocalDateTime.of(2024, 5, 24, 0, 0, 0);
        LocalDateTime dayEnd = LocalDateTime.of(2024, 5, 24, 23, 59, 59);
        LocalDateTime monthsAgo = LocalDateTime.now().minus(3, ChronoUnit.MONTHS);
    	List<Object[]> list = cpuUsageRepository.getCpuUsagePerHour(dayStart, dayEnd, monthsAgo);
    	
        // then
    	assertThat(list).isNotEmpty();
    	assertThat(list.size()).isEqualTo(2);
    	
    }

    @Test
    @DisplayName("CPU 사용률 일 단위 최소/최대/평균 조회 (최근 1년 데이터 제공)")
    @Transactional
    @Order(3)
    public void getCpuUsagePerDayTest(){

        // given
        cpuUsageRepository.save(cpuUsage1);
        cpuUsageRepository.save(cpuUsage2);
        cpuUsageRepository.save(cpuUsage3);
        cpuUsageRepository.save(cpuUsage4);
        cpuUsageRepository.save(cpuUsage5);

        // when
        LocalDateTime startDay = LocalDateTime.of(2024, 5, 22, 0, 0, 0);
        LocalDateTime endDay = LocalDateTime.of(2024, 5, 24, 23, 59, 59);
        LocalDateTime yearAgo = LocalDateTime.now().minus(1, ChronoUnit.YEARS);
    	List<Object[]> list = cpuUsageRepository.getCpuUsagePerDay(startDay, endDay, yearAgo);
    	
        // then
    	assertThat(list).isNotEmpty();
    	assertThat(list.size()).isEqualTo(3);
    	
    }
}
