package BitcoinData;

public class SectionLocation {
    public short fileCode;
    public int locationInFile;

    public SectionLocation(){
        this.fileCode = Utils.GetAppointInvalidShort();
        this.locationInFile = Utils.GetAppointInvalidInt();
    }

    public SectionLocation(final short fileCode, final int locationInFile){
        this.fileCode = fileCode;
        this.locationInFile = locationInFile;
    }

    public boolean IsValid(){
        return this.fileCode != Utils.GetAppointInvalidShort() && this.locationInFile != Utils.GetAppointInvalidInt();
    }
}
