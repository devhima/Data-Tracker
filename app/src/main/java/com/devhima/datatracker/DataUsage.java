package com.devhima.datatracker;


import android.content.Context;
import android.net.TrafficStats;
import androidx.annotation.NonNull;

public class DataUsage {

	public static long getUsageStats(){
		return (TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes());
    }

	public static String formatSize(long v) {
		if (v < 1024) return v + " B";
		int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
		return String.format("%.3f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
	}
    
    public static double byteToGB(long byt){
        double i = byt * Math.pow(10,-9);
        return i;
    }
    
}
