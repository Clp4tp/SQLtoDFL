package parser;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.util.SqlBasicVisitor;
import org.apache.calcite.sql.util.SqlVisitor;

public class SqlBasicVisitorTest<R> implements SqlVisitor<R> {

	@Override
	public R visit(SqlLiteral literal) {
		// TODO Auto-generated method stub
		System.out.print(' ');;
		return null;
	}

	@Override
	public R visit(SqlCall call) {
		SqlOperator op =call.getOperator();
		System.out.println(op.getName());
		return op.acceptCall(this, call);
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
		System.out.print(" "+ id.names);
		return null;
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

}
