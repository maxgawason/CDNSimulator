import java.rmi.server.ExportException;
import java.util.*;

public class CDNAccessNode extends CDNNode {
    TreeMap<String, List<Long>> waitingRequests;
    Cache cache;
    List<String> objectsToBeRequested;
    Long totalLatency = 0L;
    Long numPacketsProcessed = 0L;
    public CDNAccessNode(String cachePolicyType) {
        super();
        objectsToBeRequested = new ArrayList<String>();
        cache = new LRU(100L, "LRU");
        waitingRequests = new TreeMap<String, List<Long>>();
    }

    public List<String> outgoingRequests() {
        return objectsToBeRequested;
    }

    public void receiveData(List<String> receivedData) {
        for (String object : receivedData) {
            cache.insertObject(object);
        }
    }

    public void processOldRequests(Long time) {
        Set<String> keys = new TreeSet<String>(waitingRequests.keySet());
        for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
            String key = iterator.next();
            if (currentObjects.contains(key) || cache.containsObject(key)) {
                for (Long timestamp : waitingRequests.get(key)) {
                    totalLatency += time - timestamp;
                    numPacketsProcessed++;
                }
                waitingRequests.remove(key);
            }
        }
    }

    public void processNewRequests(List<String> newRequests, Long time) {
        for (String object : newRequests) {
            if (!currentObjects.contains(object) && !cache.containsObject(object)) {
                acceptRequest(object, time);
            }
        }
    }

    public void acceptRequest(String object, Long time) {
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
