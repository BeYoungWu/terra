package com.monitoring.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.monitoring.entity.CpuUsage;


@Repository
public interface CpuUsageRepository extends JpaRepository<CpuUsage, Long> {

	// CPU 사용률 분 단위 조회 (최근 1주 데이터 제공) 
	@Query("SELECT u FROM tbl_usage u "+
			"WHERE u.useTime BETWEEN :start AND :end AND u.useTime >= :weekAgo ORDER BY u.useTime")
	List<CpuUsage> getCpuUsagePerMinute(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end, @Param("weekAgo")LocalDateTime weekAgo);

	// CPU 사용률 시 단위 최소/최대/평균 조회 (최근 3달 데이터 제공)
	@Query("SELECT HOUR(u.useTime) as hour, "+
			"MIN(u.cpuUsage) as minUsage, MAX(u.cpuUsage) as maxUsage, AVG(u.cpuUsage) as avgUsage "+
			"FROM tbl_usage u "+
			"WHERE u.useTime >= :dayStart AND u.useTime <= :dayEnd And u.useTime >= :monthsAgo "+
			"GROUP BY hour ORDER BY hour")
	List<Object[]> getCpuUsagePerHour(@Param("dayStart")LocalDateTime dayStart, @Param("dayEnd")LocalDateTime dayEnd, @Param("monthsAgo")LocalDateTime monthsAgo);

	// CPU 사용률 일 단위 최소/최대/평균 조회 (최근 1년 데이터 제공)
	@Query("SELECT YEAR(u.useTime) as year, MONTH(u.useTime) as month, DAY(u.useTime) as day, "+
			"MIN(u.cpuUsage) as minUsage, MAX(u.cpuUsage) as maxUsage, AVG(u.cpuUsage) as avgUsage "+
			"FROM tbl_usage u "+
			"WHERE u.useTime >= :startDay AND u.useTime <= :endDay And u.useTime >= :yearAgo "+
			"GROUP BY year, month, day ORDER BY year, month, day")
	List<Object[]> getCpuUsagePerDay(@Param("startDay")LocalDateTime startDay, @Param("endDay")LocalDateTime endDay, @Param("yearAgo")LocalDateTime yearAgo);

	
	
}
