import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class CDNClient {
    File dataFile;
    Scanner scanner;
    public CDNClient(String dataFileName) {
        dataFile = new File(dataFileName);
        try {
            scanner = new Scanner(dataFile);
        } catch (FileNotFoundException ex) {

        }
    }

    public LinkedList<Long> getCurrentRequests(Long currentTime) {
        LinkedList<Long> currentRequests = new LinkedList<Long>();
        while (scanner.hasNext(currentTime.toString())) {
            String fullRequestLine = scanner.nextLine();
            String[] splitRequestLine = fullRequestLine.split("\\s+");
            currentRequests.add(Long.parseLong(splitRequestLine[1]));
        }
        return currentRequests;
    }

    public boolean hasMoreData() {
        return scanner.hasNext();
    }
}
