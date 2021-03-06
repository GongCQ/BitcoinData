package BitcoinData;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.locks.ReentrantLock;

public class HashIndexFile {
    private short code;
    private String name;
    private String filePath;
    private RandomAccessFile indexFile;
    private long reservedSize;
    private int usedNum;
    private boolean isConflictFile;
    private ReentrantLock appendLock;
    private ReentrantLock updateLock;

    public HashIndexFile(final short code, final String name, final String authority, final boolean isConflictFile) throws IOException {
        this.code = code;
        this.name = name;
        this.filePath = Parameter.INDEX_PATH + this.name;
        File file = new File(this.filePath);
        if(!file.exists() && authority == "rw"){
            file.createNewFile();
            this.indexFile = new RandomAccessFile(this.filePath, authority);
            this.indexFile.writeLong(Parameter.HASH_INDEX_FILE_SIZE);
            this.indexFile.writeLong(0L);

            long contentSize = Parameter.HASH_INDEX_FILE_SIZE - 2 * Parameter.SIZE_OF_LONG;
            long extendUnitSize = (long)Math.pow(2, 24);
            long extendedSize = 0;
            while(extendedSize < contentSize){
                long stepSize = Math.min(extendUnitSize, contentSize - extendedSize);
                byte[] bytes = new byte[(int)stepSize];
                for(int b = 0; b < bytes.length; b++){
                    bytes[b] = Utils.APPOINT_INVALID_BYTE;
                }
                this.indexFile.seek(this.indexFile.length());
                this.indexFile.write(bytes);
                extendedSize += stepSize;
            }
        }
        else if(!file.exists() && authority == "r"){
            throw new IOException("authority is 'read' but file doesn't exist.");
        }
        else{
            this.indexFile = new RandomAccessFile(this.filePath, authority);
        }

        this.indexFile.seek(0);
        this.reservedSize = this.indexFile.readLong();
        this.usedNum = (int)(this.indexFile.readLong());
        this.isConflictFile = isConflictFile;

        this.appendLock = new ReentrantLock();
        this.updateLock = new ReentrantLock();
    }

    private static final long GetByteFileLocation(int locationInFile){
        return Parameter.SIZE_OF_LONG * 2 + locationInFile * Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE;
    }

    public final HashIndexRecord GetRecord(final int locationInFile) throws IOException{
        if(locationInFile > Parameter.MAX_RECORD_IN_HASH_INDEX_FILE){
            throw new IllegalArgumentException("locationInFile is large than MAX_RECORD_IN_HASH_INDEX_FILE");
        }
        final long byteFileLocation = GetByteFileLocation(locationInFile);
        this.indexFile.seek(byteFileLocation);
        byte[] hashIndexRecordBytes = new byte[Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE];
        final HashIndexRecord hashIndexRecord = new HashIndexRecord(hashIndexRecordBytes);
        return hashIndexRecord;
    }

    public final int GetNextFreeLocation() throws IllegalAccessException{
        if(!this.isConflictFile){
            throw new IllegalAccessException("can't get next free location in non-conflict file.");
        }
        return this.usedNum;
    }

    public void SetRecord(final int locationInFile, final HashIndexRecord record) throws IOException{
        if(Parameter.CHECK_BEFOR_WRITE_HASH_INDEX_RECORD){
            if (locationInFile >= Parameter.MAX_RECORD_IN_HASH_INDEX_FILE){
                throw new IllegalArgumentException(String.format("locationInFile %d is large than MAX_RECORD_IN_HASH_INDEX_FILE.", locationInFile));
            }
            if(!IsFree(locationInFile)){
                throw new IllegalArgumentException("the record at locationInFile is valid, can't cover it. ");
            }
        }
        if(this.isConflictFile){
            this.indexFile.seek(Parameter.SIZE_OF_LONG);
            this.indexFile.writeLong(this.usedNum);
        }
        final long byteFileLocation = GetByteFileLocation(locationInFile);
        this.indexFile.seek(byteFileLocation);
        this.indexFile.write(record.Bytes());
    }

    public int AppendRecord(HashIndexRecord record) throws IOException, IllegalAccessException{
        if(!this.isConflictFile){
            throw new IllegalAccessException("can't append index record in non-conflict file.");
        }
//      this.appendLock.lock();
        final int nextFreeLocation = this.GetNextFreeLocation();
        record.UpdateLocation(new HashConflictLocation(this.code, nextFreeLocation, this.isConflictFile));
        this.SetRecord(nextFreeLocation, record);
        if(this.isConflictFile){
            this.usedNum++;
            this.indexFile.seek(Parameter.SIZE_OF_LONG);
            this.indexFile.writeLong(this.usedNum);
        }
//      this.appendLock.unlock();
        return nextFreeLocation;
    }

    public void SetNextLocation(final int recordLocation, HashConflictLocation location) throws IOException{
        final long byteFileLocation = GetByteFileLocation(recordLocation);
        if(Parameter.CHECK_BEFOR_WRITE_HASH_INDEX_RECORD){
            final HashIndexRecord record = GetRecord(recordLocation);
            if(!record.IsValid() || record.HasNext()){
                throw new IllegalArgumentException(
                        String.format("can't set next location in this location, " +
                                      "IsValid %s, HasNext %s.", record.IsValid(), record.HasNext()));
            }
        }
        this.indexFile.seek(byteFileLocation + Parameter.ADDRESS_SIZE);
        this.indexFile.writeShort(location.fileCode);
        this.indexFile.writeInt(location.locationInFile);
    }

    public final boolean IsFree(final int locationInFile) throws  IOException{
        byte[] readAddress = new byte[Parameter.ADDRESS_HASH_INDEX_RECORD_SIZE];
        final long byteFileLocation = GetByteFileLocation(locationInFile);
        this.indexFile.seek(byteFileLocation);
        this.indexFile.read(readAddress);
        return !Utils.IsValidAddress(readAddress);
    }

    public final int FreeNum(){
        return Parameter.MAX_RECORD_IN_HASH_INDEX_FILE - this.usedNum;
    }

    public static final String GetHashIndexFileNameByCode(short code){
        return String.format(Parameter.HASH_INDEX_FILE_PREFIX + "_%d.hash_index", code);
    }

    public static final String GetHashConflictFileNameByCode(short code){
        return String.format(Parameter.HASH_CONFLICT_INDEX_FILE_PREFIX + "_%d.hash_index", code);
    }
}
