package BitcoinData;

public class HashIndexRecord {
    private boolean isValid;
    private boolean hasNext;

    private byte[] address;                // 0~24
    private short nextConflictFileCode;    // 25~26
    private int nextConflictLocationInFile;// 27~30
    private short firstFileCode;           // 31~32
    private int firstLocationInFile;       // 33~36
    private short lastFileCode;            // 37~38
    private int lastLocationInFile;        // 39~42
    private short selfFileCode;            // 43~44
    private int selfLocationInFile;        // 45~48
    private byte isConflict;            // 49~49

    private byte[] bytes;

    public HashIndexRecord(final byte[] hashIndexRecordBytes) {
        if (hashIndexRecordBytes.length != Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE) {
            throw new IllegalArgumentException(
                    String.format("length of hashIndexRecordBytes is not valid, length %d", hashIndexRecordBytes.length));
        }

        this.address = new byte[Parameter.ADDRESS_SIZE];
        System.arraycopy(hashIndexRecordBytes, 0, address, 0, Parameter.ADDRESS_SIZE);
        this.isValid = Utils.IsValidAddress(address);
        this.hasNext = false;
        if (this.isValid) {
            this.nextConflictFileCode = Utils.BytesToShort(hashIndexRecordBytes, Parameter.ADDRESS_SIZE);
            this.nextConflictLocationInFile = Utils.BytesToInt(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + 2);

            this.firstFileCode = Utils.BytesToShort(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + (2 + 4) * 1);
            this.firstLocationInFile = Utils.BytesToInt(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + 2 + (2 + 4) * 1);

            this.lastFileCode =  Utils.BytesToShort(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + (2 + 4) * 2);
            this.lastLocationInFile = Utils.BytesToInt(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + 2 + (2 + 4) * 2);

            this.selfFileCode =  Utils.BytesToShort(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + (2 + 4) * 3);
            this.selfLocationInFile = Utils.BytesToInt(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + 2 + (2 + 4) * 3);

            this.isConflict = hashIndexRecordBytes[Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE - 1];

            this.hasNext = this.nextConflictFileCode != Utils.GetAppointInvalidShort() &&
                           this.nextConflictLocationInFile != Utils.GetAppointInvalidInt();
        }
        this.bytes = hashIndexRecordBytes;
    }

    public HashIndexRecord(byte[] address, final short nextConflictFileCode, final int nextConflictLocationInFile,
                           final short firstFileCode, final int firstLocationInFile,
                           final short lastFileCode, final int lastLocationInFile,
                           final short selfFileCode, final int selfLocationInFile, boolean isConflict) {
        this.isValid = Utils.IsValidAddress(address);

        this.address = address;
        this.nextConflictFileCode = nextConflictFileCode;
        this.nextConflictLocationInFile = nextConflictLocationInFile;
        this.firstFileCode = firstFileCode;
        this.firstLocationInFile = firstLocationInFile;
        this.lastFileCode = lastFileCode;
        this.lastLocationInFile = lastLocationInFile;
        this.selfFileCode = selfFileCode;
        this.selfLocationInFile = selfLocationInFile;
        this.isConflict = (byte)(isConflict ? 127 : -128);

        this.hasNext = this.nextConflictFileCode != Utils.GetAppointInvalidShort() &&
                       this.nextConflictLocationInFile != Utils.GetAppointInvalidInt();

        this.bytes = new byte[Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE];
        System.arraycopy(address, 0, bytes, 0, Parameter.ADDRESS_SIZE);

        Utils.ShortToBytes(nextConflictFileCode, this.bytes, Parameter.ADDRESS_SIZE);
        Utils.IntToBytes(nextConflictLocationInFile, this.bytes, Parameter.ADDRESS_SIZE + 2);
        Utils.ShortToBytes(firstFileCode, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 1);
        Utils.IntToBytes(firstLocationInFile, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 1 + 2);
        Utils.ShortToBytes(lastFileCode, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 2);
        Utils.IntToBytes(lastLocationInFile, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 2 + 2);
        Utils.ShortToBytes(selfFileCode, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 3);
        Utils.IntToBytes(selfLocationInFile, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 3 + 2);

        this.bytes[Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE - 1] = (byte)(isConflict ? 127 : -128);

    }

    public void UpdateLocation(short fileCode, int locationInFile, boolean isConflict){
        this.selfFileCode = fileCode;
        this.selfLocationInFile = locationInFile;
        this.isConflict = (byte)(isConflict ? 127 : -128);
        Utils.ShortToBytes(this.selfFileCode, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 3);
        Utils.IntToBytes(this.selfLocationInFile, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 3 + 2);
        this.bytes[Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE - 1] = (byte)(isConflict ? 127 : -128);
    }

    public final boolean IsValid() {
        return this.isValid;
    }

    public final boolean HasNext() {
        return this.hasNext;
    }

    public final byte[] Address() {
        return this.address;
    }

    public final short NextConflictFileCode() {
        return this.nextConflictFileCode;
    }

    public final int NextConflictLocationInFile() {
        return this.nextConflictLocationInFile;
    }

    public final short FirstFileCode() {
        return this.firstFileCode;
    }

    public final int FirstLocationInFile() {
        return this.firstLocationInFile;
    }

    public final short LastFileCode() {
        return this.lastFileCode;
    }

    public final int LastLocationInFile() {
        return this.lastLocationInFile;
    }

    public final short SelfFileCode(){
        return this.selfFileCode;
    }

    public final int SelfLocationInFile(){
        return this.selfLocationInFile;
    }

    public final boolean IsConflict(){
        return this.isConflict > 0;
    }

    public final byte[] Bytes() {
        return this.bytes;
    }
}
