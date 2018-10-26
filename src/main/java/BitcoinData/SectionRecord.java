package BitcoinData;

import javax.rmi.CORBA.Util;

public class SectionRecord {
    private byte[] transHash;      // 0~31
    private int height;            // 32~35  0
    private int timestamp;         // 36~39  4
    private byte isFromCoinBase;   // 40~40  8
    private byte isIn;             // 41~41  9
    private byte isOut;            // 42~42  10
    private double inValue;        // 43~50  11
    private double outValue;       // 51~58  19
    private int totalInAddresses;  // 59~62  27
    private int totalOutAddresses; // 63~66  31
    private double totalInValue;   // 67~74  35
    private double totalOutValue;  // 75~82  43
    private byte[] bytes;
    private boolean isValid;

    public SectionRecord(final byte[] bytes, int begin){
        if (bytes.length < begin + Parameter.SECTION_RECORD_SIZE) {
            throw new IllegalArgumentException(String.format("bytes size must large than begin + SECTION_RECORD_SIZE %d",
                                                              begin + Parameter.SECTION_RECORD_SIZE));
        }

        this.transHash = new byte[Parameter.TRANS_HASH_SIZE];
        System.arraycopy(bytes, begin + 0, this.transHash, 0, Parameter.TRANS_HASH_SIZE);
        this.height = Utils.BytesToInt(bytes, begin + 32);
        this.timestamp = Utils.BytesToInt(bytes, begin + 36);
        this.isFromCoinBase = bytes[begin + 40];
        this.isIn = bytes[begin + 41];
        this.isOut = bytes[begin + 42];
        this.inValue = Utils.BytesToDouble(bytes, begin + 43);
        this.outValue = Utils.BytesToDouble(bytes, begin + 51);
        this.totalInAddresses = Utils.BytesToInt(bytes, begin + 59);
        this.totalOutAddresses = Utils.BytesToInt(bytes, begin + 63);
        this.totalInValue = Utils.BytesToDouble(bytes, begin + 67);
        this.totalOutValue = Utils.BytesToDouble(bytes, begin + 75);

        this.bytes = new byte[Parameter.SECTION_RECORD_SIZE];
        System.arraycopy(bytes, begin, this.bytes, 0, Parameter.SECTION_RECORD_SIZE);
        this.isValid = this.transHash != Utils.GetAppointInvalidTransHash();
    }

    public SectionRecord(final byte[] bytes) {
        if (bytes.length != Parameter.SECTION_RECORD_SIZE) {
            throw new IllegalArgumentException(String.format("bytes size must be SECTION_RECORD_SIZE %d", Parameter.SECTION_RECORD_SIZE));
        }

        this.transHash = new byte[Parameter.TRANS_HASH_SIZE];
        System.arraycopy(bytes, 0, this.transHash, 0, Parameter.TRANS_HASH_SIZE);
        this.height = Utils.BytesToInt(bytes, 32);
        this.timestamp = Utils.BytesToInt(bytes, 36);
        this.isFromCoinBase = bytes[40];
        this.isIn = bytes[41];
        this.isOut = bytes[42];
        this.inValue = Utils.BytesToDouble(bytes, 43);
        this.outValue = Utils.BytesToDouble(bytes, 51);
        this.totalInAddresses = Utils.BytesToInt(bytes, 59);
        this.totalOutAddresses = Utils.BytesToInt(bytes, 63);
        this.totalInValue = Utils.BytesToDouble(bytes, 67);
        this.totalOutValue = Utils.BytesToDouble(bytes, 75);

        this.bytes = bytes;
        this.isValid = this.transHash != Utils.GetAppointInvalidTransHash();
    }

    public SectionRecord(final byte[] transHash, int height, int timestamp, byte isFromCoinBase, byte isIn, byte isOut,
                         double inValue, double outValue, int totalInAddresses, int totalOutAddresses,
                         double totalInValue, double totalOutValue) {
        if (transHash.length != Parameter.TRANS_HASH_SIZE) {
            throw new IllegalArgumentException(String.format("trans hash size must be TRANS_HASH_SIZE %d", Parameter.TRANS_HASH_SIZE));
        }

        this.bytes = new byte[Parameter.SECTION_RECORD_SIZE];

        System.arraycopy(transHash, 0, this.bytes, 0, Parameter.TRANS_HASH_SIZE);
        Utils.IntToBytes(height, this.bytes, Parameter.TRANS_HASH_SIZE + 0);
        Utils.IntToBytes(timestamp, this.bytes, Parameter.TRANS_HASH_SIZE + 4);
        this.bytes[Parameter.TRANS_HASH_SIZE + 8] = isFromCoinBase;
        this.bytes[Parameter.TRANS_HASH_SIZE + 9] = isIn;
        this.bytes[Parameter.TRANS_HASH_SIZE + 10] = isOut;
        Utils.DoubleToBytes(inValue, this.bytes, Parameter.TRANS_HASH_SIZE + 11);
        Utils.DoubleToBytes(outValue, this.bytes, Parameter.TRANS_HASH_SIZE + 19);
        Utils.IntToBytes(totalInAddresses, this.bytes, Parameter.TRANS_HASH_SIZE + 27);
        Utils.IntToBytes(totalOutAddresses, this.bytes, Parameter.TRANS_HASH_SIZE + 31);
        Utils.DoubleToBytes(totalInValue, this.bytes, Parameter.TRANS_HASH_SIZE + 35);
        Utils.DoubleToBytes(totalOutValue, this.bytes, Parameter.TRANS_HASH_SIZE + 43);

        this.transHash = transHash;
        this.height = height;
        this.timestamp = timestamp;
        this.isFromCoinBase = isFromCoinBase;
        this.isIn = isIn;
        this.isOut = isOut;
        this.inValue = inValue;
        this.outValue = outValue;
        this.totalInAddresses = totalInAddresses;
        this.totalOutAddresses = totalOutAddresses;
        this.totalInValue = totalInValue;
        this.totalOutValue = totalOutValue;
        this.isValid = this.transHash != Utils.GetAppointInvalidTransHash();
    }

    public final byte[] TransHash() {
        return this.transHash;
    }

    public final int Height() {
        return this.height;
    }

    public final int Timestamp() {
        return this.timestamp;
    }

    public final byte IsFromCoinBase() {
        return this.isFromCoinBase;
    }

    public final byte IsIn() {
        return this.isIn;
    }

    public final byte IsOut() {
        return this.isOut;
    }

    public final double InValue() {
        return this.inValue;
    }

    public final double OutValue() {
        return this.outValue;
    }

    public final int TotalInAddresses() {
        return this.totalInAddresses;
    }

    public final int TotalOutAddresses() {
        return this.totalOutAddresses;
    }

    public final double TotalInValue() {
        return this.totalInValue;
    }

    public final double TotalOutValue() {
        return this.totalOutValue;
    }

    public final byte[] Bytes() {
        return this.bytes;
    }

    public final boolean IsValid(){
        return this.isValid;
    }
}
