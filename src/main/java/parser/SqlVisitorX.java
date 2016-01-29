package parser;

import java.util.List;

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
import org.apache.calcite.sql.util.SqlVisitor;

public class SqlVisitorX<R> implements SqlVisitor<R>  {
    
    
    
    
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
		System.out.println("sqlDnamic");
		return null;
	}
	
	@Override
	public R visit(SqlIdentifier id) {
		System.out.print( id.names);
		return null;
	}
	
	public R visit(SqlBasicCall id) {
		
//		System.out.println("wololo");
		return null;
	}
	
	@Override
	public R visit(SqlNodeList nodeList) {
//		System.out.println("hello");
		for(SqlNode n : nodeList.getList()){
			n.accept(this);
		}
		// TODO Auto-generated method stub
		if(nodeList instanceof SqlNode){
//			System.out.println("hello");
		}
		return null;
	}
	
	
	
	@Override
	public R visit(SqlCall call) {
		// TODO Auto-generated method stub
		SqlOperator op = call.getOperator();
		
		if (call instanceof SqlBasicCall){

			
			SqlOperator op1 =((SqlBasicCall) call).getOperator();
			SqlNode[] operands = ((SqlBasicCall) call).getOperands();
			if(operands[0] instanceof SqlIdentifier){
				operands[0].accept(this);
				System.out.print(" " + op1.getName() + " ");
				operands[1].accept(this);
			}
			else{System.out.print(" " + op1.getName() + " ");
			for (int i=operands.length-1; i>=0;i--) operands[i].accept(this);
			}
			System.out.print(" ");
		}
		
		if(call instanceof SqlSelect){
			
			 SqlNodeList selectList= ((SqlSelect) call).getSelectList();
			 selectList.accept(this);
			 System.out.println();
			 SqlNode node =((SqlSelect) call).getFrom();
			 node.accept(this);
			 System.out.println();
			 SqlNode where=((SqlSelect) call).getWhere();
			 SqlNodeList slqNodeList=((SqlSelect) call).getWindowList();
			 SqlOperator oper=((SqlBasicCall) where).getOperator();
			 System.out.print(" "+ oper.getName()+ " ");
			 SqlNode[] nodeList =  ((SqlBasicCall) where).getOperands();
			 List<SqlNode> listNode =  ( (SqlBasicCall) where).getOperandList();

			 SqlOperator op1 =((SqlBasicCall) where).getOperator();
//			 System.out.println(nodeList[0].toString()+ " | " + nodeList[1].toString());
			 for(int i=nodeList.length-1; i>=0;i--) {((SqlBasicCall) nodeList[i]).accept(this);}
			
			 

		}
		if(call instanceof SqlJoin){
			SqlNode nodeL =((SqlJoin) call).getLeft();
			if(nodeL!=null){nodeL.accept(this);}
			SqlNode nodeR = ((SqlJoin) call).getRight();
			if(nodeR!=null){nodeR.accept(this);}
			

		}
		 

		
		
		return null;
	}
	
	@Override
	public R visit(SqlLiteral literal) {
		System.out.println("sqlLiteral");
		// TODO Auto-generated method stub
		return null;
	}
	   
		
}
