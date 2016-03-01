package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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
import org.apache.calcite.sql.SqlSelectOperator;
import org.apache.calcite.sql.util.SqlBasicVisitor;
import org.apache.calcite.sql.util.SqlVisitor;
import org.apache.calcite.sql.util.SqlBasicVisitor.ArgHandler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SqlTreeReverseVisitor<R> implements SqlVisitor<R> {
    private static final List<String> InterestingOperators = new ArrayList<String>(
	    Arrays.asList("count", "avg", "min", "max", "sum"));
    public List<String[]> identifiers = new ArrayList<>();
    Multimap<String, String> aliasMap = HashMultimap.create();
    Multimap<String, String> functionsMap = HashMultimap.create();
    private Stack<String> lastVisited = new Stack<>();
    SqlBasicVisitor.ArgHandler<R> argHandler = SqlBasicVisitor.ArgHandlerImpl.instance();
    Multimap<String, String> tables = HashMultimap.create();
    Multimap<String, Multimap<String, String>> functionsToTables = HashMultimap.create();

    public void setTables(Multimap<String, String> tables) {
	this.tables = tables;
    }

    public Multimap<String, Multimap<String, String>> getfunctionsToTables() {
	return this.functionsToTables;
    }

    public void setIdentifiers(List<String[]> identifiers) {
	this.identifiers = identifiers;
    }

    public SqlTreeReverseVisitor() {
    }

    @Override
    public R visit(SqlCall call) {
	SqlOperator op = call.getOperator();
	op.acceptCall(this, call);
	if (op.getName().equals("AS"))
	    aliasMap.put(call.getOperandList().get(0).toString().replace("`", ""),
		    call.getOperandList().get(1).toString());
	if (call instanceof SqlBasicCall) {
	    Multimap<String, String> map = HashMultimap.create();
	    String opString = ((SqlBasicCall) call).getOperator().toString();
	    if (InterestingOperators.contains(opString.toLowerCase())) {
		String table = lastVisited.pop();
		if (table.equals("*")) {
		    map.put(opString, call.getOperandList().get(0).toString().replace("`", ""));
		    functionsToTables.put(table, map);
		} else {
		    map.put(opString, call.getOperandList().get(0).toString().replace("`", ""));
		    functionsToTables.put(table, map);
		}
	    }
	}
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
	if (id.isSimple()) {
	    SqlIdentifier lastId = id.skipLast(1);
	    if (!InterestingOperators.contains(id.getSimple()))
		lastVisited.push(id.getSimple());
	} else if (id.isStar()) {
	    lastVisited.push("*");
	} else {
	    for (String name : id.names) {
		if (!tables.get(name).isEmpty()) {
		    lastVisited.push(name);
		}
	    }
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
	return null;
    }

    @Override
    public R visit(SqlIntervalQualifier intervalQualifier) {
	return null;
    }

    @Override
    public R visit(SqlLiteral literal) {
	return null;
    }

}
