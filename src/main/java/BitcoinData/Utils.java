package BitcoinData;
import org.bitcoinj.core.Base58;

public class Utils {
    public static final byte APPOINT_INVALID_BYTE = 127;
    private static byte[] APPOINT_INVALID_ADDRESS = {};
    private static short APPOINT_INVALID_SHORT = 0;
    private static int APPOINT_INVALID_INT = 0;
    private static long APPOINT_INVALID_LONG = 0;
    private static double APPOINT_INVALID_DOUBLE = 0;

    public final static byte[] GetAppointInvalidAddress(){
        if(APPOINT_INVALID_ADDRESS.length != Parameter.ADDRESS_SIZE){
            APPOINT_INVALID_ADDRESS = new byte[Parameter.ADDRESS_SIZE];
            for(int i = 0; i < Parameter.ADDRESS_SIZE; i++){
                APPOINT_INVALID_ADDRESS[i] = APPOINT_INVALID_BYTE;
            }
        }
        return APPOINT_INVALID_ADDRESS;
    }

    public final static short GetAppointInvalidShort(){
        if(APPOINT_INVALID_SHORT == 0){
            byte[] appointInvalidShortBytes = new byte[2];
            for(int i = 0; i < appointInvalidShortBytes.length; i++){
                appointInvalidShortBytes[i] = APPOINT_INVALID_BYTE;
            }
            APPOINT_INVALID_SHORT = BytesToShort(appointInvalidShortBytes);
        }
        return APPOINT_INVALID_SHORT;
    }

    public final static int GetAppointInvalidInt(){
        if(APPOINT_INVALID_INT == 0){
            byte[] appointInvalidIntBytes = new byte[4];
            for(int i = 0; i < appointInvalidIntBytes.length; i++){
                appointInvalidIntBytes[i] = APPOINT_INVALID_BYTE;
            }
            APPOINT_INVALID_INT = BytesToInt(appointInvalidIntBytes);
        }
        return APPOINT_INVALID_INT;
    }

    public final static long GetAppointInvalidLong(){
        if(APPOINT_INVALID_LONG == 0){
            byte[] appointInvalidLongBytes = new byte[8];
            for(int i = 0; i < appointInvalidLongBytes.length; i++){
                appointInvalidLongBytes[i] = APPOINT_INVALID_BYTE;
            }
            APPOINT_INVALID_LONG = BytesToLong(appointInvalidLongBytes);
        }
        return APPOINT_INVALID_LONG;
    }


    public final static double GetAppointInvalidDouble(){
        if(APPOINT_INVALID_DOUBLE == 0){
            byte[] appointInvalidDoubleBytes = new byte[8];
            for(int i = 0; i < appointInvalidDoubleBytes.length; i++){
                appointInvalidDoubleBytes[i] = APPOINT_INVALID_BYTE;
            }
            APPOINT_INVALID_DOUBLE = BytesToDouble(appointInvalidDoubleBytes);
        }
        return APPOINT_INVALID_DOUBLE;
    }

    public static boolean IsValidAddress(byte[] address){
        return !address.equals(GetAppointInvalidAddress());
    }

    public static byte[] AddressStringToBytes(String addressString){
        byte[] addressBytes = Base58.decode(addressString);
        if(addressBytes.length != 25){
            throw new IllegalArgumentException(
                    String.format("AddressStringToBytes, the length of converted result should be 25, " +
                                  "but %d gotten", addressBytes.length));
        }
        return addressBytes;
    }

    public static String AddressBytesToString(byte[] addressBytes){
        if(addressBytes.length != 25){
            throw new IllegalArgumentException(
                    String.format("AddressBytesToString, the length of addressBytes should be 25, " +
                                  "but %d gotten", addressBytes.length));
        }
        return Base58.encode(addressBytes);
    }

