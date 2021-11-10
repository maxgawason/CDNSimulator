import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class NetworkPacket {
    public Long object;
    public Long timeLeftInPipe;
    public NetworkPacket(Long object, Long timeLeftInPipe) {
        this.object = object;
        this.timeLeftInPipe = timeLeftInPipe;
    }

    public void advancePacket() {
        timeLeftInPipe--;
    }

    public boolean isPacketDelivered() {
        return timeLeftInPipe == 0;
    }
}

public class NetworkPipe {
    double avgLatencyMillis;
    List<Long> outData;
    LinkedList<NetworkPacket> travelingPackets;
    public NetworkPipe(double avgLatencyMillis) {
        this.avgLatencyMillis = avgLatencyMillis;
        outData = new ArrayList<Long>();
        travelingPackets = new LinkedList<NetworkPacket>();

    }

    private double getLatency() {
        return avgLatencyMillis;
    }

    public void addRequests(List<Long> newRequests, Long time) {
        for (Long object : newRequests) {
            travelingPackets.add(new NetworkPacket(object, time));
        }
    }

    public void advanceData() {
        //TODO: make sure this is correct
        Iterator<NetworkPacket> iterator = travelingPackets.iterator();
        while (iterator.hasNext()) {
            NetworkPacket packet = iterator.next();
            packet.advancePacket();
            if (packet.isPacketDelivered()) {
                outData.add(packet.object);
                iterator.remove();
            }
        }
    }

    public List<Long> getOutData() {
        return outData;
    }
}
