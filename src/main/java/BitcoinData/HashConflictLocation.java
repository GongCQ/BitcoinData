package BitcoinData;

public class HashConflictLocation {
    public short fileCode;
    public int locationInFile;
    public boolean isConflict;

    public HashConflictLocation(final short fileCode, final int locationInFile, final boolean isConflict){
        this.fileCode = fileCode;
        this.locationInFile = locationInFile;
        this.isConflict = isConflict;
    }

    public boolean IsValid(){
        return this.fileCode != Utils.GetAppointInvalidShort() && this.locationInFile != Utils.GetAppointInvalidInt();
    }

    public static final HashConflictLocation InvalidLocation(){
        return new HashConflictLocation(Utils.GetAppointInvalidShort(), Utils.GetAppointInvalidInt(), true);
    }
}
