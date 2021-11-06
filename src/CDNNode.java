import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CDNNode {
    HashSet<String> currentObjects;
    List<String> objectsToBeSentOut;
    public CDNNode() {
        currentObjects = new HashSet<String>();
        objectsToBeSentOut = new ArrayList<String>();
    }

    public void receiveRequestData(List<String> requestedData) {
        for (String object : requestedData) {
            if (currentObjects.contains(object)) {
                objectsToBeSentOut.add(object);
            }
        }
    }

    public void insertObject(String object) {
        currentObjects.add(object);
    }

    public List<String> outgoingRequests() {
        return objectsToBeSentOut;
    }

}
