import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class FileUtils {

    final static private String TRANS_DATA_PATH = "./src/main/resources/train_data";

    //final static private String TEST_DATA_PATH = "./src/main/resources/test_data";

    static private BufferedReader buildBufferedReader(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(path)));
    }

    static public HashMap<Integer,NodeK> readTrainData() throws IOException {
    	HashMap<Integer,NodeK> data = new HashMap<>();
        try (BufferedReader bf = buildBufferedReader(TRANS_DATA_PATH);) {
            String thisRow;
            while ((thisRow = bf.readLine()) != null) {
                String[] slices = thisRow.split(",");
                NodeK node=new NodeK(Double.valueOf(slices[1]),Double.valueOf(slices[2]),-1,-1);
                data.put(Integer.valueOf(slices[0]),node);
            }
            return data;
        }
    }
}
