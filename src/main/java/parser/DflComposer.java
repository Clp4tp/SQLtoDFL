package parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.SqlSelectOperator;
import org.apache.calcite.sql.SqlSyntax;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public final class DflComposer {

	private static Charset charset = Charset.forName("UTF-8");

	private static Logger log = LoggerFactory.getLogger(DflComposer.class);

	public static String writeQueryToFile(Path path, SqlQueryMeta query, int noPartitions, String partitionAttr, String resultTable) {
		// TODO
		// where identifiers dont keep the literals
		
		Multimap<String, String> selectMap = query.findTableParticipatingIdentifiers(query.getSelectIdentifiers());
		Multimap<String, String> whereMap = query.findTableParticipatingIdentifiers(query.getWhereIdentifiers());
		String dfl = "";
		HashMap<String, String> aliasedTables = new HashMap<>();
		for (String table : query.getFromTables()) {
			aliasedTables.put(table, "temp" + table);
			String dflStmt = "distributed create temporary table temp" + table + " to " + noPartitions + " on "
					+ partitionAttr + " as \n";
			selectMap.putAll(whereMap);
			String projection = (prettyPrint(selectMap.get(table).toString())).toLowerCase().length() == 0 ? "*"
					: (prettyPrint(selectMap.get(table).toString())).toLowerCase();

			String selectStmt = " select " + projection + " " + "from  " + table + "\n\n";
			dfl += dflStmt + selectStmt;
		}

		// create the end product using
		// 1. alias and functions
		// 2. any join conditions
		
		resultTable = resultTable.length()==0 ? "result" : resultTable;
		dfl += "distributed create table " +resultTable + " as \n"; //TODO missing externat or partition value
		dfl += getCombinedDflSelectStmt(query);
		dfl += "from " + prettyPrint(aliasedTables.values().toString()) + "\n";
		
		dfl += "where ";
		
			String[] s = prettyPrint(query.getWhere().toString()).split("\\s+");
			for (String subs : s) {
				log.info(s.toString());
				String[] any = subs.split("\\.");
				for (String a:  any){
					if(aliasedTables.containsKey(a)){
						dfl+= aliasedTables.get(a)+".";
					}			
					else dfl+= a+ " ";
				
				}
			}
		

		try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) {
			writer.write(dfl, 0, dfl.length());
		} catch (IOException x) {
			log.error("Exception {}", x.getMessage());
		}
		return dfl;
	}

	
	private static String getCombinedDflSelectStmt( SqlQueryMeta query){
		Multimap<String, Multimap<String, String>> functionsMapPerTable = query.getFunctionsMapPerTable();
		Multimap<String, String> aliasMap = query.getAliasMap();
		String stmt = "select ";

		for (String key : functionsMapPerTable.keySet()) {
			Collection<Multimap<String, String>> map = functionsMapPerTable.get(key);
			for (Multimap<String, String> mmap : map) {
				for (String function : mmap.keySet()) {
					log.info("dad");
					Collection<String> l = mmap.keySet();
					String temp = key.equals("*") ? "" : "temp";
					stmt += function + "(" + temp + prettyPrint(mmap.get(function).toString()) + ")";
					String s = function + "(" + prettyPrint(mmap.get(function).toString()) + ")";
					if (aliasMap.containsKey(s)) {
						stmt += " as " + prettyPrint(aliasMap.get(s).toString());
					}
				}

				stmt += ", ";
			}

			log.info("dad");
		}
		// remove trailing comma
		stmt = stmt.substring(0, stmt.lastIndexOf(',')) + " as \n"; // add
																	// external
																	// of sth
																	// else here
		return stmt;											
	}
	
	private static String prettyPrint(String s) {

		return new String(s.replace("[", "").replace("]", "").replace("`", ""));

	}

}
