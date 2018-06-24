package util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by yy on 2018/6/22.
 */

public class CpuManager {
    private static final String TAG = "CpuManager";
    public static short getThermalInfo() {
        String path="/sys/class/thermal/thermal_zone9/temp";
        File f=new File(path);
        if(!f.exists()){
            Log.d(TAG, "getThermalInfo: "+"enter");
            return 0;
        }
        else{
            FileInputStream fin=null;
            try {
                 fin = new FileInputStream(f);
                byte[] result=new byte[1024];
                if(fin.read(result)!=-1){
                    return (short)(Long.parseLong(new String(result,0,result.length).trim())/1000);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    fin.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
    /** get CPU rate 不明原因：在三星和模拟器获取总是0
     * @return
     */
    public static  int getProcessCpuRate() {
        StringBuilder tv = new StringBuilder();
        int rate = 0;
        try {
            String Result;
            Process p;
            //执行命令
            p = Runtime.getRuntime().exec("top -n 1");

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((Result = br.readLine()) != null) {
                Log.d(TAG, "getProcessCpuRate: "+"enter this");
                if (Result.trim().length() < 1) {
                    continue;
                } else {
                    String[] CPUusr = Result.split("%");
                    tv.append("USER:" + CPUusr[0] + "\n");
                    String[] CPUusage = CPUusr[0].split("User");
                    String[] SYSusage = CPUusr[1].split("System");
                    tv.append("CPU:" + CPUusage[1].trim() + " length:" + CPUusage[1].trim().length() + "\n");
                    tv.append("SYS:" + SYSusage[1].trim() + " length:" + SYSusage[1].trim().length() + "\n");

                    rate = Integer.parseInt(CPUusage[1].trim()) + Integer.parseInt(SYSusage[1].trim());
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getProcessCpuRate: "+rate);
        return rate;
    }
    /**
     * 获取当前进程的CPU使用率
     * @return CPU的使用率
     */
    public static float getCurProcessCpuRate()
    {
        float totalCpuTime1 = getTotalCpuTime();
        float processCpuTime1 = getAppCpuTime();
        try
        {
            Thread.sleep(360);
        }
        catch (Exception e)
        {
        }
        float totalCpuTime2 = getTotalCpuTime();
        float processCpuTime2 = getAppCpuTime();
        float cpuRate = 100 * (processCpuTime2 - processCpuTime1)
                / (totalCpuTime2 - totalCpuTime1);
        return cpuRate;
    }
    /**
     * 获取总的CPU使用率
     * @return CPU使用率
     */
    public static float getTotalCpuRate() {
        float totalCpuTime1 = getTotalCpuTime();
        float totalUsedCpuTime1 = totalCpuTime1 - sStatus.idletime;
        try {
            Thread.sleep(360);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        float totalCpuTime2 = getTotalCpuTime();
        float totalUsedCpuTime2 = totalCpuTime2 - sStatus.idletime;
        float cpuRate = 100 * (totalUsedCpuTime2 - totalUsedCpuTime1)
                / (totalCpuTime2 - totalCpuTime1);
        return cpuRate;
    }
    /**
     * 获取系统总CPU使用时间
     * @return 系统CPU总的使用时间
     */
    public static long getTotalCpuTime()
    {
        String[] cpuInfos = null;
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
//   long totalCpu = Long.parseLong(cpuInfos[2])
//       + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
//       + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
//       + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        sStatus.usertime = Long.parseLong(cpuInfos[2]);
        sStatus.nicetime = Long.parseLong(cpuInfos[3]);
        sStatus.systemtime = Long.parseLong(cpuInfos[4]);
        sStatus.idletime = Long.parseLong(cpuInfos[5]);
        sStatus.iowaittime = Long.parseLong(cpuInfos[6]);
        sStatus.irqtime = Long.parseLong(cpuInfos[7]);
        sStatus.softirqtime = Long.parseLong(cpuInfos[8]);
        return sStatus.getTotalTime();
    }
    /**
     * 获取当前进程的CPU使用时间
     * @return 当前进程的CPU使用时间
     */
    public static long getAppCpuTime()
    {
        // 获取应用占用的CPU时间
        String[] cpuInfos = null;
        try
        {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            cpuInfos = load.split(" ");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        long appCpuTime = Long.parseLong(cpuInfos[13])
                + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                + Long.parseLong(cpuInfos[16]);
        return appCpuTime;
    }
    static Status sStatus = new Status();
    static class Status {
        public long usertime;
        public long nicetime;
        public long systemtime;
        public long idletime;
        public long iowaittime;
        public long irqtime;
        public long softirqtime;
        public long getTotalTime() {
            return (usertime + nicetime + systemtime + idletime + iowaittime
                    + irqtime + softirqtime);
        }
    }
}
