package util;

/**
 * Created by yy on 2018/6/22.
 */

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * 各基础类型与byte之间的转换
 *
 */
public class Utility {
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    /**
     * 将short转成byte[2]
     *
     * @param a
     * @return
     */
    public static byte[] short2Byte(short a) {
        byte[] b = new byte[2];

        b[0] = (byte) (a >> 8);
        b[1] = (byte) (a);

        return b;
    }

    /**
     * 将short转成byte[2]
     *
     * @param a
     * @param b
     * @param offset
     *            b中的偏移量
     */
    public static void short2Byte(short a, byte[] b, int offset) {
        b[offset] = (byte) (a >> 8);
        b[offset + 1] = (byte) (a);
    }

    /**
     * 将byte[2]转换成short
     *
     * @param b
     * @return
     */
    public static short byte2Short(byte[] b) {
        return (short) (((b[0] & 0xff) << 8) | (b[1] & 0xff));
    }

    /**
     * 将byte[2]转换成short
     *
     * @param b
     * @param offset
     * @return
     */
    public static short byte2Short(byte[] b, int offset) {
        return (short) (((b[offset] & 0xff) << 8) | (b[offset + 1] & 0xff));
    }

    /**
     * long转byte[8]
     *
     * @param a
     * @param b
     * @param offset
     *            b的偏移量
     */
    public static void long2Byte(long a, byte[] b, int offset) {
        b[offset + 0] = (byte) (a >> 56);
        b[offset + 1] = (byte) (a >> 48);
        b[offset + 2] = (byte) (a >> 40);
        b[offset + 3] = (byte) (a >> 32);

        b[offset + 4] = (byte) (a >> 24);
        b[offset + 5] = (byte) (a >> 16);
        b[offset + 6] = (byte) (a >> 8);
        b[offset + 7] = (byte) (a);
    }

    /**
     * byte[8]转long
     *
     * @param b
     * @param offset
     *            b的偏移量
     * @return
     */
    public static long byte2Long(byte[] b, int offset) {
        return ((((long) b[offset + 0] & 0xff) << 56)
                | (((long) b[offset + 1] & 0xff) << 48)
                | (((long) b[offset + 2] & 0xff) << 40)
                | (((long) b[offset + 3] & 0xff) << 32)

                | (((long) b[offset + 4] & 0xff) << 24)
                | (((long) b[offset + 5] & 0xff) << 16)
                | (((long) b[offset + 6] & 0xff) << 8) | (((long) b[offset + 7] & 0xff) << 0));
    }

    /**
     * byte[8]转long
     *
     * @param b
     * @return
     */
    public static long byte2Long(byte[] b) {
        return ((b[0] & 0xff) << 56) | ((b[1] & 0xff) << 48)
                | ((b[2] & 0xff) << 40) | ((b[3] & 0xff) << 32) |

                ((b[4] & 0xff) << 24) | ((b[5] & 0xff) << 16)
                | ((b[6] & 0xff) << 8) | (b[7] & 0xff);
    }

    /**
     * long转byte[8]
     *
     * @param a
     * @return
     */
    public static byte[] long2Byte(long a) {
        byte[] b = new byte[4 * 2];

        b[0] = (byte) (a >> 56);
        b[1] = (byte) (a >> 48);
        b[2] = (byte) (a >> 40);
        b[3] = (byte) (a >> 32);

        b[4] = (byte) (a >> 24);
        b[5] = (byte) (a >> 16);
        b[6] = (byte) (a >> 8);
        b[7] = (byte) (a >> 0);

        return b;
    }

    /**
     * byte数组转int
     *
     * @param b
     * @return
     */
    public static int byte2Int(byte[] b) {
        return ((b[0] & 0xff) << 24) | ((b[1] & 0xff) << 16)
                | ((b[2] & 0xff) << 8) | (b[3] & 0xff);
    }

    /**
     * byte数组转int
     *
     * @param b
     * @param offset
     * @return
     */
    public static int byte2Int(byte[] b, int offset) {
        return ((b[offset++] & 0xff) << 24) | ((b[offset++] & 0xff) << 16)
                | ((b[offset++] & 0xff) << 8) | (b[offset++] & 0xff);
    }

    /**
     * int转byte数组
     *
     * @param a
     * @return
     */
    public static byte[] int2Byte(int a) {
        byte[] b = new byte[4];
        b[0] = (byte) (a >> 24);
        b[1] = (byte) (a >> 16);
        b[2] = (byte) (a >> 8);
        b[3] = (byte) (a);

        return b;
    }

    /**
     * int转byte数组
     *
     * @param a
     * @param b
     * @param offset
     * @return
     */
    public static void int2Byte(int a, byte[] b, int offset) {
        b[offset++] = (byte) (a >> 24);
        b[offset++] = (byte) (a >> 16);
        b[offset++] = (byte) (a >> 8);
        b[offset++] = (byte) (a);
    }

    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return "" + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }
    public static byte[] float2byte(float f) {
        int a1intbits = Float.floatToIntBits(f);// 将float里面的二进制串解释为int整数
        byte b[] = new byte[4];
        b[3] = (byte) ((a1intbits >> 24));
        b[2] = (byte) ((a1intbits >> 16));
        b[1] = (byte) ((a1intbits >> 8));
        b[0] = (byte) ((a1intbits >> 0));
        return b;
    }
    public static float byte2float(byte[] b) throws IOException {
        byte[] a = b;
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(a));
        float f = dis.readFloat();
        dis.close();
        return f;
    }
    public static String bytesToHexFun2(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        int index = 0;
        for(byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
        }

        return new String(buf);
    }
}

