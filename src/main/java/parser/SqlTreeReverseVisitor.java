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

public class SqlTreeReverseVisitor<R> implements SqlVisitor<R>, ArgHandler<R> {
	private static final List<String> InterestingOperators = new ArrayList<String>(
			Arrays.asList("count", "avg", "min", "max", "sum"));
	public List<String[]> identifiers = new ArrayList<>();
	Multimap<String, String> aliasMap = HashMultimap.create();
	Multimap<String, String> functionsMap = HashMultimap.create();
	private Stack<String> lastVisited = new Stack<>();
	SqlBasicVisitor.ArgHandler<R> argHandler = SqlBasicVisitor.ArgHandlerImpl.instance();
//	Multimap<String,String> w = HashMultimap.create();
	Multimap<String, String> tables = HashMultimap.create();
	Multimap<String, Multimap<String,String>> functionsToTables = HashMultimap.create();
	
//	Collect funstionsList = new ArrayList<>(); 
	
	public void setTables(Multimap<String, String> tables) {
		this.tables = tables;
	}
	public Multimap<String, Multimap<String, String>> getfunctionsToTables(){
		return this.functionsToTables;
	}

	public void setIdentifiers(List<String[]> identifiers) {
		this.identifiers = identifiers;
	}

	public SqlTreeReverseVisitor() {
		// root = new SqlSimpleNode();
		// presentNode = root;
		// qMeta = new ArrayList<>();

	}

	@Override
	public R visit(SqlCall call) {
		SqlOperator op =  call.getOperator();
//		(SqlSelectOperator)op.
		// SqlNode from =((SqlSelect) call).getFrom();
		// presentNode.setOperator(op);
		System.out.println(call.toString());
		boolean t = op.isDeterministic();
		t = op.isAggregator();
		t = op.isDynamicFunction();
		List<SqlNode> l = call.getOperandList();
		if (!l.isEmpty()) {
			// SqlSimpleNode leftNode = new SqlSimpleNode();
			// presentNode.setLeftNode();
		}
		
		op.acceptCall(this, call);
		System.out.println("Operator " + op.getName());
		if (op.getName().equals("AS"))
			aliasMap.put( call.getOperandList().get(0).toString().replace("`", ""), call.getOperandList().get(1).toString());
		if (call instanceof SqlBasicCall) {
			Multimap<String,String> map = HashMultimap.create();
			String opString = ((SqlBasicCall) call).getOperator().toString();
			if (InterestingOperators.contains(opString.toLowerCase())) {
				// functionsMap.put(op, value)
				String table = lastVisited.pop();
				if (table.equals("*")) {
					
					map.put(opString, call.getOperandList().get(0).toString().replace("`", ""));
					functionsToTables.put(table, map);
				} else {
					
//					functionsToTables.put(table, )
					map.put(opString, call.getOperandList().get(0).toString().replace("`", ""));
					functionsToTables.put(table, map);
					
				}

				// lastVisited.peek()
				// we need to create a structure to keep in whitch table and
				// operator this functions is
				// applied
			}
		}
		return null;
		// return op.acceptCall(this, call);
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
			SqlIdentifier lastId = id.skipLast(1);
			// identifiers.add(new String[] { id.getSimple() });
			if(!InterestingOperators.contains(id.getSimple())) lastVisited.push(id.getSimple());
		} else if (id.isStar()) {
			// identifiers.add(new String[] { "*" });
			lastVisited.push("*");
		} else {
			System.out.println(id.names);
			// identifiers.add(new String[] { id.names.get(0), id.names.get(1)
			// });
			for (String name : id.names) {
				if (!tables.get(name).isEmpty()) {
					//functionsToTables.put(, value)
					lastVisited.push(name);

				}
			}

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

	@Override
	public R visit(SqlLiteral literal) {
		// TODO Auto-generated method stub
		return null;
	}

}
