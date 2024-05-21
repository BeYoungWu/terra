package com.monitoring.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CpuUsageDto {

	private LocalDateTime timestamp;
	private double usage;
	
}
