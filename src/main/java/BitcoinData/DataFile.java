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

    public DataFile(int code) throws IOException, IllegalArgumentException, IllegalAccessException {
        this.code = code;
        this.filePath = Parameter.DATA_PATH + String.format("%d", code);
        File file = new File(this.filePath);
        if(!file.exists()){
            file.createNewFile();
            this.dataFile = new RandomAccessFile(this.filePath, "rw");
            long reservedSize = Parameter.SIZE_OF_LONG * 2;

            this.dataFile.writeLong(reservedSize);
            this.dataFile.writeLong(0);
        }
        else{
            this.dataFile = new RandomAccessFile(this.filePath, "rw");
            this.dataFile.seek(0);
        }
        this.dataFile.seek(0);
        this.reservedSize = this.dataFile.readLong();
        this.sectionNum = this.dataFile.readLong();
    }

    public void WriteSection(Section section){

    }
//
//    public Section ReadSection(long seq){
//
//    }

    private void Extend() throws IOException, IllegalAccessException{
        byte[] bytes = new byte[Parameter.DATA_FILE_EXTEND_SIZE];
        for(int b = 0; b < bytes.length; b++) {
            bytes[b] = Utils.APPOINT_INVALID_BYTE;
        }
        this.dataFile.seek(this.dataFile.length());
        this.dataFile.write(bytes);

        this.reservedSize += Parameter.DATA_FILE_EXTEND_SIZE;
        this.dataFile.seek(0);
        this.dataFile.writeLong(this.reservedSize);
    }

    private long UsedSize(){
        return this.sectionNum * Parameter.SECTION_SIZE * Parameter.SIZE_OF_LONG * 2;
    }
}
