import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 科普一下method的前缀的语义:
 * getXXX() get的语义一般是幂等的,就是无数次get之后不会修改任何状态,get就只做'取'的动作;
 * setXXX() set的语义一般也是幂等的(对于不相关的属性),就是无数次set之后只有相关的属性变化;
 * initXXX() init一般意味着初始的时候调一次,做一些初始化的工作。
 * 其他还有一些前缀可以用,如:
 * buildXXX()
 * calculateXXX()
 * updateXXX()
 * 方法名都是驼峰命名,动词+(形容词)+名词。下面的AllCluster,Cluster都是不对的命名
 */


public class KMedoids {

    static int k; // k个簇
    // todo key这种变量名一般用作map的key,value,尽量不要在其他地方用这个变量名。感觉这里直接就是label
    static int[] key; // k个中心点的标号
    static int[] key2;
    // todo 一样的问题,成员变量也尽量不要使用实现类
    static HashMap<Integer, NodeK> data; // 装所有的数据

	/* 读取文件，将数据存储在data中 */
    // todo method的注释格式如下,这种格式的注释可以自动生成java-doc

    /**
     * 读取文件，将数据存储在data中
     *
     * @throws IOException
     */
    static void readData() throws IOException {
        data = FileUtils.readTrainData();
    }

    /* 初始化随机选择中心点 */
    static void initKey() {
        for (int i = 0; i < k; i++) {
            // todo 相比temp,一般更习惯写成tmp
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
            } else  // todo 即使只有一行,也需要把括号加上
                i--;
        }
        /*
		 * key[0]=4; key[1]=6;
		 */
        System.out.println(key[0] + "　" + key[1]);
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
            data.get(i).label = key[index];
        }
    }

    /**
     * @param key
     */
    static void Cluster(int key[]) {
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
    // todo 命名。。
    static Double getMks(int n, int m) {
        NodeK n1 = data.get(Integer.valueOf(n));
        NodeK n2 = data.get(Integer.valueOf(m));
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

    /* h替换i,在j处的代价 */
    static Double getEveryPay(int i, int h, int j) {
        // 四种情况比较代价
        // 第一种情况：Oj当前隶属于中心点对象Oi。如果Oi被Oh所代替作为中心点，且Oj离一个Om最近，i≠m，那么Oj被重新分配给Om
        // 第二种情况：Oj当前隶属于中心点对象Oi。如果Oi被Oh代替作为一个中心点，且Oj离Oh最近，那么Oj被重新分配给Oh。
        // 第三种情况：Oj当前隶属于中心点Om，m≠i。如果Oi被Oh代替作为一个中心点，而Oj依然离Om最近，那么对象的隶属不发生变化。
        // 第四种情况：Oj当前隶属于中心点Om，m≠i。如果Oi被Oh代替作为一个中心点，且Oj离Oh最近，那么Oi被重新分配给Oh。
        int temp = data.get(Integer.valueOf(j)).label2; // Om
        if (data.get(Integer.valueOf(i)).label == data.get(Integer.valueOf(j)).label) {
            if (data.get(Integer.valueOf(j)).label2 != data.get(Integer.valueOf(h)).label2)
                return getMks(temp, j) - getMks(i, j);
            else
                return getMks(h, j) - getMks(i, j);
        } else {
            if (data.get(Integer.valueOf(j)).label2 != data.get(Integer.valueOf(h)).label2)
                return 0.0;
            else {
                return getMks(h, j) - getMks(temp, j);
            }

        }

    }

    /* 得到所有非中心点替换后的代价,j替换i */
    static Double getPay(int i, int j) {
        Double acc = 0.0;
        for (Integer m : data.keySet()) {
            acc += getEveryPay(i, j, m);
        }
        return acc;
    }

    /*
     * 不断反复换中心点分簇 1.任意选择k个对象作为初始的簇中心点； 2.指派每个剩余的对象给离它最近的中心点所代表的簇；
     * 3.while(！簇分配不再变化) for each 中心点 for each 非中心点 非中心点替换中心点,得到代价
     * 得到代价List，如果有负数，找出其中最小的，替换中心点，更新中心点集、dis1
     */
    static void AllCluster() throws IOException {
        readData();
        initKey();
        Cluster();
        while (!still()) {
            for (int ii = 0; ii < k; ii++) {// 每个中心点
                HashMap<Integer, Double> pay = new HashMap();
                int i = key[ii];
                for (Integer j : data.keySet()) {// 每个非中心点

					/*//辅助观察
					for (Integer f : data.keySet()) {
						System.out.print(data.get(f).label + " ");
					}
					System.out.println();
					*/

                    // 假设替换
                    for (int m = 0; m < k; m++) {
                        if (m != i)
                            key2[m] = key[m];
                        else
                            key2[m] = j;
                    }
                    // 更新簇分类
                    Cluster(key2);
                    pay.put(j, getPay(i, j));
                }
                int index = 0;
                Double min = Double.MAX_VALUE;
                for (Integer l : pay.keySet()) {
                    if (min > pay.get(l)) {
                        min = pay.get(l);
                        index = l;
                    }
                }
                if (min < 0) {
                    key[ii] = index;
                    // Cluster();
                    for (Integer l : data.keySet())
                        data.get(l).label = data.get(l).label2;
                }
            }
        }
    }

    // todo 看注释的逻辑有点绕,"无变化->true","有变化->false",函数名应该起得更清晰一点
    // 这类做判断的函数,函数名前缀最好是"is"或"has",写成"isXXX"这种,更清晰。
	/* 判断簇分配有无变化，无true；有false */
    static Boolean still() {
        for (Integer i : data.keySet()) {
            // todo map的get还是有一定代价的,如果一次能get到,就不要get多次啦
            if (data.get(i).label != data.get(i).label2)
                return false;
        }
        return true;
    }

    /**
     * still的改写版,供参考 ^_^
     *
     * @return
     */
    static Boolean hasNoChange() {
        for (Map.Entry<Integer, NodeK> entry : data.entrySet()) {
            NodeK value = entry.getValue();
            if (value.label != value.label2) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        k = 2;
        key = new int[k];
        key2 = new int[k];
        try {
            AllCluster();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i = 0; i < k; i++) {
            System.out.print("{");
            for (Integer j : data.keySet()) {
                if (data.get(j).label == key2[i]) {
                    System.out.print(j + ",");
                }
            }
            System.out.println("}");
        }
    }
}
