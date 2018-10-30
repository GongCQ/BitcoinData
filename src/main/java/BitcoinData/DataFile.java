package BitcoinData;
import BitcoinData.Parameter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;

public class DataFile {
    private int code;
    private String filePath;
    private RandomAccessFile dataFile;
    private long reservedSize;
    private long sectionNum;

    public DataFile(short code) throws IOException, IllegalArgumentException, IllegalAccessException {
        this.code = code;
        this.filePath = Parameter.DATA_PATH + GetDataFileNameByCode(code);
        File file = new File(this.filePath);
        if(!file.exists()){
            file.createNewFile();
            this.dataFile = new RandomAccessFile(this.filePath, "rw");
            long reservedSize = Parameter.SIZE_OF_LONG * 2;

            this.dataFile.writeLong(reservedSize);
            this.dataFile.writeLong(0);

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
        }
        this.dataFile.seek(0);
        this.reservedSize = this.dataFile.readLong();
        this.sectionNum = this.dataFile.readLong();
    }

    public void SetSection(Section section, boolean replace) throws IOException{
        if(section.SelfFileCode() != this.code){
            throw new IllegalArgumentException("section.SelfFileCode is not equal to this.code.");
        }
        if(section.SelfLocationInFile() >= Parameter.MAX_SECTION_IN_DATA_FILE ){
            throw new IllegalArgumentException("section.SelfLocationInFile must less than MAX_SECTION_IN_DATA_FILE");
        }
        if(!replace && section.SelfLocationInFile() < this.sectionNum){
            throw new IllegalArgumentException("section.SelfLocationInFile must not less than this.sectionNum when 'replace' is true.");
        }
        if(Parameter.CHECK_BEFOR_WRITE_DATA){
            Section oldSection = this.GetSection(section.SelfLocationInFile());
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
            this.dataFile.seek(Parameter.SIZE_OF_LONG);
            this.dataFile.writeLong(this.sectionNum);
        }

        long byteFileLocation = Parameter.SIZE_OF_LONG * 2 + section.SelfLocationInFile() * Parameter.SECTION_SIZE;
        this.dataFile.seek(byteFileLocation);
        this.dataFile.write(section.Bytes());
    }

    public Section GetSection(int locationInFile) throws IOException{
        if(locationInFile >= Parameter.MAX_SECTION_IN_DATA_FILE){
            throw new IllegalArgumentException("locationInFile is large than MAX_SECTION_IN_DATA_FILE");
        }
        long byteFileLocation = Parameter.SIZE_OF_LONG * 2 + locationInFile * Parameter.SECTION_SIZE;
        byte[] bytes = new byte[Parameter.SECTION_SIZE];
        this.dataFile.seek(byteFileLocation);
        this.dataFile.read(bytes);
        return new Section(bytes);
    }

    private long UsedSize(){
        return this.sectionNum * Parameter.SECTION_SIZE * Parameter.SIZE_OF_LONG * 2;
    }

    public static final String GetDataFileNameByCode(short code){
        return String.format("data_%d.data", code);
    }
}
