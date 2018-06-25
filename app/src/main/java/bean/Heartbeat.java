package bean;

import android.content.Context;
import android.location.Location;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import application.MyApplication;
import util.CpuManager;
import util.LocationUtil;
import util.MemInfo;
import util.Utility;

/**
 * Created by yy on 2018/6/22.
 */
/*
* 心跳包
* */
public class Heartbeat extends BasePacket{
    private static final String TAG = "Heartbeat";
    private static Location location;
    private byte gps;//GPS
    private float latitude;//维度
    private float longitude;//经度
    private short speed;//当前速度
    private short sensorX;//传感器X
    private short sensorY;//传感器Y
    private short sensorZ;//传感器Z
    private short cpuTemp;//CPU温度
    private short soc;//电量Soc
    private int odo;//总里程
    private byte socStatus;//充电与非充电状态
    private short volt;//电池电压
    private byte memory;//剩余内存
    private byte cpuUsage;//CPU负载
    private short availableMemory;//设备剩余空间
    private byte bootTime;//开机小时数
    private byte gpsInuse;//GPS可用卫星个数
    private byte gsignal;//通讯信号强度DB绝对值
    private byte[] status;//组合状态位
    private String vincode;//车架号
    private byte identify=0x7E;
    private int templen;
    private byte[] tobyte;
    public void init(Context context){
        this.gps=1;
         location = LocationUtil.getInstance(context).showLocation();
        if(location!=null){
            Log.d(TAG, "init: "+"enter ");
            this.latitude=(float)location.getLatitude();
            Log.d(TAG, "init: "+(float)location.getLatitude());
            this.longitude=(float)location.getLongitude();
            this.speed = (short) ((int) location.getSpeed());
            Log.d(TAG, "init: "+"locationspeed"+location.getSpeed());
        }else {
            this.latitude = 0f;
            this.longitude = 0f;
        }
        this.cpuTemp= CpuManager.getThermalInfo();
        Log.d(TAG, "init: "+"cputemp"+" "+cpuTemp);
        this.memory=(byte) (float)((MemInfo.getmem_UNUSED(MyApplication.getContext())*1.0)
        /(MemInfo.getmem_TOLAL()*1.0)*100);
        Log.d(TAG, "init: "+"memory"+" "+memory);
        this.cpuUsage=(byte)((int)(CpuManager.getTotalCpuRate()));
        Log.d(TAG, "init: "+"testcpuusage1"+" "+CpuManager.getTotalCpuRate());
        Log.d(TAG, "init: "+"testcpuusage2"+" "+cpuUsage);
        Log.d(TAG, "init: "+"testcpuusage3"+" "+(int)(CpuManager.getTotalCpuRate()));
        this.availableMemory=(short)(MemInfo.getRomMemroy()[1]/(1024*1024));
        Log.d(TAG, "init: "+"availableMemory"+" "+availableMemory);
        Log.d(TAG, "init: "+"changebeforeboottime"+(SystemClock.elapsedRealtimeNanos()/(1000*1000*1000*60*60)));
        this.bootTime=(byte)(SystemClock.elapsedRealtimeNanos()/(1000*1000*60*60));
        Log.d(TAG, "init: "+"boottime"+bootTime);
        this.gpsInuse=(byte) LocationUtil.getInstance(context).getCount();
        Log.d(TAG, "init: "+"gpsuse"+gpsInuse);
        startGetSignal(context);
        this.status=new byte[]{0,0,0};
        this.vincode="he";
        Log.d(TAG, "init: speed"+speed);
    }
    public void startGetSignal(Context c){
        //1.获得TelephoneManager:
        TelephonyManager telephoneManager = (TelephonyManager) c.getSystemService(Context.TELEPHONY_SERVICE);
        //2.获取NetworkType
        final int type = telephoneManager.getNetworkType();
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                int count=0;
                super.onSignalStrengthsChanged(signalStrength);
                StringBuffer sb = new StringBuffer();
                String strength = String.valueOf(signalStrength
                        .getGsmSignalStrength());
                if (type == TelephonyManager.NETWORK_TYPE_UMTS
                        || type == TelephonyManager.NETWORK_TYPE_HSDPA) {
                    sb.append("联通3g").append("信号强度:").append(strength);
                } else if (type == TelephonyManager.NETWORK_TYPE_GPRS
                        || type == TelephonyManager.NETWORK_TYPE_EDGE) {
                    sb.append("移动或者联通2g").append("信号强度:").append(strength);
                }else if(type==TelephonyManager.NETWORK_TYPE_CDMA){
                    sb.append("电信2g").append("信号强度:").append(strength);
                }else if(type==TelephonyManager.NETWORK_TYPE_EVDO_0
                        ||type==TelephonyManager.NETWORK_TYPE_EVDO_A){
                    sb.append("电信3g").append("信号强度:").append(strength);

                }else{
                    sb.append("非以上信号").append("信号强度:").append(strength);
                }
                Log.d(TAG, "onSignalStrengthsChanged: "+sb.toString());
                count=(byte)(Integer.parseInt(strength.trim()));
                gsignal=(byte)count;
                Log.d(TAG, "init: signal"+gsignal);
            }
        };
        telephoneManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }
    public Heartbeat(byte version, short msgId, short msgAttribute, long phonenum,
                     short msgnum, byte has){
        super(version, msgId, msgAttribute, phonenum, msgnum, has);
        try {
            templen=getMsgLength(msgAttribute);
            tobyte=new byte[16+1+templen+1+1];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   public byte[] getBytes(){
       tobyte[0]=identify;//标识位
       tobyte[1]=version;//协议版本号
       tobyte[2]= Utility.short2Byte(msgId)[0];
       tobyte[3]=Utility.short2Byte(msgId)[1];
       tobyte[4]=Utility.short2Byte(msgAttribute)[0];
       tobyte[5]=Utility.short2Byte(msgAttribute)[1];
       tobyte[6]=Utility.long2Byte(phonenum)[0];
       tobyte[7]=Utility.long2Byte(phonenum)[1];
       tobyte[8]=Utility.long2Byte(phonenum)[2];
       tobyte[9]=Utility.long2Byte(phonenum)[3];
       tobyte[10]=Utility.long2Byte(phonenum)[4];
       tobyte[11]=Utility.long2Byte(phonenum)[5];
       tobyte[12]=Utility.long2Byte(phonenum)[6];
       tobyte[13]=Utility.long2Byte(phonenum)[7];
       tobyte[14]=Utility.short2Byte(msgnum)[0];
       tobyte[15]=Utility.short2Byte(msgnum)[1];
       tobyte[16]=has;
       tobyte[17]=gps;
       tobyte[18]=Utility.float2byte(latitude)[0];
       tobyte[19]=Utility.float2byte(latitude)[1];
       tobyte[20]=Utility.float2byte(latitude)[2];
       tobyte[21]=Utility.float2byte(latitude)[3];
       tobyte[22]=Utility.float2byte(longitude)[0];
       tobyte[23]=Utility.float2byte(longitude)[1];
       tobyte[24]=Utility.float2byte(longitude)[2];
       tobyte[25]=Utility.float2byte(longitude)[3];
       tobyte[26]=Utility.short2Byte(speed)[0];
       tobyte[27]=Utility.short2Byte(speed)[1];
       tobyte[28]=Utility.short2Byte(sensorX)[0];
       tobyte[29]=Utility.short2Byte(sensorX)[1];
       tobyte[30]=Utility.short2Byte(sensorY)[0];
       tobyte[31]=Utility.short2Byte(sensorY)[1];
       tobyte[32]=Utility.short2Byte(sensorZ)[0];
       tobyte[33]=Utility.short2Byte(sensorZ)[1];
       tobyte[34]=Utility.short2Byte(cpuTemp)[0];
       tobyte[35]=Utility.short2Byte(cpuTemp)[1];
       tobyte[36]=Utility.short2Byte(soc)[0];
       tobyte[37]=Utility.short2Byte(soc)[1];
       tobyte[38]=Utility.int2Byte(odo)[0];
       tobyte[39]=Utility.int2Byte(odo)[1];
       tobyte[40]=Utility.int2Byte(odo)[2];
       tobyte[41]=Utility.int2Byte(odo)[3];
       tobyte[42]=socStatus;
       tobyte[43]=Utility.short2Byte(volt)[0];
       tobyte[44]=Utility.short2Byte(volt)[1];
       tobyte[45]=memory;
       tobyte[46]=cpuUsage;
       tobyte[47]=Utility.short2Byte(availableMemory)[0];
       tobyte[48]=Utility.short2Byte(availableMemory)[1];
       tobyte[49]=bootTime;
       tobyte[50]=gpsInuse;
       tobyte[51]=gpsInuse;
       tobyte[52]=gsignal;
       tobyte[53]=status[0];
       tobyte[54]=status[1];
       tobyte[55]=status[2];
       for(int i=0;i<vincode.getBytes().length;i++){
           tobyte[56+i]=vincode.getBytes()[i];
       }
       tobyte[16+1+templen]=check();
       tobyte[16+1+templen+1]=identify;
       return tobyte;
   }
    public byte check(){
        byte temp=tobyte[1];
        for(int i=1;i<1+templen+16;i++)
            temp=(byte)(temp^tobyte[i+1]);
        return temp;
    }
   /* public void setMsg(String msg){
        for(int i=0;i<msg.getBytes().length;i++){
            tobyte[72+i]=msg.getBytes()[i];
        }
    }*/
    public short getAvailableMemory() {
        return availableMemory;
    }

    public void setAvailableMemory(short availableMemory) {
        this.availableMemory = availableMemory;
    }

    public byte getBootTime() {
        return bootTime;
    }

    public void setBootTime(byte bootTime) {
        this.bootTime = bootTime;
    }

    public short getCpuTemp() {
        return cpuTemp;
    }

    public void setCpuTemp(short cpuTemp) {
        this.cpuTemp = cpuTemp;
    }

    public byte getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(byte cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public byte getGps() {
        return gps;
    }

    public void setGps(byte gps) {
        this.gps = gps;
    }

    public byte getGpsInuse() {
        return gpsInuse;
    }

    public void setGpsInuse(byte gpsInuse) {
        this.gpsInuse = gpsInuse;
    }

    public byte getGsignal() {
        return gsignal;
    }

    public void setGsignal(byte gsignal) {
        this.gsignal = gsignal;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public byte getMemory() {
        return memory;
    }

    public void setMemory(byte memory) {
        this.memory = memory;
    }

    public int getOdo() {
        return odo;
    }

    public void setOdo(int odo) {
        this.odo = odo;
    }

    public short getSensorX() {
        return sensorX;
    }

    public void setSensorX(short sensorX) {
        this.sensorX = sensorX;
    }

    public short getSensorY() {
        return sensorY;
    }

    public void setSensorY(short sensorY) {
        this.sensorY = sensorY;
    }

    public short getSensorZ() {
        return sensorZ;
    }

    public void setSensorZ(short sensorZ) {
        this.sensorZ = sensorZ;
    }

    public short getSoc() {
        return soc;
    }

    public void setSoc(short soc) {
        this.soc = soc;
    }

    public byte getSocStatus() {
        return socStatus;
    }

    public void setSocStatus(byte socStatus) {
        this.socStatus = socStatus;
    }

    public short getSpeed() {
        return speed;
    }

    public void setSpeed(short speed) {
        this.speed = speed;
    }

    public byte[] getStatus() {
        return status;
    }

    public void setStatus(byte[] status) {
        this.status = status;
    }

    public String getVincode() {
        return vincode;
    }

    public void setVincode(String vincode) {
        this.vincode = vincode;
    }

    public short getVolt() {
        return volt;
    }

    public void setVolt(short volt) {
        this.volt = volt;
    }
}
