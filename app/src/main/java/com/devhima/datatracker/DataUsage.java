package com.devhima.datatracker;


import android.widget.Toast;
import android.content.Context;
import android.net.TrafficStats;
import androidx.annotation.NonNull;
import java.io.*;
import android.os.*;
import com.devhima.datatracker.MainActivity;

public class DataUsage {

	public static long getUsageStats(){
        long ds = 0;
        
            if (TrafficStats.getTotalRxBytes() != TrafficStats.UNSUPPORTED && TrafficStats.getTotalTxBytes() != TrafficStats.UNSUPPORTED) {
                ds = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes();
            }
		return ds;
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
    
    private static void mkToast(String txt){
		Toast.makeText(MainActivity.shareContext, txt, Toast.LENGTH_LONG).show();
    }
    
    public static void appendLog(String text){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            
        File logFile = new File(path + "/datatracker.log");
        if (!logFile.exists()){
            try
            {
                logFile.createNewFile();
            } 
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                mkToast(e.getMessage().toString());
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
            buf.append(text);
            buf.newLine();
            buf.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            mkToast(e.getMessage().toString());
        }
    }
    
}
