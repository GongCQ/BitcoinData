package BitcoinData;

public class Section {
    private byte[] address;
    private SectionLocation previousLocation;
    private SectionLocation nextLocation;
    private short totalRecordsInSection;
    private short selfFileCode;
    private int selfLocationInFile;
    private SectionRecord[] records;

    private byte[] bytes;

    public Section(final byte[] bytes){
        int recordsNum = (bytes.length - Parameter.SECTION_HEAD_SIZE) / Parameter.SECTION_RECORD_SIZE;
        if(recordsNum * Parameter.SECTION_RECORD_SIZE + Parameter.SECTION_HEAD_SIZE != bytes.length ||
           recordsNum > Parameter.SECTION_RECORD_NUM){
            throw new IllegalArgumentException(
                    String.format("invalid length of bytes, %d, (bytes.length - SECTION_HEAD_SIZE) / SECTION_RECORD_SIZE is not integer.", bytes.length));
        }
        this.bytes = bytes;

        this.address = new byte[Parameter.ADDRESS_SIZE];
        System.arraycopy(bytes, 0, this.address, 0, Parameter.ADDRESS_SIZE);
        this.previousLocation = new SectionLocation(Utils.BytesToShort(bytes,       Parameter.ADDRESS_SIZE),
                                                    Utils.BytesToInt(bytes,   Parameter.ADDRESS_SIZE + 2));
        this.nextLocation     = new SectionLocation(Utils.BytesToShort(bytes, Parameter.ADDRESS_SIZE + 6),
                                                    Utils.BytesToInt(bytes,   Parameter.ADDRESS_SIZE + 6 + 2));
        this.totalRecordsInSection = Utils.BytesToShort(bytes, Parameter.ADDRESS_SIZE + 12);
        this.selfFileCode = Utils.BytesToShort(bytes, Parameter.ADDRESS_SIZE + 14);
        this.selfLocationInFile = Utils.BytesToInt(bytes, Parameter.ADDRESS_SIZE + 16);

        this.records = new SectionRecord[Parameter.SECTION_RECORD_NUM];
        for(int r = 0; r < recordsNum; r++){
            int recordByteBegin = Parameter.SECTION_HEAD_SIZE + r * Parameter.SECTION_RECORD_SIZE;
            SectionRecord record = new SectionRecord(this.bytes, recordByteBegin);
            if(record.IsValid()){
                this.records[r] = record;
            }
        }
    }

    public Section(final byte[] address, final SectionLocation previousLocation, final SectionLocation nextLocation,
                   final short totalRecordsInSection, final short selfFileCode, int selfLocationInFile){

    }
}
