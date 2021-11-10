import java.util.LinkedList;
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
    Double cacheSizeAsPercent;
    Long startTime;
    Long endTime;
    public static void main(String[] args) {
        System.out.println("Welcome to the CDN Simulator with delayed hits");
        CDNSimulator sim = new CDNSimulator("wiki2018-med.tr", "LRU", 0.25);
        sim.init();
        sim.run();
        sim.outputStatistics();
    }

    public CDNSimulator(String cdnDataFile, String cachePolicyType, Double cacheSizeAsPercent) {
        this.cdnDataFile = cdnDataFile;
        this.cachePolicyType = cachePolicyType;
        this.cacheSizeAsPercent = cacheSizeAsPercent;
    }

    public void outputStatistics() {
        vaNode.printStatistics();
        System.out.println("Runtime: " + (endTime - startTime)/1000000000 + " seconds");
    }

    public void init() {
        //set start
        startTime = System.nanoTime();
        //find biggest object
        biggestObject = TracePreprocessor.findHighestObject(cdnDataFile);
        //create CDN Nodes
        deNode = new CDNNode();
        sgNode = new CDNNode();
        //create pipes
        vaDePipe = new NetworkPipe(30);
        deVaPipe = new NetworkPipe(30);
        vaSgPipe = new NetworkPipe(20);
        sgVaPipe = new NetworkPipe(20);
        //create client
        client = new CDNClient(cdnDataFile);
        //create CDN Access node
        Long numObjects = Long.parseLong(biggestObject);
        Long cacheSize = (long) ((numObjects / 3) * cacheSizeAsPercent);
        vaNode = new CDNAccessNode(cachePolicyType, cacheSize);
        //distribute data
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
        endTime = System.nanoTime();
    }

    public void stepWithoutNewData() {
        System.out.println("Timestep: " + time);
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
        LinkedList<Long> vaDeOutData = vaDePipe.getOutData();
        LinkedList<Long> vaSgOutData = vaSgPipe.getOutData();
        LinkedList<Long> sgVaOutData = sgVaPipe.getOutData();
        LinkedList<Long> deVaOutData = deVaPipe.getOutData();
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
        LinkedList<Long> newRequests = client.getCurrentRequests(time);
        vaNode.processNewRequests(newRequests, time);
    }

    public void processOutgoingNetworkPipes() {
        LinkedList<Long> outgoingRequests = vaNode.outgoingRequests();
        vaDePipe.addRequests(outgoingRequests);
        vaSgPipe.addRequests(outgoingRequests);
        outgoingRequests = sgNode.outgoingRequests();
        sgVaPipe.addRequests(outgoingRequests);
        outgoingRequests = deNode.outgoingRequests();
        deVaPipe.addRequests(outgoingRequests);
    }
    public void stepWithNewData() {
        System.out.println("Timestep: " + time);
        //Long t1 = System.nanoTime();
        processIncomingNetworkPipes();
        //Long t2 = System.nanoTime();
        vaNode.processOldRequests(time);
        //Long t3 = System.nanoTime();
        processNewRequests();
        //Long t4 = System.nanoTime();
        processOutgoingNetworkPipes();
        //Long t5 = System.nanoTime();
        //System.out.println("incoming: " + String.valueOf(t2 - t1).length() + " old requests: " + String.valueOf(t3 - t2).length() + " new requests: " + String.valueOf(t4 -t3).length() + " outgoing: " + String.valueOf(t5-t4).length());
    }

}
