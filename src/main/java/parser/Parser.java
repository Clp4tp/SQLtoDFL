package parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//to send sigint ^c use this 
//kill -s INT 3040
public class Parser {
	public static FrameworkConfig frameworkConfig;
	private static Logger log = LoggerFactory.getLogger(Parser.class);

	public void attachShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.info("Terminated");
			}
		});
		log.info("Shut Down Hook Attached.");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Parser parser = new Parser();
		parser.attachShutDownHook();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s;

		// SqlSimpleParser simpleparser = new SqlSimpleParser("parser");
		s = "select distinct  count(A.id) as \"count\", count(*) as total, count(A.name), count(C.id) as Cbefirst "
				+ "from A , B, C  where A.id=B.id  and A.name=B.name and C.name=B.name ";

		// s = "select distinct count(A.id) as \"count\", C.salary as \"sal\",
		// C.name as \"employee\", count(*) as total from A , B, C, D where
		// A.id=B.id and C.name=B.name "
		// + "and "
		// + "C.age<B.age or C.age<>A.age and D.name=B.name group by A.id ,
		// C.name ";
		// and + "C.age in (Select * from B where B.name='Jim')
		
//		 s = "select " 
////		 +"l_returnflag, l_linestatus, sum(l_quantity) as sum_qty,"
////		 + " sum(l_extendedprice) as sum_base_price,"
//		 + " sum(l_extendedprice * (1 - l_discount)) as sum_disc_price, "
////		 + " sum(l_extendedprice * (1 - l_discount) * (1 + l_tax)) as sum_charge, "
////		 + " avg(l_quantity) as avg_qty, avg(l_extendedprice) as avg_price,"
//		 + " avg(l_discount) as avg_disc, count(*)  as count_order "
//		 + " from lineitem " 
//		 + " where l_shipdate <= '1998-12-01' "
//		 + " group by l_returnflag, l_linestatus ";
		 
		// String end = "order by l_returnflag, l_linestatus";
		parser.processQuery(s);
		 

	}

	public String processQuery(String expr){
		
		 SqlNode node = this.parseQuery(expr);
		SqlTreeReverseVisitor<SqlNodeList> insperctorR = new SqlTreeReverseVisitor<>();
		SqlTreeVisitor<SqlNodeList> insperctorB = new SqlTreeVisitor<>();
		// MySqlVisitorImpl has a list of identifiers. When applied on an
		// sql part i.e. say select statement, he returns the
		// list of identifiers contained.
		// ORDER BY is FUCKING DIFFERENT
		SqlQueryMeta query = new SqlQueryMeta((SqlSelect) node);
		query.getSelect().accept(insperctorB);
		query.setSelectIdentifiers(insperctorB.identifiers);
		insperctorB = new SqlTreeVisitor<>();
		
		query.getWhere().accept(insperctorB);
		query.setWhereIdentifiers(insperctorB.identifiers);
		query.setWhereOperations(insperctorB.whereOperations);
		// after having the identifiers lets keep them for our next run with the
		insperctorR.setIdentifiers(insperctorB.identifiers);
		insperctorR.setTables(query.findTableParticipatingIdentifiers(query.getSelectIdentifiers()));
		query.getSelect().accept(insperctorR);
		query.setAliasMap(insperctorR.aliasMap);
		query.setFunctionsToTables(insperctorR.functionsToTables);
		insperctorB = new SqlTreeVisitor<>();

		JoinAttributesVisitor<SqlNodeList> joinAttrVisitor = new JoinAttributesVisitor<>();
		query.getWhere().accept(joinAttrVisitor);
		query.setJoinOperations(joinAttrVisitor.joinOperations);
		query.operatorAndSubtree = joinAttrVisitor.operatorAndSubtree;
		query.JoinOperators = joinAttrVisitor.xlusiveOperator;
		Path path = Paths.get("UDF_Statement.sql");
		String output = new DflComposer().writeQueryToFile(path, query, 10, "id", "");
		return output;
	}

	public SqlNode parseQuery(String s) {
		SqlParser p = SqlParser.create(s);
		SqlNode node = null;
		try {
			node = p.parseStmt();
//			System.out.println("SQLParser :" + node.toString());
			org.apache.calcite.tools.Frameworks.ConfigBuilder configBuilder = Frameworks.newConfigBuilder();
			FrameworkConfig fC = configBuilder.build();
			Planner pl = Frameworks.getPlanner(fC);
			node = pl.parse(s);
		} catch (SqlParseException e) {
			log.error("SQLParseException");
			e.printStackTrace();
		}
		return node;
	}
}
