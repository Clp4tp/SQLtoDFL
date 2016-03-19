package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.apache.calcite.sql.SqlBasicCall;
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
import org.apache.calcite.sql.util.SqlBasicVisitor.ArgHandler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class JoinAttributesVisitor<R> implements SqlVisitor<R> {
    private static final List<String> Operators = new ArrayList<String>(
            Arrays.asList(">=", "=", "<="));
    public List<String[]>             identifiers          = new ArrayList<>();
    Multimap<String, String>          aliasMap             = HashMultimap.create();
    Multimap<String, String>          functionsMap         = HashMultimap.create();
    private Stack<String>             lastVisited          = new Stack<>();
    // private SqlSimpleNode node;
    private static final List<String> JoinOperators = new ArrayList<String>(
            Arrays.asList("AND", "OR", "LIKE"));
    SqlBasicVisitor.ArgHandler<R>     argHandler           = SqlBasicVisitor.ArgHandlerImpl.instance();
    List<SqlBasicCall> joinOperations = new ArrayList<SqlBasicCall>();
    List<String> xlusiveOperator = new ArrayList<>();
    
   
    
    Stack<OperatorOnSubtree> operatorAndSubtree =  new Stack<>();
    
    
    @Override
    public R visit(SqlCall call) {
	SqlOperator op = call.getOperator();
	if(JoinOperators.contains(op.toString())) xlusiveOperator.add(op.toString());

	
	if(call.getOperandList().size()==2 && JoinOperators.contains(op.toString())){
		for(String c : JoinOperators){
			if(call.getOperandList().get(0).toString().contains(c) && !call.getOperandList().get(1).toString().contains(c)){
				operatorAndSubtree.add(new OperatorOnSubtree(call.getOperandList().get(1), op));
				break;
			}else {
				operatorAndSubtree.add(new OperatorOnSubtree(call.getOperandList().get(1), op));
				operatorAndSubtree.add(new OperatorOnSubtree(call.getOperandList().get(0), op));
				break;
			}
			
		}
			
	}
	List<SqlNode> l = call.getOperandList();
	if (call instanceof SqlBasicCall) {
	    if (Operators.contains(op.toString())) {
		joinOperations.add((SqlBasicCall) call);	
	    }
	}
	op.acceptCall(this, call);
	return null;
    }

    @Override
    public R visit(SqlLiteral literal) {
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
            identifiers.add(new String[] { id.getSimple() });
            lastVisited.push(id.getSimple());
        } else if (id.isStar()) {
            identifiers.add(new String[] { "*" });
            lastVisited.push("*");
        } else {
            identifiers.add(new String[] { id.names.get(0), id.names.get(1) });
            lastVisited.push(id.toString());
        }
        return (R) id;
    }

    @Override
    public R visit(SqlDataTypeSpec type) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public R visit(SqlDynamicParam param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public R visit(SqlIntervalQualifier intervalQualifier) {
        // TODO Auto-generated method stub
        return null;
    }

    
}
