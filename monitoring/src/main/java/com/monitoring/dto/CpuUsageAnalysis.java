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
	
	public CpuUsageAnalysis() {
    }
	
	public CpuUsageAnalysis(int year, int month, int day, int hour, double minUsage, double maxUsage, double avgUsage) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minUsage = minUsage;
        this.maxUsage = maxUsage;
        this.avgUsage = avgUsage;
    }
	
}
