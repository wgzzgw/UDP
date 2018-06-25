package bean;

import util.Utility;

/**
 * Created by yy on 2018/6/24.
 */
/*
* 客户端通用应答
* */
public class DevideResponse extends BasePacket{
    private short ResponseNum;//应答流水号
    private short ResponseId;//应答ID
    private byte ResponseControlId;//应答命令ID
    private long ResponseTime;//应答时间
    private byte ResultLength;//结果长度
    private byte[] Result;//结果
    private byte identify=0x7E;
    private byte[] tobyte;
    private int templen;
    public DevideResponse(byte version, short msgId, short msgAttribute, long phonenum, short msgnum, byte has,
                          byte resultLength, byte responseControlId, short responseId, short responseNum,
                          long responseTime, byte[] result) {
        super(version, msgId, msgAttribute, phonenum, msgnum, has);
        ResultLength = resultLength;
        ResponseControlId = responseControlId;
        ResponseId = responseId;
        ResponseNum = responseNum;
        ResponseTime = responseTime;
        Result = result;
        try {
            templen=getMsgLength(msgAttribute);
            tobyte=new byte[1+16+templen+1+1];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setTime(byte[] a){
        tobyte[22]=a[0];
        tobyte[23]=a[1];
        tobyte[24]=a[2];
        tobyte[25]=a[3];
        tobyte[26]=a[4];
        tobyte[27]=a[5];
        tobyte[28]=a[6];
        tobyte[29]=a[7];
    }
    public byte[] getbytes(){
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
        tobyte[17]=Utility.short2Byte(ResponseNum)[0];
        tobyte[18]=Utility.short2Byte(ResponseNum)[1];
        tobyte[19]=Utility.short2Byte(ResponseId)[0];
        tobyte[20]=Utility.short2Byte(ResponseId)[1];
        tobyte[21]=ResponseControlId;
        /*tobyte[22]=Utility.long2Byte(ResponseTime)[0];
        tobyte[23]=Utility.long2Byte(ResponseTime)[1];
        tobyte[24]=Utility.long2Byte(ResponseTime)[2];
        tobyte[25]=Utility.long2Byte(ResponseTime)[3];
        tobyte[26]=Utility.long2Byte(ResponseTime)[4];
        tobyte[27]=Utility.long2Byte(ResponseTime)[5];
        tobyte[28]=Utility.long2Byte(ResponseTime)[6];
        tobyte[29]=Utility.long2Byte(ResponseTime)[7];*/
        tobyte[30]=ResultLength;
        int temp=templen-14;
        if(temp>0) {
            for (int i=0;i<temp;i++){
                tobyte[31+i]=Result[i];
            }
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
    public byte getResponseControlId() {
        return ResponseControlId;
    }

    public void setResponseControlId(byte responseControlId) {
        ResponseControlId = responseControlId;
    }

    public byte getResultLength() {
        return ResultLength;
    }

    public void setResultLength(byte resultLength) {
        ResultLength = resultLength;
    }

    public byte[] getResult() {
        return Result;
    }

    public void setResult(byte[] result) {
        Result = result;
    }

    public short getResponseId() {
        return ResponseId;
    }

    public void setResponseId(short responseId) {
        ResponseId = responseId;
    }

    public long getResponseTime() {
        return ResponseTime;
    }

    public void setResponseTime(long responseTime) {
        ResponseTime = responseTime;
    }

    public short getResponseNum() {
        return ResponseNum;
    }

    public void setResponseNum(short responseNum) {
        ResponseNum = responseNum;
    }
}
