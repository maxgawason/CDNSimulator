import java.util.List;
import java.lang.System;

public class CDNSimulator {
    String cdnDataFile;
    String cachePolicyType;
    CDNNode deNode;
    CDNNode sgNode;
    CDNAccessNode vaNode;
    NetworkPipe vaDePipe;
    NetworkPipe deVaPipe;
    NetworkPipe vaSgPipe;
    NetworkPipe sgVaPipe;
    CDNClient client;
    Long time = 0L;
    String biggestObject;
    Long cacheSize;
    public static void main(String[] args) {
        System.out.println("Welcome to the CDN Simulator with delayed hits");
        CDNSimulator sim = new CDNSimulator("wiki2018-tiny.tr", "LRU", 200L);
        sim.init();
        sim.run();
        sim.outputStatistics();
    }

    public CDNSimulator(String cdnDataFile, String cachePolicyType, Long cacheSize) {
        this.cdnDataFile = cdnDataFile;
        this.cachePolicyType = cachePolicyType;
        this.cacheSize = cacheSize;
    }

    public void outputStatistics() {
        vaNode.printStatistics();
    }

    public void init() {
        //find biggest object
        biggestObject = TracePreprocessor.findHighestObject(cdnDataFile);
        //create CDN Nodes
        deNode = new CDNNode();
        sgNode = new CDNNode();
        vaNode = new CDNAccessNode(cachePolicyType, cacheSize);
        //create pipes
        vaDePipe = new NetworkPipe(30);
        deVaPipe = new NetworkPipe(30);
        vaSgPipe = new NetworkPipe(20);
        sgVaPipe = new NetworkPipe(20);
        //create client
        client = new CDNClient(cdnDataFile);

        Long numObjects = Long.parseLong(biggestObject);
        for (Long object = 0L; object <= numObjects; object++) {
            if (object % 3 == 0) {
                deNode.insertObject(object);
            } else if (object % 3 == 1) {
                vaNode.insertObject(object);
            } else if (object % 3 == 2) {
                sgNode.insertObject(object);
            }
        }
    }

    public void run() {
        while (client.hasMoreData()) {
            stepWithNewData();
            time++;
        }
        Long waitTime = 70L;
        while (vaNode.hasWaitingRequests() && waitTime >= 0) {
            stepWithoutNewData();
            time++;
            waitTime--;
        }
    }

    public void stepWithoutNewData() {
        System.out.println("stepping without new data: " + time);
        //Long t1 = System.nanoTime();
        processIncomingNetworkPipes();
        //Long t2 = System.nanoTime();
        vaNode.processOldRequests(time);
        //Long t3 = System.nanoTime();
        processOutgoingNetworkPipes();
        //Long t4 = System.nanoTime();
        //System.out.println("Incoming: " + String.valueOf(t2 - t1).length() + " old requests: " + String.valueOf(t3 - t2).length() + " outgoing: " + String.valueOf(t4 - t3).length());
    }

    public void processIncomingNetworkPipes() {
        Long t1 = System.nanoTime();
        vaDePipe.advanceData();
        vaSgPipe.advanceData();
        sgVaPipe.advanceData();
        deVaPipe.advanceData();
        Long t2 = System.nanoTime();
        List<Long> vaDeOutData = vaDePipe.getOutData();
        List<Long> vaSgOutData = vaSgPipe.getOutData();
        List<Long> sgVaOutData = sgVaPipe.getOutData();
        List<Long> deVaOutData = deVaPipe.getOutData();
        Long t3 = System.nanoTime();
        vaNode.receiveData(sgVaOutData);
        vaNode.receiveData(deVaOutData);
        Long t5 = System.nanoTime();
        sgNode.receiveRequestData(vaSgOutData);
        deNode.receiveRequestData(vaDeOutData);
        Long t4 = System.nanoTime();
        System.out.println("advance: " + String.valueOf(t2 - t1).length() + " get out: " + String.valueOf(t3 - t2).length() + " recieve1: " + String.valueOf(t5 -t3).length() + " recieve2: " + String.valueOf(t4-t5).length());
    }

    public void processNewRequests() {
        List<Long> newRequests = client.getCurrentRequests(time);
        vaNode.processNewRequests(newRequests, time);
    }

    public void processOutgoingNetworkPipes() {
        List<Long> outgoingRequests = vaNode.outgoingRequests();
        vaDePipe.addRequests(outgoingRequests, time);
        vaSgPipe.addRequests(outgoingRequests, time);
        outgoingRequests = sgNode.outgoingRequests();
        sgVaPipe.addRequests(outgoingRequests, time);
        outgoingRequests = deNode.outgoingRequests();
        deVaPipe.addRequests(outgoingRequests, time);
    }
    public void stepWithNewData() {
        System.out.println("stepping with new data");
        processIncomingNetworkPipes();
        vaNode.processOldRequests(time);
        processNewRequests();
        processOutgoingNetworkPipes();
    }

}
