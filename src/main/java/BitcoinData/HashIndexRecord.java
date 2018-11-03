package BitcoinData;

public class HashIndexRecord {
    private boolean isValid;
    private boolean hasNext;

    private byte[] address;                // 0~24
//    private short nextConflictFileCode;    // 25~26
//    private int nextConflictLocationInFile;// 27~30
    private HashConflictLocation nextConflictLocation;
//    private short firstFileCode;           // 31~32
//    private int firstLocationInFile;       // 33~36
    private SectionLocation firstSectionLocation;
//    private short lastFileCode;            // 37~38
//    private int lastLocationInFile;        // 39~42
    private SectionLocation lastSectionLocation;
//    private short selfFileCode;            // 43~44
//    private int selfLocationInFile;        // 45~48
//    private byte isConflict;            // 49~49
    private HashConflictLocation selfLocation;

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
            short nextConflictFileCode = Utils.BytesToShort(hashIndexRecordBytes, Parameter.ADDRESS_SIZE);
            int nextConflictLocationInFile = Utils.BytesToInt(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + 2);
            this.nextConflictLocation = new HashConflictLocation(nextConflictFileCode, nextConflictLocationInFile, true);

            short firstFileCode = Utils.BytesToShort(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + (2 + 4) * 1);
            int firstLocationInFile = Utils.BytesToInt(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + 2 + (2 + 4) * 1);
            this.firstSectionLocation = new SectionLocation(firstFileCode, firstLocationInFile);

            short lastFileCode =  Utils.BytesToShort(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + (2 + 4) * 2);
            int lastLocationInFile = Utils.BytesToInt(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + 2 + (2 + 4) * 2);
            this.lastSectionLocation = new SectionLocation(lastFileCode, lastLocationInFile);

            short selfFileCode =  Utils.BytesToShort(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + (2 + 4) * 3);
            int selfLocationInFile = Utils.BytesToInt(hashIndexRecordBytes, Parameter.ADDRESS_SIZE + 2 + (2 + 4) * 3);
            boolean isConflict = hashIndexRecordBytes[Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE - 1] > 0 ? true : false;
            this.selfLocation = new HashConflictLocation(selfFileCode, selfLocationInFile, isConflict);

            this.hasNext = this.nextConflictLocation.IsValid();
        }
        this.bytes = hashIndexRecordBytes;
    }

    public HashIndexRecord(byte[] address, HashConflictLocation nextConflictLocation ,
                           SectionLocation firstSectionLocation, SectionLocation lastSectionLocation,
                           HashConflictLocation selfLocation) {
        this.isValid = Utils.IsValidAddress(address);

        this.address = address;
        this.nextConflictLocation = nextConflictLocation;
        this.firstSectionLocation = firstSectionLocation;
        this.lastSectionLocation = lastSectionLocation;
        this.selfLocation = selfLocation;

        this.hasNext = this.nextConflictLocation.IsValid();

        this.bytes = new byte[Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE];
        System.arraycopy(address, 0, bytes, 0, Parameter.ADDRESS_SIZE);

        Utils.ShortToBytes(this.nextConflictLocation.fileCode, this.bytes, Parameter.ADDRESS_SIZE);
        Utils.IntToBytes(this.nextConflictLocation.locationInFile, this.bytes, Parameter.ADDRESS_SIZE + 2);
        Utils.ShortToBytes(this.firstSectionLocation.fileCode, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 1);
        Utils.IntToBytes(this.firstSectionLocation.locationInFile, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 1 + 2);
        Utils.ShortToBytes(this.lastSectionLocation.fileCode, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 2);
        Utils.IntToBytes(this.lastSectionLocation.locationInFile, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 2 + 2);
        Utils.ShortToBytes(this.selfLocation.fileCode, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 3);
        Utils.IntToBytes(this.selfLocation.locationInFile, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 3 + 2);
        this.bytes[Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE - 1] = (byte)(this.selfLocation.isConflict ? 127 : -128);

    }

    public void UpdateLocation(final HashConflictLocation location){
        this.selfLocation = location;
        Utils.ShortToBytes(this.selfLocation.fileCode, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 3);
        Utils.IntToBytes(this.selfLocation.locationInFile, this.bytes, Parameter.ADDRESS_SIZE + (2 + 4) * 3 + 2);
        this.bytes[Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE - 1] = (byte)(this.selfLocation.isConflict ? 127 : -128);
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

    public final HashConflictLocation NextConflictLocation() {
        return this.nextConflictLocation;
    }

    public final SectionLocation FirstSectionLocation() {
        return this.firstSectionLocation;
    }

    public final SectionLocation LastSectionLocation() {
        return this.lastSectionLocation;
    }

    public final HashConflictLocation SelfLocation(){
        return this.selfLocation;
    }

    public final byte[] Bytes() {
        return this.bytes;
    }
}
