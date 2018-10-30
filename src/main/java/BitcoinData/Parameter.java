package BitcoinData;


public class Parameter {
    public static final String DATA_ROOT = "/data/bitcoin_data/";
    public static final String INDEX_PATH = DATA_ROOT + "index/";
    public static final String DATA_PATH = DATA_ROOT + "data/";

    public static final String DATA_FILE_PREFIX = "data_";
    public static final String HASH_INDEX_FILE_PREFIX = "hash_index_";
    public static final String HASH_CONFLICT_INDEX_FILE_PREFIX = "hash_conflict_";

    public static final int SIZE_OF_BYTE = 1;
    public static final int SIZE_OF_SHORT = 2;
    public static final int SIZE_OF_INT = 4;
    public static final int SIZE_OF_LONG = 8;
    public static final int SIZE_OF_DOUBLE = 8;
    public static final int SIZE_OF_BOOL = 1;

    public static final int MAX_DATA_FILE = 256;
    public static final int ADDRESS_SIZE = 25;
    public static final int SECTION_RECORD_NUM = 5;
    public static final int SECTION_LOCATOR_SIZE = SIZE_OF_SHORT + SIZE_OF_INT;
    public static final int TRANS_HASH_SIZE = 32;
    public static final int SECTION_HEAD_SIZE = ADDRESS_SIZE + SECTION_LOCATOR_SIZE + SECTION_LOCATOR_SIZE +
                                                SIZE_OF_SHORT + SIZE_OF_SHORT + SIZE_OF_INT;
    public static final int SECTION_RECORD_SIZE =
             (TRANS_HASH_SIZE + SIZE_OF_INT + SIZE_OF_INT +
                                SIZE_OF_BOOL + SIZE_OF_BOOL + SIZE_OF_BOOL +
                                SIZE_OF_DOUBLE + SIZE_OF_DOUBLE +
                                SIZE_OF_INT + SIZE_OF_INT +
                                SIZE_OF_DOUBLE + SIZE_OF_DOUBLE);
            // trans hash (32byte) | height (int) | timestamp (int) |
            //                       is from coinbase (byte) | is in (byte) | is out (byte)
            //                       in value (double) | out value (double) |
            //                       total in address (int) | total out address (int)
            //                       total in value (double) | total out value (double)
    public static final int SECTION_SIZE = SECTION_HEAD_SIZE + SECTION_RECORD_NUM * SECTION_RECORD_SIZE;
    public static final long DATA_FILE_SIZE = SIZE_OF_LONG + SIZE_OF_LONG + 2 * (long) Math.pow(2, 30);   // 2GB
    public static final long MAX_SECTION_IN_DATA_FILE = (DATA_FILE_SIZE - SIZE_OF_LONG - SIZE_OF_LONG) / SECTION_SIZE;
    public static final int DATA_FILE_EXTEND_SIZE = (int) Math.pow(2, 25);  // 32 MB

    public static final int ADDRESS_HASH_INDEX_RECORD_SIZE = ADDRESS_SIZE +
                                                              SIZE_OF_SHORT + SIZE_OF_INT +
                                                              (SIZE_OF_SHORT + SIZE_OF_INT) * 2 +
                                                              SIZE_OF_SHORT + SIZE_OF_INT +
                                                              SIZE_OF_BOOL ;  //50 bytes
    public static final int HASH_FILE_CODE_POSITION = 4;
    public static final int HASH_FRAGMENT_BEGIN = 1;
    public static final int HASH_FRAGMENT_END = 3;
    public static final int ADDRESS_HASH_SIZE = HASH_FRAGMENT_END - HASH_FRAGMENT_BEGIN + 1;
    public static final int MAX_RECORD_IN_HASH_INDEX_FILE = (int)Math.pow(2, ADDRESS_HASH_SIZE * 8);
    public static final long HASH_INDEX_FILE_SIZE = SIZE_OF_LONG + SIZE_OF_LONG +
            MAX_RECORD_IN_HASH_INDEX_FILE * ADDRESS_HASH_INDEX_RECORD_SIZE;  // 688MB

    public static final int MAX_HASH_INDEX_FILE = 256;
    public static final int MAX_HASH_CONFLICT_FILE = 256;
    public static final int HASH_INDEX_MAX_CACHE_NUM = 10000000;

    public static final boolean CHECK_BEFOR_WRITE_HASH_INDEX_RECORD = true;
    public static final boolean CHECK_BEFOR_WRITE_DATA = true;


}
