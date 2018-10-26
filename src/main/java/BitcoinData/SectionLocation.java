package BitcoinData;

public class SectionLocation {
    public short fileCode;
    public int locationInFile;

    public SectionLocation(final short fileCode, final int locationInFile){
        this.fileCode = fileCode;
        this.locationInFile = locationInFile;
    }
}
