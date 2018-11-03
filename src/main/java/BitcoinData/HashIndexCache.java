package BitcoinData;
//import java.util.HashMap;
import java.util.Date;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class HashIndexCache {
    private ConcurrentHashMap<byte[], HashIndexCacheItem> cacheItemMap;
    private ReentrantLock clearOutLock;

    public HashIndexCache(){
        this.cacheItemMap = new ConcurrentHashMap<byte[], HashIndexCacheItem>(Parameter.HASH_INDEX_MAX_CACHE_NUM);
        this.clearOutLock = new ReentrantLock();
    }

    public HashIndexRecord Get(byte[] address){
        HashIndexCacheItem item = this.cacheItemMap.get(address);
        if(item == null){ // don't hit
            return null;
        }
        else{
            item.lastHitTime = new Date();
            return item.indexRecord;
        }
    }

    public void Set(byte[] address, HashIndexRecord record){
        HashIndexCacheItem item = this.cacheItemMap.get(address);
        if(item == null) {// don't hit;
            item = new HashIndexCacheItem();
            item.lastHitTime = new Date();
            item.indexRecord = record;
            this.cacheItemMap.put(address, item);
            this.ClearOut(0.1);
        }
        else{
            item.lastHitTime = new Date();
            item.indexRecord = record;
        }
    }

    private void ClearOut(double tolerate){
        int tolerateSize = (int)(Parameter.HASH_INDEX_MAX_CACHE_NUM * (1 + tolerate));
        if(this.cacheItemMap.size() >= tolerateSize){
            this.clearOutLock.lock();
            Date[] lastHitTimeSort = new Date[this.cacheItemMap.size()];
            int count = 0;
            for(ConcurrentHashMap.Entry<byte[], HashIndexCacheItem> entry: this.cacheItemMap.entrySet()){
                lastHitTimeSort[count] = entry.getValue().lastHitTime;
                count++;
            }
            Arrays.sort(lastHitTimeSort);
            Date cut = lastHitTimeSort[(int)(tolerate / (1 + tolerate) * lastHitTimeSort.length)];
            for(Iterator<ConcurrentHashMap.Entry<byte[], HashIndexCacheItem>> ite = this.cacheItemMap.entrySet().iterator(); ((Iterator) ite).hasNext();){
                ConcurrentHashMap.Entry<byte[], HashIndexCacheItem> item = ite.next();
                if(item.getValue().lastHitTime.compareTo(cut) <= 0){
                    ite.remove();
                }
            }
            this.clearOutLock.unlock();
        }
    }

    public int Size(){
        return this.cacheItemMap.size();
    }
}
