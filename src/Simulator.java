import java.util.List;

public class Simulator {
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
        Simulator sim = new Simulator("wiki2018-tiny.tr", "LRU", 200L);
        sim.init();
        sim.run();
        sim.outputStatistics();
    }

    public Simulator(String cdnDataFile, String cachePolicyType, Long cacheSize) {
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
                deNode.insertObject(object.toString());
            } else if (object % 3 == 1) {
                vaNode.insertObject(object.toString());
            } else if (object % 3 == 2) {
                sgNode.insertObject(object.toString());
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
        processIncomingNetworkPipes();
        vaNode.processOldRequests(time);
        processOutgoingNetworkPipes();
    }

    public void processIncomingNetworkPipes() {
        vaDePipe.advanceData();
        vaSgPipe.advanceData();
        sgVaPipe.advanceData();
        deVaPipe.advanceData();
        List<String> vaDeOutData = vaDePipe.getOutData();
        List<String> vaSgOutData = vaSgPipe.getOutData();
        List<String> sgVaOutData = sgVaPipe.getOutData();
        List<String> deVaOutData = deVaPipe.getOutData();
        vaNode.receiveData(sgVaOutData);
        vaNode.receiveData(deVaOutData);
        sgNode.receiveRequestData(vaSgOutData);
        deNode.receiveRequestData(vaDeOutData);
    }

    public void processNewRequests() {
        List<String> newRequests = client.getCurrentRequests(time);
        vaNode.processNewRequests(newRequests, time);
    }

    public void processOutgoingNetworkPipes() {
        List<String> outgoingRequests = vaNode.outgoingRequests();
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
