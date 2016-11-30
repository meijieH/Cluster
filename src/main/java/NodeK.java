

class NodeK {
	Double attr1;
	Double attr2;
	int label;
	int label2;
	public NodeK(Double attr1, Double attr2, int label, int label2) {
		super();
		this.attr1 = attr1;
		this.attr2 = attr2;
		this.label = label;
		this.label2 = label2;
	}
	public String toString(){
		return "("+attr1+","+attr2+","+label+")";
	}

	

}