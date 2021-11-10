import java.rmi.server.ExportException;
import java.util.*;

public class CDNAccessNode extends CDNNode {
    TreeMap<Long, List<Long>> waitingRequests;
    Cache cache;
    LinkedList<Long> objectsToBeRequested;
    Long totalLatency = 0L;
    Long numPacketsProcessed = 0L;
    public CDNAccessNode(String cachePolicyType, Long cacheSize) {
        super();
        objectsToBeRequested = new LinkedList<Long>();
        if (cachePolicyType.equals("LRU")) {
            cache = new LRU(cacheSize);
        } else {
            cache = new MAD(cacheSize);
        }
        waitingRequests = new TreeMap<Long, List<Long>>();
    }

    public LinkedList<Long> outgoingRequests() {
        return objectsToBeRequested;
    }

    public void receiveData(LinkedList<Long> receivedData) {
        for (Long object : receivedData) {
            cache.insertObject(object);
        }
    }

    public void processOldRequests(Long time) {
        Set<Long> keys = new TreeSet<Long>(waitingRequests.keySet());
        for (Iterator<Long> iterator = keys.iterator(); iterator.hasNext();) {
            Long key = iterator.next();
            if (currentObjects.contains(key) || cache.containsObject(key)) {
                for (Long timestamp : waitingRequests.get(key)) {
                    totalLatency += time - timestamp;
                    numPacketsProcessed++;
                }
                waitingRequests.remove(key);
            }
        }
    }

    public void processNewRequests(LinkedList<Long> newRequests, Long time) {
        for (Long object : newRequests) {
            if (!currentObjects.contains(object) && !cache.containsObject(object)) {
                acceptRequest(object, time);
            }
        }
    }

    public void acceptRequest(Long object, Long time) {
        if (waitingRequests.containsKey(object)) {
            waitingRequests.get(object).add(time);
            objectsToBeRequested.add(object);
        } else {
            ArrayList<Long> timestamps = new ArrayList<Long>(Arrays.asList(time));
            waitingRequests.put(object, timestamps);
        }
    }

    public boolean hasWaitingRequests() {
        return !waitingRequests.isEmpty();
    }

    public void printStatistics() {
        System.out.println("Number of Packets Processed: " + numPacketsProcessed);
        System.out.println("Total Latency: " + totalLatency);
        System.out.println("Average Latency: " + (totalLatency / (double) numPacketsProcessed));
    }
}
