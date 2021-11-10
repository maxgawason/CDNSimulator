import java.util.HashSet;

public abstract class Cache {
    Long numObjectsInCache;
    Long cacheSize;

    public Cache(Long cacheSize) {
        numObjectsInCache = 0L;
        this.cacheSize = cacheSize;
    }

    public void insertObject(Long object) {
        if (numObjectsInCache < cacheSize) {
            numObjectsInCache++;
        } else {
            evictObject();
        }
        addObject(object);
    }

    abstract void addObject(Long object);

    abstract void evictObject();

    abstract boolean containsObject(Long object);

    abstract void resetStatistics();

    abstract void printStatistics();
}
