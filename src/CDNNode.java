import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CDNNode {
    HashSet<Long> currentObjects;
    List<Long> objectsToBeSentOut;
    public CDNNode() {
        currentObjects = new HashSet<Long>();
        objectsToBeSentOut = new ArrayList<Long>();
    }

    public void receiveRequestData(List<Long> requestedData) {
        for (Long object : requestedData) {
            if (currentObjects.contains(object)) {
                objectsToBeSentOut.add(object);
            }
        }
    }

    public void insertObject(Long object) {
        currentObjects.add(object);
    }

    public List<Long> outgoingRequests() {
        return objectsToBeSentOut;
    }

}
