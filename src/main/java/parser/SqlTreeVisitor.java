package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

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
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SqlTreeVisitor<R> implements SqlVisitor<R> {
    private static final List<String> InterestingOperators = new ArrayList<String>(
            Arrays.asList("count", "avg", "min", "max", "sum"));
//    private static Logger log = LoggerFactory.getLogger(SqlTreeVisitor.class);
    public List<String[]>             identifiers          = new ArrayList<>();
    Multimap<String, String>          aliasMap             = HashMultimap.create();
    Multimap<String, String>          functionsMap         = HashMultimap.create();
    private Stack<String>             lastVisited          = new Stack<>();
    // private SqlSimpleNode node;
    SqlBasicVisitor.ArgHandler<R>     argHandler           = SqlBasicVisitor.ArgHandlerImpl.instance();

    @Override
    public R visit(SqlCall call) {
        SqlOperator op = call.getOperator();
        //System.out.println(call.toString());
        if (op.getName().equals("AS"))
            aliasMap.put(call.getOperandList().get(1).toString(), call.getOperandList().get(0).toString());
        if (call instanceof SqlBasicCall) {
            String opString = ((SqlBasicCall) call).getOperator().toString();
            if (InterestingOperators.contains(opString.toLowerCase())) {
                // functionsMap.put(op, value)
                functionsMap.put(opString, call.getOperandList().get(0).toString());
            }
        }
         return op.acceptCall(this, call);
    }

    @Override
    public R visit(SqlLiteral literal) {
	System.out.println("dawwdawdawd");
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
