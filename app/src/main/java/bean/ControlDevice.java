package bean;

/**
 * Created by yy on 2018/6/24.
 */

public class ControlDevice extends BasePacket{
    private byte controlId;//命令ID
    private long controlTime;//命令时间
    private byte controlLength;//消息长度
    private byte[] controlMsg;//消息内容

    public ControlDevice(byte version, short msgId, short msgAttribute, long phonenum, short msgnum, byte has,
                         long controlTime, byte controlId, byte controlLength) {
        super(version, msgId, msgAttribute, phonenum, msgnum, has);
        this.controlTime = controlTime;
        this.controlId = controlId;
        this.controlLength = controlLength;
    }

    public byte getControlId() {
        return controlId;
    }

    public void setControlId(byte controlId) {
        this.controlId = controlId;
    }

    public byte getControlLength() {
        return controlLength;
    }

    public void setControlLength(byte controlLength) {
        this.controlLength = controlLength;
    }

    public byte[] getControlMsg() {
        return controlMsg;
    }

    public void setControlMsg(byte[] controlMsg) {
        this.controlMsg = controlMsg;
    }

    public long getControlTime() {
        return controlTime;
    }

    public void setControlTime(long controlTime) {
        this.controlTime = controlTime;
    }
}
