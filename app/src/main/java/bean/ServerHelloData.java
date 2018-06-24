package bean;

import util.Utility;

/**
 * Created by yy on 2018/6/22.
 */

public class ServerHelloData extends BasePacket{
    private short answernum;
    private short answerid;
    private byte result;
    private byte[] tobyte;
    private byte identify = 0x7E;
    private int templen;

    public byte[] getBytes() {
        tobyte[0] = identify;
        tobyte[1] = version;//协议版本号
        tobyte[2] = Utility.short2Byte(msgId)[0];
        tobyte[3] = Utility.short2Byte(msgId)[1];
        tobyte[4] = Utility.short2Byte(msgAttribute)[0];
        tobyte[5] = Utility.short2Byte(msgAttribute)[1];
        tobyte[6] = Utility.long2Byte(phonenum)[0];
        tobyte[7] = Utility.long2Byte(phonenum)[1];
        tobyte[8] = Utility.long2Byte(phonenum)[2];
        tobyte[9] = Utility.long2Byte(phonenum)[3];
        tobyte[10] = Utility.long2Byte(phonenum)[4];
        tobyte[11] = Utility.long2Byte(phonenum)[5];
        tobyte[12] = Utility.long2Byte(phonenum)[6];
        tobyte[13] = Utility.long2Byte(phonenum)[7];
        tobyte[14] = Utility.short2Byte(msgnum)[0];
        tobyte[15] = Utility.short2Byte(msgnum)[1];
        tobyte[16] = has;
        tobyte[17] = Utility.short2Byte(answernum)[0];
        tobyte[18] = Utility.short2Byte(answernum)[1];
        tobyte[19] = Utility.short2Byte(answerid)[0];
        tobyte[20] = Utility.short2Byte(answerid)[1];
        tobyte[21] = result;
        tobyte[16+1+templen] = check();
        tobyte[16+1+templen+1] = identify;
        return tobyte;
    }
    public byte check(){
        byte temp=tobyte[1];
        for(int i=1;i<1+templen+16;i++)
            temp=(byte)(temp^tobyte[i+1]);
        return temp;
    }
    public ServerHelloData(byte version, short msgId, short msgAttribute,
                        long phonenum, short msgnum, byte has, short answernum,
                        short answerid, byte result) {
        super(version,msgId,msgAttribute,phonenum,msgnum,has);
        this.answernum = answernum;
        this.answerid = answerid;
        this.result = result;
        try {
            templen = getMsgLength(msgAttribute);
            tobyte = new byte[16+1+templen+1+1];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public short getAnswernum() {
        return answernum;
    }

    public void setAnswernum(short answernum) {
        this.answernum = answernum;
    }

    public short getAnswerid() {
        return answerid;
    }

    public void setAnswerid(short answerid) {
        this.answerid = answerid;
    }

    public byte getResult() {
        return result;
    }

    public void setResult(byte result) {
        this.result = result;
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

}
