package parser;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlOperator;

public class OperatorOnSubtree {
	SqlOperator op;
	SqlNode node;
	
	JoinCondition cond;
	
	public OperatorOnSubtree(SqlNode node, SqlOperator op) {
		this.op = op;
		this.node = node;
		cond= new JoinCondition( (SqlBasicCall) node);
	}
	
	@Override
	public String toString(){
		return new String(cond.toString()  + (op!=null ? " "+ op.toString() : ""));
	}
	
	public String toString(int i){
		return new String(cond.toString() );
	}
}