import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class FileUtils {

    final static private String TRANS_DATA_PATH = "./src/main/resources/train_data";

    //final static private String TEST_DATA_PATH = "./src/main/resources/test_data";

    static private BufferedReader buildBufferedReader(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(path)));
    }

    // static public Map<xxx,xxx> xxx
    static public HashMap<Integer, NodeK> readTrainData() throws IOException {
        // todo 使用Map,List一般在接口定义里都是用的接口类,而不是具体的类
        // Man<xxx,xxx> data = new HashMap<>();
        HashMap<Integer, NodeK> data = new HashMap<>();
        try (BufferedReader bf = buildBufferedReader(TRANS_DATA_PATH);) {
            String thisRow;
            while ((thisRow = bf.readLine()) != null) {
                String[] slices = thisRow.split(",");
                // todo -1 是 magic number,在NodeK里面用一个常量来代替吧
                NodeK node = new NodeK(Double.valueOf(slices[1]), Double.valueOf(slices[2]), NodeK.INVALID_LABEL, NodeK.INVALID_LABEL);
                data.put(Integer.valueOf(slices[0]), node);
            }
            return data;
        }
    }
}
