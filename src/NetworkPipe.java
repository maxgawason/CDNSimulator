import java.util.LinkedList;
import java.util.List;

public class NetworkPipe {
    int avgLatencyMillis;
    int ringLocation = 0;
    LinkedList<Long>[] currentPackets;

    public NetworkPipe(int avgLatencyMillis) {
        this.avgLatencyMillis = avgLatencyMillis;
        currentPackets = new LinkedList[avgLatencyMillis];
        for (int i = 0; i < avgLatencyMillis; i++) {
            currentPackets[i] = new LinkedList<Long>();
        }
    }

    private double getLatency() {
        return avgLatencyMillis;
    }

    public void addRequests(List<Long> newRequests, Long time) {
        LinkedList<Long> newPackets = new LinkedList<Long>();
        for (Long object : newRequests) {
            newPackets.add(object);
        }
        currentPackets[ringLocation] = newPackets;
    }

    public void advanceData() {
        ringLocation++;
        ringLocation = ringLocation % (avgLatencyMillis - 1);
    }

    public List<Long> getOutData() {
        return currentPackets[ringLocation];
    }
}
