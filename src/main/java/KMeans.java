import java.io.IOException;
import java.util.HashMap;

// todo 可以考虑kmean和kmedoids的共性,提出一个基类来共享一些代码~

public class KMeans {

    static int k; // k个簇
    static int[] key; //k个簇标号
    static HashMap<Integer, NodeK> keydata;
    static HashMap<Integer, NodeK> data; // 装所有的数据

    /* 读取文件，将数据存储在data中 */
    static void readData() throws IOException {
        data = FileUtils.readTrainData();
    }

    /* 初始化随机选择中心点 */
    static void initKey() {
        key = new int[k];
        for (int i = 0; i < k; i++) {
            int temp = (int) (Math.random() * data.size()) + 1;
            boolean flag = true;
            for (int j = 0; j < i; j++) {
                if (temp == key[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                key[i] = temp;
            } else
                i--;
            keydata.put(key[i], data.get(Integer.valueOf(key[i])));
        }
        /*
		 * key[0]=4; key[1]=6;
		 */
        System.out.println(key[0] + "　" + key[1]);
    }

    static void initCluster() {
        // todo 如果是遍历map的话不要用这种写法,用下面的写法更快,试分析两者有何不同
//		for(Map.Entry<Integer, NodeK> entry: data.entrySet()){
//			Integer key = entry.getKey();
//			NodeK value = entry.getValue();
//		}
        for (Integer i : data.keySet()) {
            Double dis[] = new Double[k];
            int index = 0;
            for (int j = 0; j < k; j++) {
                dis[j] = getMks(i, key[j]);
            }
            Double in = dis[0];
            for (int j = 0; j < k; j++) {
                if (in > dis[j]) {
                    in = dis[j];
                    index = j;
                }
            }
            data.get(i).label = key[index];
        }
    }

    /* 根据中心点分簇 */
    static void Cluster() {
        for (Integer i : data.keySet()) {
            Double dis[] = new Double[k];
            int index = 0;
            for (int j = 0; j < k; j++) {
                dis[j] = getMks(i, key[j]);
            }
            Double in = dis[0];
            for (int j = 0; j < k; j++) {
                if (in > dis[j]) {
                    in = dis[j];
                    index = j;
                }
            }
            data.get(i).label2 = key[index];
        }
    }

    /* 数据对之间距离 */
    static Double getMks(int n, int m) {
        NodeK n1 = data.get(Integer.valueOf(n));
        NodeK n2 = keydata.get(Integer.valueOf(m));
        Double dis = 0.0;
        Double t1 = 1.0;
        Double t2 = 1.0;
        for (int i = 0; i < k; i++) {
            t1 *= (n1.attr1 - n2.attr1);
            t2 *= (n1.attr2 - n2.attr2);
        }
        dis = Math.pow((t1 + t2), 1.0 / k);
        return dis;
    }

    /*更新均值点*/
    static void updateKeyData() {
        Double acc1 = 0.0;
        Double acc2 = 0.0;
        for (int i = 0; i < k; i++) {
            for (Integer j : data.keySet()) {
                if (j == key[i]) {
                    acc1 += data.get(j).attr1;
                    acc2 += data.get(j).attr2;
                }
            }
            NodeK node = new NodeK(acc1 / k, acc2 / k, -1, -1);
            keydata.put(key[i], node);
        }
    }

    /*
     * 1.首先将所有对象随机分配到k个非空的簇中。
     * 2.计算每个簇的平均值，并用该平均值代表相应的簇。
     * 3.根据每个对象与各个簇中心的距离，分配给最近的簇。
     * 4.然后转第二步，重新计算每个簇的平均值。这个过程不断重复直到满足某个准则函数才停止。*/
    static void AllCluster() throws IOException {
        readData();
        initKey();
        initCluster();
        while (!still()) {
            for (Integer j : data.keySet()) {
                data.get(j).label = data.get(j).label2;
            }
			/*//辅助观察
			for (Integer f : data.keySet()) {
				System.out.print(data.get(f).label + " ");
			}
			System.out.println();*/

            //得到均值点，更新keydata
            updateKeyData();
            //更新簇
            Cluster();
        }
    }

    /* 判断簇分配有无变化，无true；有false */
    static Boolean still() {
        for (Integer i : data.keySet()) {
            if (data.get(i).label != data.get(i).label2)
                return false;
        }
        return true;
    }

    public static void main(String[] args) {
        k = 2;
        keydata = new HashMap();
        try {
            AllCluster();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < k; i++) {
            System.out.print("{");
            for (Integer j : data.keySet()) {
                if (data.get(j).label == key[i]) {
                    System.out.print(j + ",");
                }
            }
            System.out.println("}");
        }
    }
}
