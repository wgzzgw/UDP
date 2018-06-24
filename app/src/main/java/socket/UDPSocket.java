package socket;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import application.MyApplication;
import bean.Constants;
import bean.Heartbeat;
import bean.HelloData;
import bean.ServerHelloData;
import timer.HeartbeatTimer;
import util.Utility;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by yy on 2018/6/22.
 */
public class UDPSocket implements SensorEventListener {
    //服务端IP地址
    private static final String SERVER_IP = "120.55.161.203";
    // 服务端端口号
    public static final int SERVER_PORT = 9202;
    public static short HELLO = 0x1001;//客户端消息ID
    public static short HEART=0x1000;//心跳包消息ID
    public static short MSGLENGTH = 33;
    public static long PHONENUM=Long.valueOf("0000013570466112");
    public static short MSGNUM = 1;//客户端应答流水号
    public static byte HAS = 0x3c;
    public static short MACHINE_TYPE = 403;
    public static byte CUSTOMER_NAME = 2;
    public static byte SIM_OPERATOR = 1;
    public static short VERSIONCODE=1;
    public static byte[] VINCODE=new byte[17];
    private static final String TAG = "UDPSocket";
    // 单个CPU线程池大小
    private static final int POOL_SIZE = 5;
    //缓冲区大小
    private static final int BUFFER_LENGTH = 1024;
    //缓存数组
    private byte[] receiveByte = new byte[BUFFER_LENGTH];
    //当前线程是否运行
    private boolean isThreadRunning = false;
    private Context mContext;
    private DatagramSocket client;
    private DatagramPacket receivePacket;
    //最近最新接收包的时间
    private long lastReceiveTime = 0;
    //超时接收时间 两分钟收不到认为离线
    private static final long TIME_OUT = 120 * 1000;
    //心跳包接收的时间
    private static final long HEARTBEAT_MESSAGE_DURATION = 27 * 1000;
    //建立ExecutorService线程池
    private ExecutorService mThreadPool;
    //客户端线程 用于接收数据包
    private Thread clientThread;
    //定时器
    private HeartbeatTimer timer;
    //Hello数据
    private HelloData deviceHello;
    //信号量
    private boolean isOK;
    //心跳包
    public Heartbeat heart;
    private SensorManager mSensorManager;
    //传感器XYZ
    private short x,y,z;
    public UDPSocket(Context context) {
        this.mContext = context;
        //得到手机的CPU核数
        int cpuNumbers = Runtime.getRuntime().availableProcessors();
        // 根据CPU数目初始化线程池
        mThreadPool = Executors.newFixedThreadPool(cpuNumbers * POOL_SIZE);
        // 记录创建对象时的时间
        lastReceiveTime = System.currentTimeMillis();
        //hello数据报
         deviceHello = new HelloData(
                (byte)0x2,HELLO,MSGLENGTH,PHONENUM,MSGNUM,HAS,MACHINE_TYPE,
                CUSTOMER_NAME,SIM_OPERATOR,new byte[]{1},VERSIONCODE,VINCODE);
        isOK=false;
        heart=new Heartbeat((byte)0x2,HEART,(short)55,PHONENUM,MSGNUM,HAS);
        heart.init(MyApplication.getContext());
        mSensorManager = (SensorManager)MyApplication.getContext().getSystemService(SENSOR_SERVICE);// 获取传感器管理服务
        // 为系统的方向传感器注册监听器
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
        try {
            Thread.sleep(360);
        }catch (Exception e){
            e.printStackTrace();
        }
        heart.setSpeed((byte) x);
        heart.setSensorX(x);
        Log.d(TAG, "UDPSocketX: "+heart.getSensorX());
        heart.setSensorY(y);
        Log.d(TAG, "UDPSocketY: "+heart.getSensorY());
        heart.setSensorZ(z);
        Log.d(TAG, "UDPSocketZ: "+heart.getSensorZ());
        heart.setSoc((short)10);
        heart.setOdo(101);
        heart.setSocStatus((byte)0x31);
        heart.setVolt((short)220);
    }
    //启动socket
    public void startUDPSocket() {
        if (client != null) return;
        try {
            client = new DatagramSocket();
            if (receivePacket == null) {
                // 创建接受数据的 packet
                receivePacket = new DatagramPacket(receiveByte, BUFFER_LENGTH);
            }
            //启动发送和接收数据的线程
            startSocketThread();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    /**
     * 开启接收数据的线程
     */
    private void startSocketThread() {
        clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "clientThread is running...");
                //接收数据会阻塞 因此放入子线程
                receiveMessage();
            }
        });
        isThreadRunning = true;
        clientThread.start();
        //开启首次发送hello包
        startHelloPacket();
    }

    private void startHelloPacket() {
        sendMessage(deviceHello);
        while(true){
            if(isOK){
                //开启定时器  发送心跳包
                startHeartbeatTimer();
                break;
            }
        }
    }
    /**
     * 发送消息 hello
     *
     * @param message
     */
    public void sendMessage(final HelloData message) {
        //往线程池放发送任务  消息类型依据传参
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] data = message.getBytes();
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(Utility.byteToBit(data[6]));
                    buffer.append(Utility.byteToBit(data[7]));
                    buffer.append(Utility.byteToBit(data[8]));
                    buffer.append(Utility.byteToBit(data[9]));
                    buffer.append(Utility.byteToBit(data[10]));
                    buffer.append(Utility.byteToBit(data[11]));
                    buffer.append(Utility.byteToBit(data[12]));
                    buffer.append(Utility.byteToBit(data[13]));
                    String phonenumtemp = new String(buffer);
                    Log.d(TAG, "run: " + "客户端的手机号：" + Long.parseLong(phonenumtemp, 2));
                    InetAddress targetAddress = InetAddress.getByName(SERVER_IP);
                    //创建数据报，包含发送的数据信息
                    DatagramPacket packet = new DatagramPacket(data, data.length, targetAddress, SERVER_PORT);
                    client.send(packet);
                    // 数据发送事件
                    Log.d(TAG, "数据发送成功");
                    Log.d(TAG, "run: " + "成功发送:"  + ",消息ID:0x" +
                            Integer.toHexString(Utility.byte2Short(new byte[]{data[2], data[3]})));
                    /*//创建数据报，用于接收服务器端响应的数据
                    byte[] data2 = new byte[1024];
                    DatagramPacket packet2 = new DatagramPacket(data2, data2.length);
                    // 接收服务器响应的数据
                    client.receive(packet2);
                    StringBuffer sbuffer = new StringBuffer();
                    sbuffer.append(Utility.byteToBit(packet2.getData()[6]));
                    sbuffer.append(Utility.byteToBit(packet2.getData()[7]));
                    sbuffer.append(Utility.byteToBit(packet2.getData()[8]));
                    sbuffer.append(Utility.byteToBit(packet2.getData()[9]));
                    sbuffer.append(Utility.byteToBit(packet2.getData()[10]));
                    sbuffer.append(Utility.byteToBit(packet2.getData()[11]));
                    sbuffer.append(Utility.byteToBit(packet2.getData()[12]));
                    sbuffer.append(Utility.byteToBit(packet2.getData()[13]));
                    String sphonenumtemp = new String(sbuffer);
                    Long sphonenum = Long.parseLong(sphonenumtemp, 2);
                    ServerHelloData serverHello = new ServerHelloData(
                            packet2.getData()[1],
                            Utility.byte2Short(new byte[]{packet2.getData()[2], packet2.getData()[3]}),
                            Utility.byte2Short(new byte[]{packet2.getData()[4], packet2.getData()[5]}),
                            sphonenum,
                            Utility.byte2Short(new byte[]{packet2.getData()[14], packet2.getData()[15]}),
                            packet2.getData()[16],
                            Utility.byte2Short(new byte[]{packet2.getData()[17], packet2.getData()[18]}),
                            Utility.byte2Short(new byte[]{packet2.getData()[19], packet2.getData()[20]}),
                            packet2.getData()[21]
                    );
                    Log.d(TAG, "run: " + "服务器返回的消息ID：" + Integer.toHexString(serverHello.getMsgId()));
                    Log.d(TAG, "run: " + "服务器返回的消息结果：" + serverHello.getResult());
                    Log.d(TAG, "run: " + "服务器返回的消息应答ID：0x" + Integer.toHexString(serverHello.getAnswerid()));
                    Log.d(TAG, "run: " + "服务器返回的消息应答流水号：" + serverHello.getAnswernum());
                    Log.d(TAG, "run: " + "服务器返回的消息终端手机号：" + serverHello.getPhonenum());
                    Log.d(TAG, "run: " + "服务器返回的消息预留：0x" + Integer.toHexString(serverHello.getHas()));
                    isOK=true;*/
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 发送心跳包
     *
     * @param message
     */
    public void sendMessage(final byte[] message) {
        //往线程池放发送任务  消息类型依据传参
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress targetAddress = InetAddress.getByName(SERVER_IP);
                    //创建数据报，包含发送的数据信息
                    DatagramPacket packet = new DatagramPacket
                            (message, message.length, targetAddress, SERVER_PORT);
                    client.send(packet);
                    // 数据发送事件
                    Log.d(TAG, "心跳包发送成功");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 处理接受到的消息
     */
    private void receiveMessage() {
        while (isThreadRunning) {
            try {
                if (client != null) {
                    client.receive(receivePacket);
                }
                //接受到数据刷新时间
                lastReceiveTime = System.currentTimeMillis();
                Log.d(TAG, "receive packet success...");
            } catch (IOException e) {
                Log.e(TAG, "UDP数据包接收失败！线程停止");
                stopUDPSocket();
                e.printStackTrace();
                return;
            }

            if (receivePacket == null || receivePacket.getLength() == 0) {
                Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }
            Log.d(TAG,   "message from " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort());
            short type= Utility.byte2Short(new byte[]{receivePacket.getData()[2], receivePacket.getData()[3]});
            switch (type){
                case  Constants.SERVER_HELLO:
                    StringBuffer sbuffer = new StringBuffer();
                    sbuffer.append(Utility.byteToBit(receivePacket.getData()[6]));
                    sbuffer.append(Utility.byteToBit(receivePacket.getData()[7]));
                    sbuffer.append(Utility.byteToBit(receivePacket.getData()[8]));
                    sbuffer.append(Utility.byteToBit(receivePacket.getData()[9]));
                    sbuffer.append(Utility.byteToBit(receivePacket.getData()[10]));
                    sbuffer.append(Utility.byteToBit(receivePacket.getData()[11]));
                    sbuffer.append(Utility.byteToBit(receivePacket.getData()[12]));
                    sbuffer.append(Utility.byteToBit(receivePacket.getData()[13]));
                    String sphonenumtemp = new String(sbuffer);
                    Long sphonenum = Long.parseLong(sphonenumtemp, 2);
                    ServerHelloData serverHello = new ServerHelloData(
                            receivePacket.getData()[1],
                            Utility.byte2Short(new byte[]{receivePacket.getData()[2], receivePacket.getData()[3]}),
                            Utility.byte2Short(new byte[]{receivePacket.getData()[4], receivePacket.getData()[5]}),
                            sphonenum,
                            Utility.byte2Short(new byte[]{receivePacket.getData()[14], receivePacket.getData()[15]}),
                            receivePacket.getData()[16],
                            Utility.byte2Short(new byte[]{receivePacket.getData()[17], receivePacket.getData()[18]}),
                            Utility.byte2Short(new byte[]{receivePacket.getData()[19], receivePacket.getData()[20]}),
                            receivePacket.getData()[21]
                    );
                    String origin=Utility.bytesToHexFun2(receivePacket.getData());
                    Log.d(TAG, "receiveMessage: 服务端原始数据"+origin);
                    Log.d(TAG, "run: " + "服务器返回的消息ID：" + Integer.toHexString(serverHello.getMsgId()));
                    Log.d(TAG, "run: " + "服务器返回的消息结果：" + serverHello.getResult());
                    Log.d(TAG, "run: " + "服务器返回的消息应答ID：0x" + Integer.toHexString(serverHello.getAnswerid()));
                    Log.d(TAG, "run: " + "服务器返回的消息应答流水号：" + serverHello.getAnswernum());
                    Log.d(TAG, "run: " + "服务器返回的消息终端手机号：" + serverHello.getPhonenum());
                    Log.d(TAG, "run: " + "服务器返回的消息预留：0x" + Integer.toHexString(serverHello.getHas()));
                    isOK=true;
                    break;
                case Constants.HEART:
                    for(int i=0;i<receivePacket.getData().length;i++) {
                        Log.d(TAG, "receiveMessageHeart: " + receivePacket.getData()[i]);
                    }
                    break;
                case Constants.CONTROL:
                    if(receivePacket.getData()[17]==(short)0x01){
                        Log.d(TAG, "receiveMessage: "+"d");
                    }
            }
            // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (receivePacket != null) {
                receivePacket.setLength(BUFFER_LENGTH);
            }
        }
    }
    /**
     * 启动心跳，timer 间隔十秒
     */
    private void startHeartbeatTimer()  {
        timer = new HeartbeatTimer();
        timer.setOnScheduleListener(new HeartbeatTimer.OnScheduleListener() {
            @Override
            public void onSchedule() {
                Log.d(TAG, "timer is onSchedule...");
                long duration = System.currentTimeMillis() - lastReceiveTime;
                Log.d(TAG, "duration:" + duration);
                if (duration > TIME_OUT) {//若超过两分钟都没收到我的心跳包，则认为对方不在线。
                    Log.d(TAG, "超时，对方已经下线");
                    // 刷新时间，重新进入下一个心跳周期
                    lastReceiveTime = System.currentTimeMillis();
                }
                    byte[] tempbyte=heart.getBytes();
                Log.d(TAG, "onScheduleyyyy: "+Utility.bytesToHexFun2(tempbyte));
                try {
                    Log.d(TAG, "onSchedule: " + "测试经纬度"
                            + Utility.byte2float(new byte[]{tempbyte[18], tempbyte[19],
                            tempbyte[20], tempbyte[21]}));
                    Log.d(TAG, "onSchedule: 16进制维度"+Utility.bytesToHexFun2(new byte[]{
                            tempbyte[18], tempbyte[19],
                            tempbyte[20], tempbyte[21]
                    }));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sendMessage(tempbyte);
            }

        });
        //无延时27秒一次任务
        timer.startTimer(0, HEARTBEAT_MESSAGE_DURATION);
    }
    public void stopUDPSocket() {
        isThreadRunning = false;
        receivePacket = null;
        isOK=false;
        if (clientThread != null) {
            clientThread.interrupt();
        }
        if (client != null) {
            client.close();
            client = null;
        }
        if (timer != null) {
            timer.exit();
        }
        // 取消注册传感器监听
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()==Sensor.TYPE_ORIENTATION){
            //如果是方向传感器
            //x该值表示方位，0代表北（North）；90代表东（East）；
             x = (short)sensorEvent.values[SensorManager.DATA_X];
            //y值表示倾斜度，或手机翘起的程度。
             y = (short)sensorEvent.values[SensorManager.DATA_Y];
            //z值表示手机沿着Y轴的滚动角度。表示手机沿着Y轴的滚动角度。取值范围是-90≤z值≤90。
             z = (short)sensorEvent.values[SensorManager.DATA_Z];
        }else if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            //如果是加速度传感器
             x = (short)sensorEvent.values[SensorManager.DATA_X];
             y = (short)sensorEvent.values[SensorManager.DATA_Y];
             z = (short)sensorEvent.values[SensorManager.DATA_Z];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
