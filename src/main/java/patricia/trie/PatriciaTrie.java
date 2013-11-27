package patricia.trie;

import java.util.LinkedList;
import java.util.List;

public class PatriciaTrie {

	private Node head;

	public PatriciaTrie(String searchKey) {
		initializeHead(searchKey);
	}

	public PatriciaTrie() {

	}

	public Node[] insert(String searchKey) {

		if (isNullOrEmpty(searchKey)) {
			return null;
		}

		if (initializeHead(searchKey)) {
			return head.getNodes();
		}

		Node[] nodes = new Node[4];
		doInsert(head, searchKey, nodes);

		return nodes;
	}

	private boolean initializeHead(String searchKey) {
		if (head == null) {
			head = new Node(getLeftMostOneBit(searchKey), null, null, null,
					searchKey);
			head.setRight(head);

			return true;
		}

		return false;
	}

	private void doInsert(Node node, String searchKey, Node[] nodes) {

		if (node != null) {
			// search left
			char bit = getBitAt(node.getBitIndex(), searchKey);
			if (bit == '0') {

				if (node.getLeft() == null) {
					Node n = createNewNode(node, searchKey);
					n.setParent(node);
					node.setLeft(n);

					nodes[0] = n.getNodes()[0];
					nodes[1] = n.getNodes()[1];
					nodes[2] = n.getNodes()[2];
					nodes[3] = n.getNodes()[3];
				} else if (node.getBitIndex() >= node.getLeft().getBitIndex()
						|| (node.getLeft().getBitIndex() > getDiffAtFrom(node
								.getLeft().getKey(), searchKey,
								toBytes(searchKey).length() - 1))) {
					// find upward pointer
					Node newNode = createNewNode(node.getLeft(), searchKey);

					if (node.getBitIndex() > newNode.getBitIndex()) {
						// new node comes above node
						newNode.setParent(node.getParent());
						node.setParent(newNode);
						updateLeftOrRightPointerOnCurrentNode(newNode,
								node.getLeft(), searchKey);
					} else if (node.getBitIndex() < newNode.getBitIndex()) {
						// new node comes below node
						newNode.setParent(node);
						node.setLeft(newNode);
					}

					nodes[0] = newNode.getNodes()[0];
					nodes[1] = newNode.getNodes()[1];
					nodes[2] = newNode.getNodes()[2];
					nodes[3] = newNode.getNodes()[3];
				} else {
					doInsert(node.getLeft(), searchKey, nodes);
				}
			}
			// search right
			else if (bit == '1') {

				if (node.getRight() == null) {
					Node n = createNewNode(node, searchKey);
					n.setParent(node);
					node.setRight(n);

					nodes[0] = n.getNodes()[0];
					nodes[1] = n.getNodes()[1];
					nodes[2] = n.getNodes()[2];
					nodes[3] = n.getNodes()[3];
				} else if (node.getBitIndex() >= node.getRight().getBitIndex()
						|| (node.getRight().getBitIndex() > getDiffAtFrom(node
								.getRight().getKey(), searchKey,
								toBytes(searchKey).length() - 1))) {
					// find upward pointer
					Node newNode = createNewNode(node.getRight(), searchKey);

					if (node.getBitIndex() > newNode.getBitIndex()) {
						// new node comes above node
						newNode.setParent(node.getParent());
						node.setParent(newNode);
						updateLeftOrRightPointerOnCurrentNode(newNode,
								node.getRight(), searchKey);
					} else if (node.getBitIndex() < newNode.getBitIndex()) {
						// new node comes below node
						node.setRight(newNode);
						newNode.setParent(node);
					}

					nodes[0] = newNode.getNodes()[0];
					nodes[1] = newNode.getNodes()[1];
					nodes[2] = newNode.getNodes()[2];
					nodes[3] = newNode.getNodes()[3];
				} else {
					doInsert(node.getRight(), searchKey, nodes);
				}
			}
		}
	}

	private Node initializeNode(Node parent, Node left, Node right,
			int bitIndex, String searchKey) {

		return new Node(bitIndex, parent, left, right, searchKey);
	}

	private void updateLeftOrRightPointerOnCurrentNode(Node newNode, Node node,
			String searchKey) {

		if (getBitAt(node.getBitIndex(), searchKey) == '0') {
			newNode.setLeft(node);
		} else if (getBitAt(node.getBitIndex(), searchKey) == '1') {
			newNode.setRight(node);
		}
	}

