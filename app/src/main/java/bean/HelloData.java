package bean;

import android.util.Log;

import util.Utility;

/**
 * Created by yy on 2018/6/22.
 */
/*
* 客户端 hello包
* */
public class HelloData extends BasePacket{
    private static final String TAG = "HelloData";
    private short machine_type;//0x0403
    private byte customer_name;
    private byte simOperator;
    private byte[] icc_id;//大小=10
    private short VesionCode;
    /*private String VinCode;//17个字节*/
    private byte[] VinCode;//17个字节
    private byte identify=0x7E;
    private int templen;
    private byte[] tobyte;
    public byte[] getBytes(){
        tobyte[0]=identify;//标识位
        tobyte[1]=version;//协议版本号
        tobyte[2]=Utility.short2Byte(msgId)[0];
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
        tobyte[17]=Utility.short2Byte(machine_type)[0];
        tobyte[18]=Utility.short2Byte(machine_type)[1];
        tobyte[19]=customer_name;
        tobyte[20]=simOperator;
        for(int i=0;i<icc_id.length;i++){
            tobyte[21+i]=icc_id[i];//10
        }
        tobyte[31]=Utility.short2Byte(VesionCode)[0];
        tobyte[32]=Utility.short2Byte(VesionCode)[1];
    	for(int i=0;i<17;i++){
    		tobyte[33+i]=VinCode[i];//17
    	}
        tobyte[templen+16+1]=check();
        tobyte[templen+16+1+1]=identify;
        return tobyte;
    }
    public byte check(){
        byte temp=tobyte[1];
        for(int i=1;i<1+templen+16;i++)
        temp=(byte)(temp^tobyte[i+1]);
        return temp;
    }
    public HelloData(byte version, short msgId, short msgAttribute, long phonenum,
                short msgnum, byte has, short machine_type, byte customer_name,
                byte simOperator, byte[] icc_id, short vesionCode, byte[] vinCode
    ) {
        super(version,msgId,msgAttribute,phonenum,msgnum,has);
        this.machine_type = machine_type;
        this.customer_name = customer_name;
        this.simOperator = simOperator;
        this.icc_id = icc_id;
        VesionCode = vesionCode;
        VinCode = vinCode;
        try {
            templen=getMsgLength(msgAttribute);
            Log.d(TAG, "HelloData: "+"templen"+templen);
            tobyte=new byte[templen+16+1+1+1];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte getBanben() {
        return version;
    }
    public void setBanben(byte banben) {
        this.version = banben;
    }
    public short getMsgId() {
        return msgId;
    }
    public void setMsgId(short msgId) {
        this.msgId = msgId;
    }
    public short getMsgAttribute() {
        return msgAttribute;
    }
    public void setMsgAttribute(short msgAttribute) {
        this.msgAttribute = msgAttribute;
    }
    public long getPhonenum() {
        return phonenum;
    }
    public void setPhonenum(long phonenum) {
        this.phonenum = phonenum;
    }
    public short getMsgnum() {
        return msgnum;
    }
    public void setMsgnum(short msgnum) {
        this.msgnum = msgnum;
    }
    public byte getHas() {
        return has;
    }
    public void setHas(byte has) {
        this.has = has;
    }
    public short getMachine_type() {
        return machine_type;
    }
    public void setMachine_type(short machine_type) {
        this.machine_type = machine_type;
    }
    public byte getCustomer_name() {
        return customer_name;
    }
    public void setCustomer_name(byte customer_name) {
        this.customer_name = customer_name;
    }
    public byte getSimOperator() {
        return simOperator;
    }
    public void setSimOperator(byte simOperator) {
        this.simOperator = simOperator;
    }
    public byte[] getIcc_id() {
        return icc_id;
    }
    public void setIcc_id(byte[] icc_id) {
        this.icc_id = icc_id;
    }
    public short getVesionCode() {
        return VesionCode;
    }
    public void setVesionCode(short vesionCode) {
        VesionCode = vesionCode;
    }
    public byte[] getVinCode() {
        return VinCode;
    }
    public void setVinCode(byte[] vinCode) {
        VinCode = vinCode;
    }
}
