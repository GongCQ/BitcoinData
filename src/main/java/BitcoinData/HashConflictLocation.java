package BitcoinData;

public class HashConflictLocation {
    public short fileCode;
    public int locationInFile;

    public HashConflictLocation(final short fileCode, final int locationInFile){
        this.fileCode = fileCode;
        this.locationInFile = locationInFile;
    }
}
