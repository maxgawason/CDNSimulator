import java.util.HashSet;

public abstract class Cache {
    Long numObjectsInCache;
    Long cacheSize;

    public Cache(Long cacheSize) {
        numObjectsInCache = 0L;
        this.cacheSize = cacheSize;
    }

    public void insertObject(Long object, Long time) {
        if (numObjectsInCache < cacheSize) {
            numObjectsInCache++;
        } else {
            evictObject();
        }
        addObject(object, time);
    }

    abstract void addObject(Long object, Long time);

    abstract void evictObject();

    abstract boolean containsObject(Long object, Long time);
}
