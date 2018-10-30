package BitcoinData;

public class Section {
    private byte[] address;
    private SectionLocation previousLocation;
    private SectionLocation nextLocation;
    private short totalRecordsInSection;
    private short selfFileCode;
    private int selfLocationInFile;
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
        this.totalRecordsInSection = Utils.BytesToShort(bytes, Parameter.ADDRESS_SIZE + 12);
        this.selfFileCode = Utils.BytesToShort(bytes, Parameter.ADDRESS_SIZE + 14);
        this.selfLocationInFile = Utils.BytesToInt(bytes, Parameter.ADDRESS_SIZE + 16);

        this.records = new SectionRecord[Parameter.SECTION_RECORD_NUM];
        for(int r = 0; r < rawRecordsNum; r++){
            int recordByteBegin = Parameter.SECTION_HEAD_SIZE + r * Parameter.SECTION_RECORD_SIZE;
            SectionRecord record = new SectionRecord(this.bytes, recordByteBegin);
            if(record.IsValid()){
                this.records[r] = record;
            }
        }

        this.isValid = this.address != Utils.GetAppointInvalidAddress();
    }

    public Section(final byte[] address, final SectionLocation previousLocation, final SectionLocation nextLocation,
                   final short totalRecordsInSection, final short selfFileCode, int selfLocationInFile,
                   SectionRecord[] records){
        if(totalRecordsInSection != (short)records.length || totalRecordsInSection > Parameter.SECTION_RECORD_NUM){
            throw new IllegalArgumentException("invalid totalRecordsInSection or records.length.");
        }

        this.address = address;
        this.previousLocation = previousLocation;
        this.nextLocation = nextLocation;
        this.totalRecordsInSection = totalRecordsInSection;
        this.selfFileCode = selfFileCode;
        this.selfLocationInFile = selfLocationInFile;
        this.records = records;

        this.bytes = new byte[Parameter.SECTION_SIZE];
        System.arraycopy(address, 0, this.bytes, 0, Parameter.ADDRESS_SIZE);
        Utils.ShortToBytes(previousLocation.fileCode, this.bytes, Parameter.ADDRESS_SIZE);
        Utils.IntToBytes(previousLocation.locationInFile, this.bytes, Parameter.ADDRESS_SIZE + 2);
        Utils.ShortToBytes(nextLocation.fileCode, this.bytes, Parameter.ADDRESS_SIZE + 6);
        Utils.IntToBytes(nextLocation.locationInFile, this.bytes, Parameter.ADDRESS_SIZE + 8);
        Utils.ShortToBytes(totalRecordsInSection, this.bytes, Parameter.ADDRESS_SIZE + 12);
        Utils.ShortToBytes(selfFileCode, this.bytes, Parameter.ADDRESS_SIZE + 14);
        Utils.IntToBytes(selfLocationInFile, this.bytes, Parameter.ADDRESS_SIZE + 16);
        for(int r = 0; r < records.length; r++){
            System.arraycopy(records[r], 0, this.bytes,
                      Parameter.ADDRESS_SIZE + 20 + r * Parameter.SECTION_RECORD_SIZE,
                             Parameter.SECTION_RECORD_SIZE);
        }

        this.isValid = this.address != Utils.GetAppointInvalidAddress();
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

    public final short TotalRecordsInSection(){
        return this.totalRecordsInSection;
    }

    public final short SelfFileCode(){
        return this.selfFileCode;
    }

    public final int SelfLocationInFile(){
        return this.selfLocationInFile;
    }

    public final SectionRecord[] Records(){
        return this.records;
    }

    public final boolean IsValid(){
        return this.isValid;
    }

    public final byte[] Bytes(){
        return this.bytes;
    }
}
