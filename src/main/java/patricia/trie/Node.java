package patricia.trie;

public class Node {

	private int bitIndex;
	private Node parent;
	private Node left;
	private Node right;
	private String key;

	public Node(int bitIndex, Node parent, Node left, Node right, String key) {
		this.bitIndex = bitIndex;
		this.parent = parent;
		this.left = left;
		this.right = right;
		this.key = key;
	}

	public Node(int bitIndex, Node parent, String key) {
		this.parent = parent;
		this.bitIndex = bitIndex;
		this.key = key;
	}

	public Node(int bitIndex, String key) {
		this.bitIndex = bitIndex;
		this.key = key;
	}

	public Node(String key) {
		this.key = key;
	}

	public Node() {

	}

	public int getBitIndex() {
		return bitIndex;
	}

	public void setBitIndex(int bitIndex) {
		this.bitIndex = bitIndex;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void copy(Node node) {

		bitIndex = node.getBitIndex();
		key = node.getKey();
		parent = node.getParent();
		left = node.getLeft();
		right = node.getRight();
	}

	/**
	 * Contains parent,current,left and right nodes.
	 * 
	 * @param node
	 * @return
	 */
	public Node[] getNodes() {

		Node[] nodes = new Node[4];
		if ( parent != null ) {
			nodes[0] = new Node( parent.getBitIndex(), parent.getKey() );
		}
		else {
			nodes[0] = null;
		}

		nodes[1] = new Node( bitIndex, key );

		if ( left != null ) {
			nodes[2] = new Node( left.getBitIndex(), left.getKey() );
		}
		else {
			nodes[2] = null;
		}

		if ( right != null ) {
			nodes[3] = new Node( right.getBitIndex(), right.getKey() );
		}
		else {
			nodes[3] = null;
		}

		return nodes;
	}

	@Override
	public boolean equals(Object obj) {

		if ( obj instanceof Node ) {
			Node n = (Node) obj;

			if ( ( bitIndex == n.getBitIndex() ) && ( key.equals( n.getKey() ) ) ) {
				return true;
			}

			return false;
		}

		return false;
	}

	@Override
	public String toString() {
		return "{bitIndex=" + bitIndex + ", key=" + key + "}";
	}
}
