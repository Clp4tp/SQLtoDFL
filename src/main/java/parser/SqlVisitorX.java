package parser;

import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.util.SqlVisitor;

public class SqlVisitorX<R> implements SqlVisitor<R> {

	@Override
	public R visit(SqlIntervalQualifier intervalQualifier) {
		// TODO Auto-generated method stub
		
		return null;
	}
	
	@Override
	public R visit(SqlDynamicParam param) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public R visit(SqlDataTypeSpec type) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public R visit(SqlIdentifier id) {
		System.out.println("Table "+ id.names.get(0));
		return null;
	}
	
	@Override
	public R visit(SqlNodeList nodeList) {
		// TODO Auto-generated method stub
		if(nodeList instanceof SqlNode){
			System.out.println("hello");
		}
		return null;
	}
	
	@Override
	public R visit(SqlCall call) {
		// TODO Auto-generated method stub
		if(call instanceof SqlSelect){
			
			
			 SqlNode node =((SqlSelect) call).getFrom();
			 node.accept(this);
			System.out.println("SqlSelect");
		}
		if(call instanceof SqlJoin){
			SqlNode nodeL =((SqlJoin) call).getLeft();
			if(nodeL!=null){nodeL.accept(this);}
			SqlNode nodeR = ((SqlJoin) call).getRight();
			if(nodeR!=null){nodeR.accept(this);}
			
//			System.out.println("SqlJoin");
		}
		
		return null;
	}
	
	@Override
	public R visit(SqlLiteral literal) {
		// TODO Auto-generated method stub
		return null;
	}
	   
		
}
