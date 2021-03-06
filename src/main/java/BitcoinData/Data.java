package BitcoinData;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.locks.ReentrantLock;

public class Data {
    private HashIndex hashIndex;
    private DataFile[] dataFiles;
    private short maxFileCode;
    private ReentrantLock getNextFreeSectionLock;

    public Data() throws IOException, IllegalArgumentException, IllegalAccessException{
        this.dataFiles = new DataFile[Parameter.MAX_DATA_FILE];
        this.maxFileCode = -1;
        for(short c = 0; c < Parameter.MAX_DATA_FILE; c++){
            String name = DataFile.GetDataFileNameByCode(c);
            File dataFile = new File(Parameter.DATA_PATH + name);
            if(dataFile.exists()){
                this.dataFiles[c] = new DataFile(c);
                this.maxFileCode = c;
            }
            else{
                break;
            }
        }
        this.getNextFreeSectionLock = new ReentrantLock();
        this.hashIndex = new HashIndex(this);
    }

    public SectionLocation GetNextFreeSectionLocation() throws IOException, IllegalArgumentException, IllegalAccessException{
//        this.getNextFreeSectionLock.lock();
        if(this.maxFileCode < 0){
            this.dataFiles[0] = new DataFile((short)0);
            this.maxFileCode = 0;
        }
        int nextFreeLocationInFile = this.dataFiles[this.maxFileCode].GetNextFreeSectionLocation();
        if(nextFreeLocationInFile < 0){
            this.maxFileCode++;
            if(this.maxFileCode >= Parameter.MAX_DATA_FILE){
                throw new IllegalAccessException("attemp to create too many data file.");
            }
            this.dataFiles[this.maxFileCode] = new DataFile(this.maxFileCode);
            nextFreeLocationInFile = this.dataFiles[this.maxFileCode].GetNextFreeSectionLocation();
        }
//        this.getNextFreeSectionLock.unlock();
        return new SectionLocation(this.maxFileCode, nextFreeLocationInFile);
    }

    public void SetRecord(final byte[] address, final SectionRecord record) throws IOException, IllegalAccessException{
        HashIndexRecord indexRecord = this.hashIndex.WriteSearch(address);
        Section lastSection = this.GetSection(indexRecord.LastSectionLocation());
        lastSection.AppendRecord(record);
        this.dataFiles[lastSection.SelfLocation().fileCode].SetSection(lastSection, true);
    }

    private Section GetSection(SectionLocation location) throws  IOException{
        if(this.dataFiles[location.fileCode] == null){
            throw new IllegalArgumentException("the data file for the location is null");
        }
        return this.dataFiles[location.fileCode].GetSection(location.locationInFile);
    }

    public final Section GetSection(final byte[] address) throws IOException{
        HashIndexRecord indexRecord = this.hashIndex.ReadSearch(address);
        return this.GetSection(indexRecord.LastSectionLocation());
    }
}