    public static byte[] TransHashStringToBytes(String transHashString){
        byte[] bytes = new byte[transHashString.length() / 2];
        for(int i = 0; i < transHashString.length() / 2; i++) {
            String subStr = transHashString.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        if(bytes.length != 32){
            throw new IllegalArgumentException(
                    String.format("TransHashStringToBytes, the length of converted result should be 32, " +
                                  "but %d gotten", bytes.length));
        }
        return bytes;
    }

    public static String TransHashBytesToString(byte[] transHashBytes){
        if(transHashBytes.length != 32){
            throw new IllegalArgumentException(
                    String.format("TransHashBytesToString, the length of transHashBytes should be 32, " +
                                  "but %d gotten", transHashBytes.length));
        }
        StringBuilder buf = new StringBuilder(transHashBytes.length * 2);
        for(byte b : transHashBytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }
        return buf.toString();
    }

    public static byte[] ShortToBytes(short s) {
        int temp = s;
        byte[] b = new byte[2]; // 将最低位保存在最低位
        b[0] = (byte)(temp & 0xff);
        temp = temp >> 8; // 向右移8位
        b[1] = (byte)(temp & 0xff);
        return b;
    }

    public static short BytesToShort(byte[] bytes) {
        short s = 0;
        short s0 = (short) (bytes[0] & 0xff);// 最低位
        short s1 = (short) (bytes[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    public static byte[] IntToBytes(int i){
        byte[] b = new byte[4];
        b[0] = (byte) ((i & 0xff000000) >> 24);
        b[1] = (byte) ((i & 0x00ff0000) >> 16);
        b[2] = (byte) ((i & 0x0000ff00) >> 8);
        b[3] = (byte)  (i & 0x000000ff);
        return b;
    }

    public static int BytesToInt(byte[] bytes){
        if(bytes.length != 4){
            throw new IllegalArgumentException(
                    String.format("BytesToInt, the length of byte array should be 4, but %d gotten", bytes.length));

        }
        return 	(0xff000000 	& (bytes[0] << 24))  |
                (0x00ff0000 	& (bytes[1] << 16))  |
                (0x0000ff00 	& (bytes[2] << 8))   |
                (0x000000ff 	&  bytes[3]);
    }

    public static byte[] LongToBytes(long l){
        byte b[] = new byte[8];
        b[0] = (byte)  (0xff & (l >> 56));
        b[1] = (byte)  (0xff & (l >> 48));
        b[2] = (byte)  (0xff & (l >> 40));
        b[3] = (byte)  (0xff & (l >> 32));
        b[4] = (byte)  (0xff & (l >> 24));
        b[5] = (byte)  (0xff & (l >> 16));
        b[6] = (byte)  (0xff & (l >> 8));
        b[7] = (byte)  (0xff & l);
        return b;
    }

    public static long BytesToLong(byte[] bytes){
        if(bytes.length != 8){
            throw new IllegalArgumentException(
                    String.format("BytesToLong, the length of byte array should be 8, but %d gotten", bytes.length));
        }
        return 	(0xff00000000000000L 	& ((long)bytes[0] << 56))  |
                (0x00ff000000000000L 	& ((long)bytes[1] << 48))  |
                (0x0000ff0000000000L 	& ((long)bytes[2] << 40))  |
                (0x000000ff00000000L 	& ((long)bytes[3] << 32))  |
                (0x00000000ff000000L 	& ((long)bytes[4] << 24))  |
                (0x0000000000ff0000L 	& ((long)bytes[5] << 16))  |
                (0x000000000000ff00L 	& ((long)bytes[6] << 8))   |
                (0x00000000000000ffL 	&  (long)bytes[7]);
    }

    public static byte[] DoubleToBytes(double d){
        long longbits = Double.doubleToLongBits(d);
        return LongToBytes(longbits);
    }

    public static double BytesToDouble(byte[] bytes){
        if(bytes.length != 8){
            throw new IllegalArgumentException(
                    String.format("BytesToDouble, the length of byte array should be 8, but %d gotten", bytes.length));
        }
        return Double.longBitsToDouble(BytesToLong(bytes));
    }

    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7',
                                            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static String BytesToHexString(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        int index = 0;
        for(byte b : bytes) {
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
        }
        return new String(buf);
    }

}
