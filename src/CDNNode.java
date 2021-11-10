import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class CDNNode {
    HashSet<Long> currentObjects;
    LinkedList<Long> objectsToBeSentOut;
    public CDNNode() {
        currentObjects = new HashSet<Long>();
        objectsToBeSentOut = new LinkedList<Long>();
    }

    public void receiveRequestData(LinkedList<Long> requestedData) {
        for (Long object : requestedData) {
            if (currentObjects.contains(object)) {
                objectsToBeSentOut.add(object);
            }
        }
    }

    public void insertObject(Long object) {
        currentObjects.add(object);
    }

    public LinkedList<Long> outgoingRequests() {
        return objectsToBeSentOut;
    }

}
