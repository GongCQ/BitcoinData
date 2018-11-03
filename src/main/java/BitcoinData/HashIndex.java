package BitcoinData;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class HashIndex {
    private Data data;
    private HashIndexFile[] indexFiles;
    private ArrayList<HashIndexFile> hashConflictFile;
    private HashIndexCache indexCache;
    private ReentrantLock hashFreeLock;
    private ReentrantLock hashConflictLock;

    public HashIndex(Data data) throws IOException, IllegalAccessException {
        this.data = data;
        this.hashFreeLock = new ReentrantLock();
        this.indexCache = new HashIndexCache();
        // hash index file
        this.indexFiles = new HashIndexFile[Parameter.MAX_HASH_INDEX_FILE];
        for(short i = 0; i < Parameter.MAX_HASH_INDEX_FILE; i++){
            String fileName = Parameter.INDEX_PATH + HashIndexFile.GetHashIndexFileNameByCode(i);
            this.indexFiles[i] = new HashIndexFile(i, fileName, "rw", false);
        }

        // hash conflict file
        this.hashConflictFile = new ArrayList<HashIndexFile>(Parameter.MAX_HASH_CONFLICT_FILE);
        final File indexFilePath = new File(Parameter.INDEX_PATH);
        final File[] allIndexFile = indexFilePath.listFiles();
        for(final File file : allIndexFile){
            final String name = file.getName();
            final int prefixLength = Parameter.HASH_CONFLICT_INDEX_FILE_PREFIX.length();
            if (name.length() > prefixLength &&
                name.substring(0, prefixLength).equals(Parameter.HASH_CONFLICT_INDEX_FILE_PREFIX)){
                final short fileCode = Short.parseShort(name.substring(prefixLength, name.lastIndexOf('.')));
                if(fileCode > this.hashConflictFile.size()){
                    throw new IllegalAccessException(String.format("find a invalid hash conflict file, " +
                            "name %s, code %d.", name, fileCode));
                }
                this.hashConflictFile.add(fileCode, new HashIndexFile(fileCode, file.getAbsolutePath(), "rw", true));
                System.out.println(String.format("find a hash conflict file, " + name + ", code %d", fileCode));
            }
        }
    }

    public final HashIndexRecord ReadSearch(final byte[] address) throws IOException{
        HashIndexRecord record = this.indexCache.Get(address);  // memory cache
        if(record == null){
            record = this.indexFiles[GetIndexFileCode(address)].GetRecord(GetLocationInIndexFile(address));  // hash index
            while(!address.equals(record.Address())){  // hash conflict
                if(!record.IsValid() || !record.HasNext()){
                    return null;
                }
                record = this.hashConflictFile.get(record.SelfLocation().fileCode).GetRecord(record.SelfLocation().locationInFile);
            }
        }
        return record;
    }

    public final HashIndexRecord WriteSearch(final byte[] address) throws IOException, IllegalAccessException{
        HashIndexRecord record = this.indexCache.Get(address);  // memory cache
        if(record == null){
            short hashIndexFileCode = HashIndex.GetIndexFileCode(address);
            int locationInHashIndexFile = HashIndex.GetLocationInIndexFile(address);
            record = this.indexFiles[hashIndexFileCode].GetRecord(locationInHashIndexFile);

            if(address.equals(record.Address())){  // hash hit
                return record;
            }

            this.hashFreeLock.lock();
            record = this.indexFiles[hashIndexFileCode].GetRecord(locationInHashIndexFile);
            if(!record.IsValid()){  // hash free
                SectionLocation freeSectionLocation = this.data.GetNextFreeSectionLocation();
                HashIndexRecord newRecord = new HashIndexRecord(address,
                        HashConflictLocation.InvalidLocation(), freeSectionLocation, freeSectionLocation,
                        new HashConflictLocation(hashIndexFileCode, locationInHashIndexFile, false));
                this.indexFiles[hashIndexFileCode].SetRecord(locationInHashIndexFile, newRecord);
                this.hashFreeLock.unlock();
                this.indexCache.Set(address, newRecord);
                return newRecord;
            }
            else{
                this.hashFreeLock.unlock();
            }

            this.hashConflictLock.lock();
            record = this.indexFiles[hashIndexFileCode].GetRecord(locationInHashIndexFile);
            while(!address.equals(record.Address())){  // hash conflict
                if(!record.HasNext()){
                    HashConflictLocation nextFreeLocation = this.GetNextFreeHashConflictLocation();
                    HashIndexFile fileForRecord = record.SelfLocation().isConflict ?
                                                    this.hashConflictFile.get(record.SelfLocation().fileCode)
                                                  : this.indexFiles[record.SelfLocation().fileCode];

                    SectionLocation freeSectionLocation = this.data.GetNextFreeSectionLocation();
                    HashIndexRecord newRecord = new HashIndexRecord(address,
                                                                    HashConflictLocation.InvalidLocation(),     // no next hash index record
                                                                    freeSectionLocation, freeSectionLocation,
                                                                    new HashConflictLocation(nextFreeLocation.fileCode, nextFreeLocation.locationInFile,true));
                    HashIndexFile fileForNewRecord = this.hashConflictFile.get(newRecord.SelfLocation().fileCode);
                    fileForNewRecord.AppendRecord(newRecord);

                    fileForRecord.SetNextLocation(record.SelfLocation().locationInFile, newRecord.SelfLocation());

                    record = newRecord;
                    break;
                }
                else{
                    record = this.hashConflictFile.get(record.NextConflictLocation().fileCode).GetRecord(record.NextConflictLocation().locationInFile);
                }
            }
            this.hashConflictLock.unlock();
        }
        this.indexCache.Set(address, record);
        return record;

    }

    public void UpdateRecord(final HashIndexRecord record){

    }

    public void RefreshCache(){

    }

    private static final short GetIndexFileCode(final byte[] address){
        return (short)(address[Parameter.HASH_FILE_CODE_POSITION] + 128);
    }

    private static final int GetLocationInIndexFile(final byte[] address){
        int locationInFile = 0;
        for(int i = Parameter.HASH_FRAGMENT_END; i >= Parameter.HASH_FRAGMENT_BEGIN; i--){
            locationInFile += (int)((address[i] + 128) * Math.pow(256, Parameter.HASH_FRAGMENT_END - i));
        }
        return locationInFile;
    }

    private HashConflictLocation GetNextFreeHashConflictLocation() throws IOException, IllegalAccessException{
        if(this.hashConflictFile.size() == 0){
            final String fileName = HashIndexFile.GetHashConflictFileNameByCode((short)0);
            this.hashConflictFile.add(0, new HashIndexFile((short)0, fileName, "rw", true));
        }
        HashIndexFile lastFile = this.hashConflictFile.get(this.hashConflictFile.size() - 1);
        if (lastFile.FreeNum() <= 0){
            String newFileName = HashIndexFile.GetHashConflictFileNameByCode((short)this.hashConflictFile.size());
            this.hashConflictFile.add(this.hashConflictFile.size(),
                    new HashIndexFile((short)this.hashConflictFile.size(), newFileName, "rw", true));
        }
        lastFile = this.hashConflictFile.get(this.hashConflictFile.size() - 1);
        HashConflictLocation location = new HashConflictLocation((short)(this.hashConflictFile.size() - 1),
                                                                  lastFile.GetNextFreeLocation(), true);
        return location;
    }
}
