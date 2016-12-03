class NodeK {
    final static int INVALID_LABEL = -1;
    // todo 命名太随意了
    Double attr1;
    Double attr2;
    int label;
    int label2;

    public NodeK(Double attr1, Double attr2, int label, int label2) {
        super();      // todo 干啥的?
        this.attr1 = attr1;
        this.attr2 = attr2;
        this.label = label;
        this.label2 = label2;
    }

    public String toString() {
        // todo 这么写也可以,但是不如用String.format,更好看
        // return String.format("(%s,%s,%s)", attr1, attr2, label);
        return "(" + attr1 + "," + attr2 + "," + label + ")";
    }


}