	/**
	 * parent,left and right may need to update after this method call. This
	 * method doesn't set the pointers except self pointer since they are set
	 * differently depending on different conditions.
	 * 
	 * @param node
	 * @param searchKey
	 * @return
	 */
	private Node createNewNode(Node node, String searchKey) {

		int diffIndex = getDiffAtFrom(node.getKey(), searchKey,
				toBytes(searchKey).length() - 1);
		Node n = initializeNode(null, null, null, diffIndex, searchKey);
		setPointToItself(n, node, diffIndex, searchKey);

		return n;
	}

	private void setPointToItself(Node node, Node parent, int diffIndex,
			String searchKey) {

		if (getBitAt(diffIndex, searchKey) == '0') {
			node.setLeft(node);

			if (getBitAt(diffIndex, parent.getKey()) == '1') {
				node.setRight(parent);
			}
		} else {
			node.setRight(node);

			if (getBitAt(diffIndex, parent.getKey()) == '0') {
				node.setLeft(parent);
			}
		}
	}

	public final int getLeftMostOneBit(String str) {

		char[] binary = toBytes(str).toCharArray();
		int index = 0;
		for (char c : binary) {
			if (c == '1') {
				break;
			}

			index++;
		}

		return index;
	}

	/**
	 * TODO need to be reviewed later.
	 * 
	 * @param str
	 */
	public final String toBytes(String str) {

		byte[] bytes = str.getBytes();
		StringBuilder binary = new StringBuilder();
		for (byte b : bytes) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				binary.append((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
		}

		return binary.toString();
	}

	public final char getBitAt(int bitIndex, String str) {

		String binary = toBytes(str);

		// log.debug( "str: " + str + " binary expression: " + binary );

		if ((bitIndex >= 0) && (bitIndex < binary.length())) {
			return binary.toCharArray()[bitIndex];
		}

		// log.warn( "bitIndex," + bitIndex + " is out of range, " +
		// binary.length()
		// + ". about to prepend zeros and return the corresponding bit," +
		// bitIndex );

		return prependGaps(binary.toCharArray(), getGapsAsZeroBits(bitIndex
				- binary.toCharArray().length))[bitIndex - 1];
	}

	/**
	 * returning -1 means both of the parameters are the same. the parameter,
	 * from is inclusive.
	 * 
	 * @param nodeKey
	 *            Key within each node.
	 * @param searchKey
	 *            Key to be searched.
	 * @param from
	 *            Index to start.
	 * @return
	 */
	public final int getDiffAtFrom(String nodeKey, String searchKey, int from) {

		char[] nodeKeyArray = toBytes(nodeKey).toCharArray();
		char[] searchKeyArray = toBytes(searchKey).toCharArray();

		if (searchKeyArray.length > nodeKeyArray.length) {
			nodeKeyArray = prependGaps(nodeKeyArray,
					getGapsAsZeroBits(searchKeyArray.length
							- nodeKeyArray.length));
		} else if (searchKeyArray.length < nodeKeyArray.length) {
			searchKeyArray = prependGaps(searchKeyArray,
					getGapsAsZeroBits(nodeKeyArray.length
							- searchKeyArray.length));
		}

		int length = Math.min(nodeKeyArray.length, searchKeyArray.length);
		int index = 0;
		int start = 0;
		for (int i = start; i < length; i++) {
			if (nodeKeyArray[i] != searchKeyArray[i]) {
				break;
			}

			index++;
		}

		return index;
	}

	public final char[] getGapsAsZeroBits(int gap) {

		char[] gaps = new char[gap];
		for (int i = 0; i < gaps.length; i++) {
			gaps[i] = '0';
		}

		return gaps;
	}

	public final char[] prependGaps(char[] original, char[] gaps) {

		char[] res = new char[original.length + gaps.length];
		int index = 0;

		for (index = 0; index < original.length; index++) {
			res[index] = original[index];
		}
		for (int i = 0; i < gaps.length; i++) {
			res[index++] = gaps[i];
		}

		return res;
	}

	public final boolean search(String searchKey) {

		if (isNullOrEmpty(searchKey)) {
			return false;
		}

		Node[] nodes = new Node[4];
		doSearch(head, searchKey, nodes);

		if (nodes[1] == null) {
			return false;
		}

		// should not happen. If it does, that means that Patricia.insert() has
		// some bug.
		if (!nodes[1].getKey().equals(searchKey)) {
			return false;
		}

		return true;
	}

	private void doSearch(Node node, String searchKey, Node[] nodes) {

		if (node != null) {
			if (getBitAt(node.getBitIndex(), searchKey) == '0') {

				if (node.getBitIndex() >= node.getLeft().getBitIndex()) {
					if (node.getLeft().getKey().equals(searchKey)) {
						nodes[0] = node.getLeft().getNodes()[0];
						nodes[1] = node.getLeft().getNodes()[1];
						nodes[2] = node.getLeft().getNodes()[2];
						nodes[3] = node.getLeft().getNodes()[3];
					}

					return;
				}

				doSearch(node.getLeft(), searchKey, nodes);
			} else if (getBitAt(node.getBitIndex(), searchKey) == '1') {

				if (node.getBitIndex() >= node.getRight().getBitIndex()) {
					if (node.getRight().getKey().equals(searchKey)) {
						nodes[0] = node.getRight().getNodes()[0];
						nodes[1] = node.getRight().getNodes()[1];
						nodes[2] = node.getRight().getNodes()[2];
						nodes[3] = node.getRight().getNodes()[3];
					}

					return;
				}

				doSearch(node.getRight(), searchKey, nodes);
			}
		}
	}

	public final List<String> searchPrefix(String prefix) {

		if (isNullOrEmpty(prefix)) {
			throw new IllegalArgumentException(
					"invalid argument specified. prefix," + prefix);
		}

		Node res = new Node();
		// res holds where to search prefix if it
		try {
			doSearch(head, prefix, res);
		} catch (NullPointerException ex) {
			System.err
					.println("patricia trie search encountered some issue for '"
							+ prefix + "'");
		}

		List<String> results = new LinkedList<String>();

		if (res.getKey() == null) {
			return results;
		}

		try {
			doSearchPrefix(res, prefix, results);
		} catch (NullPointerException ex) {
			System.err
					.println("patricia trie search encountered some issue for '"
							+ prefix + "'");
		}
		return results;
	}

	private void doSearchPrefix(Node node, String prefix, List<String> results) {

		if (node != null) {

			if ((node.getLeft() != null)
					&& (node.getBitIndex() < node.getLeft().getBitIndex())) {
				doSearchPrefix(node.getLeft(), prefix, results);
			}

			if (node.getKey().startsWith(prefix)) {
				results.add(node.getKey());
			}

			if ((node.getRight() != null)
					&& (node.getBitIndex() < node.getRight().getBitIndex())) {
				doSearchPrefix(node.getRight(), prefix, results);
			}
		}
	}

	private void doSearch(Node node, String prefix, Node res) {

		if (node != null) {
			if (getBitAt(node.getBitIndex(), prefix) == '0') {

				if (node.getLeft() != null
						&& (node.getLeft().getKey().startsWith(prefix))) {

					if (res.getKey() == null) {
						res.copy(node.getLeft());

						return;
					}
				}

				if (node.getBitIndex() >= node.getLeft().getBitIndex()) {
					return;
				}

				doSearch(node.getLeft(), prefix, res);
			} else if (getBitAt(node.getBitIndex(), prefix) == '1') {

				if (node.getRight().getKey().startsWith(prefix)) {
					if (res.getKey() == null) {
						res.copy(node.getRight());
					}
				}

				if (node.getBitIndex() >= node.getRight().getBitIndex()) {
					return;
				}

				doSearch(node.getRight(), prefix, res);
			}
		}
	}

	private boolean isNullOrEmpty(String str) {

		if (str == null) {
			return true;
		}

		if (str.equals("")) {
			return true;
		}

		return false;
	}

	public void printInOrder() {
		doPrintInOrder(head);
	}

	private void doPrintInOrder(Node node) {

		if (node != null) {
			if ((node.getLeft() != null)
					&& (node.getBitIndex() < node.getLeft().getBitIndex())) {
				doPrintInOrder(node.getLeft());
			}

			System.out.println("bit index: " + node.getBitIndex() + " key: "
					+ node.getKey());

			if ((node.getRight() != null)
					&& (node.getBitIndex() < node.getRight().getBitIndex())) {
				doPrintInOrder(node.getRight());
			}
		}
	}
}
