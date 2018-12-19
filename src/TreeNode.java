import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNode extends DefaultMutableTreeNode {
	private String nodeKind;
	private String content;
	private int lineNum;

	public TreeNode() {
		super();
		nodeKind = "";
		content = "";
	}

	public TreeNode(String content) {
		super(content);
		this.content = content;
		nodeKind = "";
	}

	public TreeNode(String kind, String content) {
		super(content);
		this.content = content;
		nodeKind = kind;
	}

	public TreeNode(String kind, String content,int lineNum) {
		super(content);
		this.content = content;
		this.lineNum = lineNum;
		nodeKind = kind;
	}

	public String getNodeKind() {
		return nodeKind;
	}

	public void setNodeKind(String nodeKind) {
		this.nodeKind = nodeKind;
	}

	public int getLineNum() {
		return lineNum;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		setUserObject(content);
	}


	public void add(TreeNode childNode) {
		super.add(childNode);
	}

	public TreeNode getChildAt(int index) {
		return (TreeNode) super.getChildAt(index);
	}
}
