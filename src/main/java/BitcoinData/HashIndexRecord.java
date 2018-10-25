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
            byte[] shortBytes = new byte[Parameter.SIZE_OF_SHORT];
            System.arraycopy(hashIndexRecordBytes, Parameter.ADDRESS_SIZE,
                    shortBytes, 0, 2);
            this.nextConflictFileCode = Utils.BytesToShort(shortBytes);
            System.arraycopy(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + (2 + 4) * 1,
                    shortBytes, 0, 2);
            this.firstFileCode = Utils.BytesToShort(shortBytes);
            System.arraycopy(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + (2 + 4) * 2,
                    shortBytes, 0, 2);
            this.lastFileCode = Utils.BytesToShort(shortBytes);
            System.arraycopy(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + (2 + 4) * 3,
                    shortBytes, 0, 2);
            this.selfFileCode = Utils.BytesToShort(shortBytes);

            byte[] intBytes = new byte[Parameter.SIZE_OF_INT];
            System.arraycopy(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + 2,
                    intBytes, 0, 4);
            this.nextConflictLocationInFile = Utils.BytesToInt(intBytes);
            System.arraycopy(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + 2 + (2 + 4) * 1,
                    intBytes, 0, 4);
            this.firstLocationInFile = Utils.BytesToInt(intBytes);
            System.arraycopy(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + 2 + (2 + 4) * 2,
                    intBytes, 0, 4);
            this.lastLocationInFile = Utils.BytesToInt(intBytes);
            System.arraycopy(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + 2 + (2 + 4) * 3,
                    intBytes, 0, 4);
            this.selfLocationInFile = Utils.BytesToInt(intBytes);

            this.isConflict = hashIndexRecordBytes[Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE - 1];

            this.hasNext = this.nextConflictFileCode != Utils.GetAppointInvalidShort() &&
                           this.nextConflictLocationInFile != Utils.GetAppointInvalidInt();
        }
        this.bytes = hashIndexRecordBytes;
    }

    public HashIndexRecord(byte[] address, final short nextConflictFileCode, final int nextConflictLocationInFile,
                           final short firstFileCode, final int firstLocationInFile,
                           final short lastFileCode, final int lastLocationInFile,
                           final short selfFileCode, final int selfLocationInFile, byte isConflict) {
        this.isValid = Utils.IsValidAddress(address);

        this.address = address;
        this.nextConflictFileCode = nextConflictFileCode;
        this.nextConflictLocationInFile = nextConflictLocationInFile;
        this.firstFileCode = firstFileCode;
        this.firstLocationInFile = firstLocationInFile;
        this.lastFileCode = lastFileCode;
        this.lastLocationInFile = lastLocationInFile;
        this.isConflict = isConflict;

        this.hasNext = this.nextConflictFileCode != Utils.GetAppointInvalidShort() &&
                       this.nextConflictLocationInFile != Utils.GetAppointInvalidInt();

        this.bytes = new byte[Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE];
        System.arraycopy(address, 0, bytes, 0, Parameter.ADDRESS_SIZE);

        System.arraycopy(Utils.ShortToBytes(nextConflictFileCode), 0,
                bytes, Parameter.ADDRESS_SIZE + 0, 2);
        System.arraycopy(Utils.IntToBytes(nextConflictLocationInFile), 0,
                bytes, Parameter.ADDRESS_SIZE + 2, 4);

        System.arraycopy(Utils.ShortToBytes(firstFileCode), 0,
                bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 1, 2);
        System.arraycopy(Utils.IntToBytes(firstLocationInFile), 0,
                bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 1 + 2, 4);

        System.arraycopy(Utils.ShortToBytes(lastFileCode), 0,
                bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 2, 2);
        System.arraycopy(Utils.IntToBytes(lastLocationInFile), 0,
                bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 2 + 2, 4);

        System.arraycopy(Utils.ShortToBytes(selfFileCode), 0,
                bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 3, 2);
        System.arraycopy(Utils.IntToBytes(selfLocationInFile), 0,
                bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 3 + 2, 4);

        this.bytes[Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE] = isConflict;

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
