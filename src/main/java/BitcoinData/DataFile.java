package BitcoinData;
import java.util.HashSet;
import BitcoinData.Parameter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;

public class DataFile {
    private int code;
    private String filePath;
    private RandomAccessFile dataFile;
    private long sectionNum;
    private HashSet<Integer> freeLocationSet;

    public DataFile(short code) throws IOException, IllegalArgumentException, IllegalAccessException {
        this.code = code;
        this.filePath = Parameter.DATA_PATH + GetDataFileNameByCode(code);
        File file = new File(this.filePath);
        this.freeLocationSet = new HashSet<Integer>();
        if(!file.exists()){
            file.createNewFile();
            this.dataFile = new RandomAccessFile(this.filePath, "rw");
            byte[] headBytes = new byte[Parameter.DATA_FILE_HEAD_SIZE];
            for(int i = 0; i < headBytes.length; i++){
                headBytes[i] = Utils.MAX_BYTE;
                if (i < Parameter.MAX_SECTION_IN_DATA_FILE){
                    this.freeLocationSet.add(i);
                }
            }
            this.dataFile.write(headBytes);

            long contentSize = Parameter.DATA_FILE_SIZE - 2 * Parameter.SIZE_OF_LONG;
            long extendUnitSize = (long)Math.pow(2, 27);
            long extendedSize = 0;
            while(extendedSize < contentSize){
                long stepSize = Math.min(extendUnitSize, contentSize - extendedSize);
                byte[] bytes = new byte[(int)stepSize];
                for(int b = 0; b < bytes.length; b++){
                    bytes[b] = Utils.APPOINT_INVALID_BYTE;
                }
                this.dataFile.seek(this.dataFile.length());
                this.dataFile.write(bytes);
                extendedSize += stepSize;
            }
        }
        else{
            this.dataFile = new RandomAccessFile(this.filePath, "rw");
            this.dataFile.seek(0);
            byte[] headBytes = new byte[Parameter.DATA_FILE_HEAD_SIZE];
            this.dataFile.read(headBytes);
            for(int i = 0; i < Parameter.MAX_SECTION_IN_DATA_FILE; i++){
                if (headBytes[i] == Utils.MAX_BYTE){
                    this.freeLocationSet.add(i);
                }
            }
        }
        this.sectionNum = Parameter.MAX_SECTION_IN_DATA_FILE - this.freeLocationSet.size();
    }

    private boolean IsFree(int location){
        return this.freeLocationSet.contains(location);
    }

    public void SetSection(Section section, boolean replace) throws IOException{
        if(section.SelfLocation().fileCode != this.code){
            throw new IllegalArgumentException("section.SelfFileCode is not equal to this.code.");
        }
        if(section.SelfLocation().locationInFile >= Parameter.MAX_SECTION_IN_DATA_FILE ){
            throw new IllegalArgumentException("section.SelfLocationInFile must less than MAX_SECTION_IN_DATA_FILE");
        }
        if(!replace && !this.IsFree(section.SelfLocation().locationInFile)){
            throw new IllegalArgumentException("section.SelfLocationInFile must be free when 'replace' is true.");
        }
        if(Parameter.CHECK_BEFOR_WRITE_DATA){
            Section oldSection = this.GetSection(section.SelfLocation().locationInFile);
            if(!replace) {
                if (oldSection.IsValid()) {
                    throw new IllegalArgumentException("a valid section has exist in the special location.");
                }
            }
            if(replace){
                if(!oldSection.Address().equals(section.Address())){
                    throw new IllegalArgumentException("replace error, " +
                            "the address of old secion is not equal to that of secion.");
                }
            }
        }

        if(!replace) {
            this.sectionNum += 1;
            this.freeLocationSet.remove(section.SelfLocation().locationInFile);
            this.dataFile.seek(section.SelfLocation().locationInFile);
            this.dataFile.write(Utils.MIN_BYTE);
        }

        long byteFileLocation = Parameter.DATA_FILE_HEAD_SIZE + section.SelfLocation().locationInFile * Parameter.SECTION_SIZE;
        this.dataFile.seek(byteFileLocation);
        this.dataFile.write(section.Bytes());
    }

    public Section GetSection(int locationInFile) throws IOException{
        if(locationInFile >= Parameter.MAX_SECTION_IN_DATA_FILE){
            throw new IllegalArgumentException("locationInFile is large than MAX_SECTION_IN_DATA_FILE");
        }
        long byteFileLocation = Parameter.DATA_FILE_HEAD_SIZE + locationInFile * Parameter.SECTION_SIZE;
        byte[] bytes = new byte[Parameter.SECTION_SIZE];
        this.dataFile.seek(byteFileLocation);
        this.dataFile.read(bytes);
        return new Section(bytes);
    }

    public int FreeLocationsNum(){
        return this.freeLocationSet.size();
    }

    private int SectionNum(){
        return (int)this.sectionNum;
    }

    public int GetNextFreeSectionLocation() throws IOException{
        if(this.freeLocationSet.size() == 0){
            return -1;
        }
        int aFreeLocation = -1;
        for(int location : this.freeLocationSet){
            aFreeLocation = location;
            break;
        }
        if(aFreeLocation >= 0){
            this.sectionNum++;
            this.freeLocationSet.remove(aFreeLocation);
            this.dataFile.seek(aFreeLocation);
            this.dataFile.write(Utils.MIN_BYTE);
        }
        return aFreeLocation;
    }

    public static final String GetDataFileNameByCode(short code){
        return String.format("data_%d.data", code);
    }
}
