package com.monitoring.dto;

import lombok.Data;

@Data
public class CpuUsageAnalysis {

	private int year;
	private int month;
	private int day;
	private int hour;
	private double minUsage;
	private double maxUsage;
	private double avgUsage; 
	
}
