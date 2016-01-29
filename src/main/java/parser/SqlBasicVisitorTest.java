package parser;

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

public class SqlBasicVisitorTest<R> implements SqlVisitor<R> {
    private static final char           COMMA      = ',';
    SqlStatementHelper                  helper;
    SqlBasicVisitor.ArgHandler<SqlNode> argHandler = SqlBasicVisitor.ArgHandlerImpl.instance();

    @Override
    public R visit(SqlLiteral literal) {
        if (!literal.toValue().equals("NONE") && !literal.toValue().equals("false") && !literal.toValue().equals("true")
                && !literal.toValue().equals("JOIN")) {
            if (literal.toString().equals("COMMA"))
                System.out.print(COMMA);
            else
                System.out.println("-" + literal.toSqlString(null));
        }
        return null;
    }

    public R visit(SqlBasicCall call) {
        R result = null;
        SqlOperator op = call.getOperator();
        System.out.println("SQLBASICCALL_ " + op.getName() + "-" + op.getKind().toString());
        return op.acceptCall(this, call);
    }

    public R visit(SqlJoin call) {
        R result = null;
        SqlOperator op = call.getOperator();
        System.out.println("JOIN_ " + op.getName() + "-" + op.getKind().toString());
        return op.acceptCall(this, call);
    }

    public R visit(SqlSelect call) {
        R result = null;
        SqlOperator op = call.getOperator();
        System.out.println("JOIN_ " + op.getName() + "-" + op.getKind().toString());
        return op.acceptCall(this, call);
    }

    @Override
    public R visit(SqlCall call) {
        SqlOperator op = call.getOperator();
        
        if (helper == null) { // this means we are at the first pass
            String type = op.getName();
            helper = new SqlStatementHelper();
            helper.setType(type);
        }

        if (!op.getKind().toString().equals("JOIN"))
            System.out.println(op.getName() + "-" + op.getKind().toString());
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
        System.out.print(" " + id.names);
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
