package BitcoinData;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.io.File;
import java.util.ArrayList;

public class HashIndex {
    private Data data;
    private HashIndexFile[] indexFile;
    private ArrayList<HashIndexFile> hashConflictFile;
    private HashMap<byte[], HashIndexRecord> indexCache;

    public HashIndex(Data data) throws IOException, IllegalAccessException {
        this.data = data;
        // hash index file
        this.indexFile = new HashIndexFile[Parameter.MAX_HASH_INDEX_FILE];
        for(short i = 0; i < Parameter.MAX_HASH_INDEX_FILE; i++){
            String fileName = Parameter.INDEX_PATH + HashIndexFile.GetHashIndexFileNameByCode(i);
            this.indexFile[i] = new HashIndexFile(fileName, "rw", false);
        }

        // hash conflict file
        this.hashConflictFile = new ArrayList<HashIndexFile>(Parameter.MAX_HASH_CONFLICT_FILE);
        final File indexFilePath = new File(Parameter.INDEX_PATH);
        final File[] allIndexFile = indexFilePath.listFiles();
        for(final File file : allIndexFile){
            final String name = file.getName();
            final int prefixLength = Parameter.HASH_CONFLICT_INDEX_FILE_PREFIX.length();
            if (name.length() > prefixLength &&
                name.substring(0, prefixLength - 1).equals(Parameter.HASH_CONFLICT_INDEX_FILE_PREFIX)){
                final int fileCode = Integer.parseInt(name.substring(prefixLength, name.lastIndexOf('.') - 1));
                if(fileCode >= this.hashConflictFile.size()){
                    throw new IllegalAccessException(String.format("find a invalid hash conflict file, " +
                            "name %s, code %d.", name, fileCode));
                }
                this.hashConflictFile.add(fileCode, new HashIndexFile(file.getAbsolutePath(), "rw", true));
                System.out.println(String.format("find a hash conflict file, " + name + ", code %d", fileCode));
            }
        }
    }

    public final HashIndexRecord ReadSearch(final byte[] address) throws IOException{
        HashIndexRecord record = this.indexCache.get(address);  // memory cache
        if(record == null){
            record = this.indexFile[GetIndexFileCode(address)].GetRecord(GetLocationInIndexFile(address));  // hash index
            while(!address.equals(record.Address())){  // hash conflict
                if(!record.IsValid() || !record.HasNext() || this.hashConflictFile.get(record.NextConflictFileCode()) == null){
                    return null;
                }
                record = this.hashConflictFile.get(record.NextConflictFileCode()).GetRecord(record.NextConflictLocationInFile());
            }
        }
        return record;
    }

    public final HashIndexRecord WriteSearch(final byte[] address) throws IOException{
        HashIndexRecord record = this.indexCache.get(address);  // memory cache
        if(record == null){
            short hashIndexFileCode = GetIndexFileCode(address);
            int locationInHashIndexFile = GetLocationInIndexFile(address);
            record = this.indexFile[hashIndexFileCode].GetRecord(locationInHashIndexFile);
            if(address.equals(record.Address())){  // hash hit
                return record;
            }
            if(!record.IsValid()){  // hash free
                SectionLocation freeSectionLocation = this.data.GetNextFreeSectionLocation();
                HashIndexRecord newRecord = new HashIndexRecord(address,
                                                                Utils.GetAppointInvalidShort(), Utils.GetAppointInvalidInt(),
                                                                freeSectionLocation.fileCode, freeSectionLocation.locationInFile,
                                                                freeSectionLocation.fileCode, freeSectionLocation.locationInFile,
                                                                hashIndexFileCode, locationInHashIndexFile, (byte)-128);
                this.indexFile[hashIndexFileCode].SetRecord(locationInHashIndexFile, newRecord);
                return newRecord;
            }

            while(!address.equals(record.Address())){  // hash conflict
                if(!record.HasNext()){
                    HashConflictLocation nextFreeLocation = this.GetNextFreeHashConflictLocation();
                    HashIndexFile fileForRecord = record.IsConflict() ? this.hashConflictFile.get(record.SelfFileCode())
                                                                      : this.indexFile[record.SelfFileCode()];

                    SectionLocation freeSectionLocation = this.data.GetNextFreeSectionLocation();
                    HashIndexRecord newRecord = new HashIndexRecord(address,
                                                                    Utils.GetAppointInvalidShort(), Utils.GetAppointInvalidInt(),
                                                                    freeSectionLocation.fileCode, freeSectionLocation.locationInFile,
                                                                    freeSectionLocation.fileCode, freeSectionLocation.locationInFile,
                                                                    nextFreeLocation.fileCode, nextFreeLocation.locationInFile, (byte)127);
                    this.hashConflictFile.get(newRecord.SelfFileCode()).SetRecord(newRecord.SelfLocationInFile(), newRecord);

                    fileForRecord.SetNextLocation(record.SelfLocationInFile(), newRecord.SelfFileCode(), newRecord.SelfLocationInFile());

                    record = newRecord;
                    break;
                }
                else{
                    record = this.hashConflictFile.get(record.NextConflictFileCode()).GetRecord(record.NextConflictLocationInFile());
                }
            }
        }
        return record;

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

    private HashConflictLocation GetNextFreeHashConflictLocation() throws IOException{
        if(this.hashConflictFile.size() == 0){
            final String fileName = HashIndexFile.GetHashConflictFileNameByCode((short)0);
            this.hashConflictFile.add(0, new HashIndexFile(fileName, "rw", true));
        }
        HashIndexFile lastFile = this.hashConflictFile.get(this.hashConflictFile.size() - 1);
        if (lastFile.FreeNum() <= 0){
            String newFileName = HashIndexFile.GetHashConflictFileNameByCode((short)this.hashConflictFile.size());
            this.hashConflictFile.add(this.hashConflictFile.size(), new HashIndexFile(newFileName, "rw", true));
        }
        lastFile = this.hashConflictFile.get(this.hashConflictFile.size() - 1);
        HashConflictLocation location = new HashConflictLocation((short)(this.hashConflictFile.size() - 1),
                                                                  lastFile.GetNextFreeLocation());
        return location;
    }
}
