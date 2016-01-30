package parser;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlOperator;

public class SqlSimpleNode {

	// We might need this to be a generic
	private SqlOperator operator;
	private String keyword;
	private String operation;
	private SqlSimpleNode rightNode;
	private SqlSimpleNode leftNode;
	private String meta;
	private boolean isLeaf;

	public SqlSimpleNode(SqlOperator op, SqlSimpleNode r, SqlSimpleNode l) {
		this.operator = op;
		this.rightNode = r;
		this.leftNode = l;
	}

	public SqlSimpleNode() {
		leftNode = null;
		rightNode = null;
		isLeaf = false;
	};

	public boolean hasLeftNode() {
		return (leftNode != null) ? true : false;
	}

	public boolean hasRightNode() {
		return (rightNode != null) ? true : false;
	}

	public SqlOperator getOperator() {
		return operator;
	}

	public void setOperator(SqlOperator operator) {
		this.operator = operator;
		this.setMeta(operator.getName());
	}

	public SqlSimpleNode getRightNode() {
		return rightNode;
	}

	public void setRightNode(SqlSimpleNode rightNode) {
		this.rightNode = rightNode;
	}

	public SqlSimpleNode getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(SqlSimpleNode leftNode) {
		this.leftNode = leftNode;
	}

	public String getMeta() {
		return meta;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

	// public boolean isComplex() {
	// return isComplex;
	// }
	//
	//
	// public void setComplex(boolean isComplex) {
	// this.isComplex = isComplex;
	// }
	//
	//
	// public boolean isRoot() {
	// return isRoot;
	// }
	//
	//
	// public void setRoot(boolean isRoot) {
	// this.isRoot = isRoot;
	// }

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

}
