package bean;

import util.Utility;

/**
 * Created by yy on 2018/6/22.
 */

public class BasePacket {
    public byte version;//协议版本号
    public short msgId;//消息ID
    public short msgAttribute;//消息属性
    public long phonenum;//终端手机号
    public short msgnum;//消息流水号
    public byte has;//预留

    public BasePacket(byte version, short msgId, short msgAttribute, long phonenum,
                      short msgnum, byte has) {
        this.version = version;
        this.msgId = msgId;
        this.msgAttribute = msgAttribute;
        this.phonenum = phonenum;
        this.msgnum = msgnum;
        this.has = has;
    }

    public byte getHas() {
        return has;
    }

    public void setHas(byte has) {
        this.has = has;
    }

    public short getMsgAttribute() {
        return msgAttribute;
    }

    public void setMsgAttribute(short msgAttribute) {
        this.msgAttribute = msgAttribute;
    }

    public short getMsgId() {
        return msgId;
    }

    public void setMsgId(short msgId) {
        this.msgId = msgId;
    }

    public short getMsgnum() {
        return msgnum;
    }

    public void setMsgnum(short msgnum) {
        this.msgnum = msgnum;
    }

    public long getPhonenum() {
        return phonenum;
    }

    public void setPhonenum(long phonenum) {
        this.phonenum = phonenum;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }
    public short getMsgLength(short msgAttribute) throws Exception {
        byte lattribute = Utility.short2Byte(msgAttribute)[1];
        byte tattribute = Utility.short2Byte(msgAttribute)[0];
        if ((tattribute & 1) == 0) {
            return (short) lattribute;
        } else {
            return (short) (lattribute + 256);
        }
    }
}
