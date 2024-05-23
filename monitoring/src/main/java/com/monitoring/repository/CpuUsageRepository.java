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
			"WHERE u.timestamp BETWEEN :start AND :end AND u.timestamp >= :weekAgo ORDER BY u.timestamp")
	List<CpuUsage> getCpuUsagePerMinute(@Param("start")LocalDateTime start, @Param("end")LocalDateTime end, @Param("weekAgo")LocalDateTime weekAgo);

	// CPU 사용률 시 단위 최소/최대/평균 조회 (최근 3달 데이터 제공)
	@Query("SELECT HOUR(u.timestamp) as hour, "+
			"MIN(u.usage) as minUsage, MAX(u.usage) as maxUsage, AVG(u.usage) as avgUsage "+
			"FROM tbl_usage u "+
			"WHERE u.timestamp >= :dayStart AND u.timestamp <= :dayEnd And u.timestamp >= :monthsAgo "+
			"GROUP BY hour ORDER BY hour")
	List<Object[]> getCpuUsagePerHour(@Param("dayStart")LocalDateTime dayStart, @Param("dayEnd")LocalDateTime dayEnd, @Param("monthsAgo")LocalDateTime monthsAgo);

	// CPU 사용률 일 단위 최소/최대/평균 조회 (최근 1년 데이터 제공)
	@Query("SELECT YEAR(u.timestamp) as year, MONTH(u.timestamp) as month, DAY(u.timestamp) as day, "+
			"MIN(u.usage) as minUsage, MAX(u.usage) as maxUsage, AVG(u.usage) as avgUsage "+
			"FROM tbl_usage u "+
			"WHERE u.timestamp >= :startDay AND u.timestamp <= :endDay And u.timestamp >= :yearAgo "+
			"GROUP BY year, month, day ORDER BY year, month, day")
	List<Object[]> getCpuUsagePerDay(@Param("startDay")LocalDateTime startDay, @Param("endDay")LocalDateTime endDay, @Param("yearAgo")LocalDateTime yearAgo);

	
	
}
