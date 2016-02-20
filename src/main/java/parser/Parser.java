package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.apache.calcite.prepare.PlannerImpl;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.runtime.CalciteContextException;
import org.apache.calcite.runtime.CalciteException;
import org.apache.calcite.runtime.Resources.ExInst;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlDataTypeSpec;
import org.apache.calcite.sql.SqlDelete;
import org.apache.calcite.sql.SqlDialect;
import org.apache.calcite.sql.SqlDynamicParam;
import org.apache.calcite.sql.SqlFunction;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlInsert;
import org.apache.calcite.sql.SqlIntervalQualifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlLiteral;
import org.apache.calcite.sql.SqlMerge;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;
import org.apache.calcite.sql.SqlOperatorTable;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.SqlUpdate;
import org.apache.calcite.sql.SqlWindow;
import org.apache.calcite.sql.SqlWith;
import org.apache.calcite.sql.SqlWithItem;
import org.apache.calcite.sql.advise.SqlSimpleParser;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.SqlParser.ConfigBuilder;
import org.apache.calcite.sql.util.SqlBasicVisitor;
import org.apache.calcite.sql.util.SqlVisitor;
import org.apache.calcite.sql.validate.SelectScope;
import org.apache.calcite.sql.validate.SqlConformance;
import org.apache.calcite.sql.validate.SqlModality;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.sql.validate.SqlValidatorCatalogReader;
import org.apache.calcite.sql.validate.SqlValidatorException;
import org.apache.calcite.sql.validate.SqlValidatorImpl;
import org.apache.calcite.sql.validate.SqlValidatorNamespace;
import org.apache.calcite.sql.validate.SqlValidatorScope;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.apache.calcite.tools.RelConversionException;
import org.apache.calcite.tools.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.foundationdb.sql.parser.FromTable;
import com.google.common.collect.Multimap;

//to send sigint ^c use this 
//kill -s INT 3040
public class Parser {
	public static FrameworkConfig frameworkConfig;
	private static Logger log = LoggerFactory.getLogger(Parser.class);

	private static final String[] functions = { "count", "avg", "min", "max" };

	public void attachShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.error("Abrupty Terminated");
			}
		});
		log.info("Shut Down Hook Attached.");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SqlSimpleParser a = new SqlSimpleParser("parser");
		System.out.println(a.simplifySql("select * from users where users.id = 1 ;"));
		Parser parser = new Parser();
		parser.attachShutDownHook();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String s;
		SqlSimpleParser simpleparser = new SqlSimpleParser("parser");
		s = "select distinct count(A.id) as \"count\", C.salary,  C.name from A, B, C, D where A.id=B.id and C.name=B.name and "
				+ "C.age<B.age or C.age<>A.age and C.name=B.name  group by A.id , C.name ";
		// and + "C.age in (Select * from B where B.name='Jim')
		System.out.println("SIMPLE PARSER :" + simpleparser.simplifySql(s));
		SqlParser b = SqlParser.create(s);
		SqlNode node = null;
		try {
			node = b.parseStmt();
			System.out.println("SQLParser :" + node.toString());
			org.apache.calcite.tools.Frameworks.ConfigBuilder configBuilder = Frameworks.newConfigBuilder();
			FrameworkConfig fC = configBuilder.build();
			Planner pl = Frameworks.getPlanner(fC);
			node = pl.parse(s);
			System.out.println("---Planner:" + node.toString());
		} catch (SqlParseException e) {
			log.error("SQLParseException" );
			e.printStackTrace(System.err);
		}
		MySqlVisitorImpl<SqlNodeList> insperctorB = new MySqlVisitorImpl<>();
		// MySqlVisitorImpl has a list of identifiers. When applied on an
		// sql part i.e. say select statement, he returns the
		// list of identifiers contained.
		// ORDER BY is FUCKING DIFFERENT
		SqlQueryMeta query = new SqlQueryMeta((SqlSelect) node);
		SqlNode ps = query.getSelect().accept(insperctorB);

		System.out.println();
		System.out.println(insperctorB.identifiers);
		query.setSelectIdentifiers(insperctorB.identifiers);

		insperctorB = new MySqlVisitorImpl<>();
		query.getWhere().accept(insperctorB);
		query.setWhereIdentifiers(insperctorB.identifiers);

		insperctorB = new MySqlVisitorImpl<>();
		query.getGroupby().accept(insperctorB);
		query.setGroupByIdentifiers(insperctorB.identifiers);

		// //ORDER BY is FUCKING DIFFERENT
		// insperctorB = new MySqlVisitorImpl<>();
		// query.getOrderby().accept(insperctorB);
		System.out.println("end");

		// Start Specifying set of Unique tables in order to start making
		// the sql batch call
		log.debug("Start classifying tables ");
		Path path = Paths.get("../UDF_Statement.sql");
		//
		log.info("Any sql files will be placed under current directory " + path.toUri().toString());
		byte[] fileArray;
		Charset charset = Charset.forName("UTF-8");
		String stmt = "Distributed create table temp A to 10 on id as Select id from A ;";

		try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) {
			writer.write(stmt, 0, stmt.length());
		} catch (IOException x) {
			log.error("Exception {}", x.getMessage());
		}
		Multimap<String, String> map = query.findTableParticipatingIdentifiers(query.getSelectIdentifiers());
		map = query.findTableParticipatingIdentifiers(query.getWhereIdentifiers());

		log.info("sup");

	}

}
