package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.util.SqlBasicVisitor;
import org.apache.calcite.sql.util.SqlVisitor;
import org.apache.calcite.sql.util.SqlBasicVisitor.ArgHandler;

public class MySqlVisitorImpl<R> implements SqlVisitor<R>, ArgHandler<R> {

	public List<String[]> identifiers = new ArrayList<>();

	private SqlSimpleNode node;
	SqlBasicVisitor.ArgHandler<R> argHandler = SqlBasicVisitor.ArgHandlerImpl.instance();
	private final SqlSimpleNode root;
	SqlSimpleNode presentNode;
	// List<SqlQueryMeta> qMeta;

	public MySqlVisitorImpl() {
		root = new SqlSimpleNode();
		presentNode = root;
		// qMeta = new ArrayList<>();

	}

	@Override
	// 1rst call
	public R visit(SqlCall call) {
		SqlOperator op = call.getOperator();

		// SqlNode from =((SqlSelect) call).getFrom();
		presentNode.setOperator(op);
		System.out.println(call.toString());
		boolean t = op.isDeterministic();
		t = op.isAggregator();
		t = op.isDynamicFunction();
		List<SqlNode> l = call.getOperandList();
		if (!l.isEmpty()) {
			SqlSimpleNode leftNode = new SqlSimpleNode();

			// presentNode.setLeftNode();
		}
		System.out.println(op.getName());

		return presentNode.getOperator().acceptCall(this, call);

		// return null;
	}

	@Override
	public R visit(SqlLiteral literal) {

		System.out.println("literal" + literal.toString());
		return null;
	}

	@Override
	public R visit(SqlNodeList nodeList) {
		R result = null;
		for (int i = 0; i < nodeList.size(); i++) {
			SqlNode node = nodeList.get(i);
			result = node.accept(this);
		}
		return result;
	}

	@Override
	public R visit(SqlIdentifier id) {
		// TODO Auto-generated method stub
		if (id.isSimple()) {
			System.out.println("Simple " + id.getSimple());
			identifiers.add(new String[] { id.getSimple() });
			
		} else {
			System.out.println(id.names);
			identifiers.add(new String[] { id.names.get(0), id.names.get(1) });
			
		}
		return (R) id;
	}

	@Override
	public R visit(SqlDataTypeSpec type) {
		// TODO Auto-generated method stub
		System.out.println("dadw");
		return null;
	}

	@Override
	public R visit(SqlDynamicParam param) {
		// TODO Auto-generated method stub
		System.out.println("dadaw");
		return null;
	}

	@Override
	public R visit(SqlIntervalQualifier intervalQualifier) {
		// TODO Auto-generated method stub
		System.out.println("dadawdawdaw");
		return null;
	}

	// ?
	@Override
	public R result() {
		// TODO Auto-generated method stub
		System.out.println("Handler result");
		return null;
	}

	@Override
	public R visitChild(SqlVisitor visitor, SqlNode expr, int i, SqlNode operand) {
		// TODO Auto-generated method stub
		System.out.println("handler visit Child");
		return null;
	}

}
