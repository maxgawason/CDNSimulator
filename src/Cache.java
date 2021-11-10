import java.util.HashSet;

public abstract class Cache {
    Long numObjectsInCache;
    Long cacheSize;

    public Cache(Long cacheSize) {
        numObjectsInCache = 0L;
        this.cacheSize = cacheSize;
    }

    public void insertObject(String object) {
        if (numObjectsInCache < cacheSize) {
            numObjectsInCache++;
        } else {
            evictObject();
        }
        addObject(object);
    }

    abstract void addObject(String object);

    abstract void evictObject();

    abstract boolean containsObject(String object);
}
