package parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.SqlSelectOperator;
import org.apache.calcite.sql.SqlSyntax;
import org.apache.calcite.sql.fun.SqlHistogramAggFunction;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public final class DflComposer {
    private PartitionManager manager;
    private static Charset charset = Charset.forName("UTF-8");
    private static Logger log = LoggerFactory.getLogger(DflComposer.class);

    public String writeQueryToFile(Path path, SqlQueryMeta query, int noPartitions, String partitionAttr,
	    String resultTable) {
	// TODO combined statement has to be partitioned to 1 -- REMEMBER

	String direct = applyDirect(query);
	if (direct == "") {
	    partitionAttr = "";
	} else {
	    partitionAttr = direct;
	}
	Multimap<String, String> selectMap = query.findTableParticipatingIdentifiers(query.getSelectIdentifiers());
	Multimap<String, String> whereMap = query.findTableParticipatingIdentifiers(query.getWhereIdentifiers());
	String dfl = "";
	dfl += prettyPrint(query.getCall().toString().toLowerCase()) + ";\n\n";
	HashMap<String, String> aliasedTables = new HashMap<>();
	if (!query.getFromTables().isEmpty() && query.getFromTables().size() == 1) {
	    for (String key : whereMap.keySet()) {
		Collection<String> random = whereMap.get(key);
		if (random.size() >= 1) {
		    Iterator<String> it = random.iterator();
		    if (it.hasNext()) {
			partitionAttr = it.next();
		    }
		}
	    }
	}
	for (String table : query.getFromTables()) {
	    if (query.getFromTables().size() == 1)
		break;
	    aliasedTables.put(table, "temp" + table);
	    String partAttr = partitionAttr;
	    if (partitionAttr.equals("")) {
		partAttr = (String) (whereMap.get(table)).iterator().next();
		if (whereMap.get(table).contains("ID"))
		    partAttr = "ID"; // prefer this one( we cannot know the key
				     // anyway without a database);
	    }
	    String dflStmt = "distributed create temporary table temp" + table + " to " + noPartitions + " on "
		    + partAttr + " as \n";
	    selectMap.putAll(whereMap);
	    String projection = (prettyPrint(selectMap.get(table).toString())).toLowerCase().length() == 0 ? "*"
		    : (prettyPrint(selectMap.get(table).toString())).toLowerCase();

	    String selectStmt = " select " + projection + " " + "from  " + table + ";\n\n";
	    dfl += dflStmt + selectStmt;
	}

	// CREATE THE FINAL STATEMENTS REPARTITION AND END PRODUCT
	// if()

	// TODO -- Start repartitioning here
	dfl += repartition(query, dfl, noPartitions);

	resultTable = resultTable.length() == 0 ? "result" : resultTable;
	dfl += "distributed create table  " + resultTable + " to 1 "
		+ (partitionAttr.equals("") ? "" : "on " + partitionAttr + " ") + "as "
		+ (direct == "" ? "\n" : "direct \n");
	if (query.getFromTables().size() != 1) {
	    dfl += getDflSelectStmt(query);// CombinedDflSelectStmt(query);
	} else {
	    return dfl += prettyPrint(query.getCall().toString().toLowerCase());
	}

	if (aliasedTables.size() != 0)
	    dfl += "from " + prettyPrint(aliasedTables.values().toString()) + " \n";
	else {
	    dfl += "from " + prettyPrint(query.getFromTables().toString()) + " \n";
	}

	// WHERE LAST STATEMENT
	dfl += "where ";
	String[] s = prettyPrint(query.getWhere().toString()).split("\\s+");
	for (String subs : s) {
	    String[] any = subs.split("\\.");
	    for (String a : any) {
		if (aliasedTables.containsKey(a)) {
		    dfl += aliasedTables.get(a) + ".";
		} else {
		    dfl += a + " ";
		}
	    }
	}
	dfl += ";";
	try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) {
	    writer.write(dfl, 0, dfl.length());
	} catch (IOException x) {
	    log.error("Exception {}", x.getMessage());
	}

	return dfl;

    }

    private String getDflSelectStmt(SqlQueryMeta query) {
	List<List<String>> functionsPerTable = query.getFunctionsToTables();
	Multimap<String, String> aliasMap = query.getAliasMap();
	String stmt = "select ";
	if (query.getCall().toString().contains(" DISTINCT ")) {
	    stmt += "distinct ";
	}
	if (functionsPerTable.size() > 0) {
	    for (List<String> list : functionsPerTable) {
		if (list.size() < 2) {
		    throw new NotImplementedException("Found empty function");
		}
		String[] table_and_column = list.get(1).split("\\.");
		String temp = null;
		if (table_and_column[0] == "*") {
		    temp = "";
		} else {
		    temp = "temp";
		}
		// String temp = key.equals("*") ? "" : "temp";
		String operator = list.get(0) + "(" + temp + prettyPrint(list.get(1)) + ")";
		stmt += operator;
		if (aliasMap.containsKey(list.get(0) + "(" + list.get(1) + ")")) {
		    stmt += " as " + prettyPrint(aliasMap.get(list.get(0) + "(" + list.get(1) + ")").toString());
		}
		stmt += ", ";
	    }
	} else if (query.getSelectIdentifiers().size() > 0) {
	    for (String[] list : query.getSelectIdentifiers()) {
		if (list.length == 1) {
		    stmt += list[0];
		} else {
		    stmt += "temp" + list[0] + "." + list[1];
		}

		// stmt=stmt.substring(0, stmt.lastIndexOf('.'));
		stmt += ",";
	    }
	} else {// append star
	    stmt += "* ";
	}

	if (stmt.contains(","))
	    stmt = stmt.substring(0, stmt.lastIndexOf(',')) + " \n";
	return stmt;
    }

    private String applyDirect(SqlQueryMeta query) {

	// We need to check if we have any joining attributes
	// cases
	// 1. One table shows up , use direct?
	// 2. Two or more tables we need to find their partition attribute.
	// WORKFLOW : from Tables : A, B, C, ... N represented as a graph
	// find path connecting A-B-C...-N on any order on one attribute i.e the
	// same attribute needs to be present to all nodes connecting them
	// with join operator [ =, >=, <= ]

	if (query.getFromTables().size() > 1) {
	    List<JoinCondition> joins = new ArrayList<>();
	    // Stack<JoinCondition> stack = new Stack<>();
	    for (SqlBasicCall call : query.getJoinOperations()) {
		joins.add(new JoinCondition(call));
		// stack.push(new JoinCondition(call));
	    }

	    manager = new PartitionManager(joins, query);

	    // String directJoin = JoinCondition.findCycleImproved(joins,
	    // query);
	    if (!manager.masterPartition.equals("")) {
		log.info("Direct JOIN detected on attribute " + manager.masterPartition);
	    } else {
		// TODO work here to start
		log.info("Repartitions Detected");
	    }
	    // if (directJoin != "") {
	    // log.info("Direct JOIN detected on attribute " + directJoin);
	    // } else
	    // log.info("No JOIN detected ");

	    return manager.masterPartition;
	}
	return "";
    }

    private String repartition(SqlQueryMeta query, String dfl, int noPartitions) {
	String partitionDfl = "";
	if (manager.masterPartition.equals("")) {
	    if (manager.repartitions.size() != 0) {
		int i = 0;
		for (List<JoinCondition> list : manager.repartitions) {

		    List<String> tables = new ArrayList<>();
		    for (JoinCondition jC : list) {
			if (!tables.contains(jC.tableL))
			    tables.add(jC.tableL);
			if (!tables.contains(jC.tableR))
			    tables.add(jC.tableR);
		    }
		    partitionDfl += "distributed create temporary table temp" + prettyPrint(tables.toString()) + " to "
			    + noPartitions + " on " + manager.joinOnTablesDirect[i];
		    i++;

		}
	    }

	}
	return "";
    }

    private static String prettyPrint(String s) {
	return new String(s.replace("[", "").replace("]", "").replace("`", ""));

    }

}
