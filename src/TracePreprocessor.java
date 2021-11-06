import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TracePreprocessor {
    public static String findHighestObject(String file) {
        File f = new File(file);
        Long biggestObject = 0L;
        try {
            Scanner scanner = new Scanner(f);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] splitLine = line.split("\\s+");
                if (Long.parseLong(splitLine[1]) > biggestObject) {
                    biggestObject = Long.parseLong(splitLine[1]);
                }
            }
        } catch (FileNotFoundException ex) {

        }
        return biggestObject.toString();
    }
}
