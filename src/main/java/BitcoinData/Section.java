package BitcoinData;

public class Section {
    private byte[] address;
    private SectionLocation previousLocation;
    private SectionLocation nextLocation;
    private short tailLocator;
    private SectionLocation selfLocation;
    private short seq;
    private SectionRecord[] records;
    private boolean isValid;

    private byte[] bytes;

    public Section(final byte[] bytes){
        int rawRecordsNum = (bytes.length - Parameter.SECTION_HEAD_SIZE) / Parameter.SECTION_RECORD_SIZE;
        if(rawRecordsNum * Parameter.SECTION_RECORD_SIZE + Parameter.SECTION_HEAD_SIZE != bytes.length ||
           rawRecordsNum > Parameter.SECTION_RECORD_NUM){
            throw new IllegalArgumentException(
                    String.format("invalid length of bytes, %d, (bytes.length - SECTION_HEAD_SIZE) / SECTION_RECORD_SIZE is not integer.", bytes.length));
        }
        this.bytes = bytes;

        this.address = new byte[Parameter.ADDRESS_SIZE];
        System.arraycopy(bytes, 0, this.address, 0, Parameter.ADDRESS_SIZE);
        this.previousLocation = new SectionLocation(Utils.BytesToShort(bytes,       Parameter.ADDRESS_SIZE),
                                                    Utils.BytesToInt(bytes,   Parameter.ADDRESS_SIZE + 2));
        this.nextLocation     = new SectionLocation(Utils.BytesToShort(bytes, Parameter.ADDRESS_SIZE + 6),
                                                    Utils.BytesToInt(bytes,   Parameter.ADDRESS_SIZE + 8));
        this.tailLocator = Utils.BytesToShort(bytes, Parameter.ADDRESS_SIZE + 12);
        short selfFileCode = Utils.BytesToShort(bytes, Parameter.ADDRESS_SIZE + 14);
        int selfLocationInFile = Utils.BytesToInt(bytes, Parameter.ADDRESS_SIZE + 16);
        this.selfLocation = new SectionLocation(selfFileCode, selfLocationInFile);
        this.seq = Utils.BytesToShort(bytes, Parameter.ADDRESS_SIZE + 20);

        this.records = new SectionRecord[Parameter.SECTION_RECORD_NUM];
        int readRecordsNum = 0;
        for(int r = 0; r < rawRecordsNum; r++){
            int recordByteBegin = Parameter.SECTION_HEAD_SIZE + r * Parameter.SECTION_RECORD_SIZE;
            SectionRecord record = new SectionRecord(this.bytes, recordByteBegin);
            if(record.IsValid()){
                this.records[r] = record;
                readRecordsNum++;
            }
            else{
                break;
            }
        }
        if(readRecordsNum % Parameter.SECTION_RECORD_NUM != this.tailLocator){
            throw new IllegalArgumentException("readRecordsNum mod SECTION_RECORD_NUM is not equal to this.tailLocator");
        }

        this.isValid = this.address != Utils.GetAppointInvalidAddress();
    }

    public Section(final byte[] address, final SectionLocation previousLocation, final SectionLocation nextLocation,
                   final short tailLocator, final SectionLocation selfLocation, final short seq,
                   SectionRecord[] records){
        if(records.length != Parameter.SECTION_RECORD_NUM){
            throw new IllegalArgumentException("records.length must be SECTION_RECORD_NUM.");
        }
        if(tailLocator % Parameter.SECTION_RECORD_NUM != (short)records.length){
            throw new IllegalArgumentException("invalid tailLocator or records.length.");
        }

        this.address = address;
        this.previousLocation = previousLocation;
        this.nextLocation = nextLocation;
        this.tailLocator = tailLocator;
        this.selfLocation = selfLocation;
        this.seq = seq;
        this.records = records;

        this.bytes = new byte[Parameter.SECTION_SIZE];
        System.arraycopy(address, 0, this.bytes, 0, Parameter.ADDRESS_SIZE);
        Utils.ShortToBytes(previousLocation.fileCode, this.bytes, Parameter.ADDRESS_SIZE);
        Utils.IntToBytes(previousLocation.locationInFile, this.bytes, Parameter.ADDRESS_SIZE + 2);
        Utils.ShortToBytes(nextLocation.fileCode, this.bytes, Parameter.ADDRESS_SIZE + 6);
        Utils.IntToBytes(nextLocation.locationInFile, this.bytes, Parameter.ADDRESS_SIZE + 8);
        Utils.ShortToBytes(tailLocator, this.bytes, Parameter.ADDRESS_SIZE + 12);
        Utils.ShortToBytes(selfLocation.fileCode, this.bytes, Parameter.ADDRESS_SIZE + 14);
        Utils.IntToBytes(selfLocation.locationInFile, this.bytes, Parameter.ADDRESS_SIZE + 16);
        Utils.ShortToBytes(seq, this.bytes, Parameter.ADDRESS_SIZE + 20);
        for(int r = 0; r < records.length; r++){
            System.arraycopy(records[r], 0, this.bytes,
                      Parameter.SECTION_HEAD_SIZE + r * Parameter.SECTION_RECORD_SIZE,
                             Parameter.SECTION_RECORD_SIZE);
        }

        this.isValid = this.address != Utils.GetAppointInvalidAddress();
    }

    private static final int GetRecordByteLocation(final short recordSeq){
        return Parameter.SECTION_HEAD_SIZE + recordSeq * Parameter.SECTION_RECORD_SIZE;
    }

    public void AppendRecord(final SectionRecord record){
        final int recordByteLocation = Section.GetRecordByteLocation(this.tailLocator);
        System.arraycopy(record.Bytes(), 0, this.bytes, 0, recordByteLocation);
        this.records[this.tailLocator] = record;
        this.tailLocator++;
        this.tailLocator %= Parameter.SECTION_RECORD_NUM;
    }

    public final byte[] Address(){
        return this.address;
    }

    public final SectionLocation PreviousLocation(){
        return this.previousLocation;
    }

    public final SectionLocation NextLocation(){
        return this.nextLocation;
    }

    public final short TailLocator(){
        return this.tailLocator;
    }

    public final SectionLocation SelfLocation(){
        return this.selfLocation;
    }

    public final short Seq() {return this.seq;}

    public final SectionRecord[] Records(){
        return this.records;
    }

    public final boolean IsValid(){
        return this.isValid;
    }

    public final byte[] Bytes(){
        return this.bytes;
    }

    public final boolean IsFull(){
        return this.records[this.tailLocator] != null;
    }
}
