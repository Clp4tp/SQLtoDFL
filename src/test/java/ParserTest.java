import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.codehaus.janino.Java.AssertStatement;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

import parser.Parser;

public class ParserTest {
	private static Logger log = LoggerFactory.getLogger(ParserTest.class);

	@Test
	public void testQueryParserCase0() {
		System.out.println("------------testQueryParserCase0-----------------");
		String query = "select Employee.id " + "from Employee, Director "
				+ "where Employee.id=Director.id and Employee.salary>1500";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(query));
		System.out.println("------------testQueryParserCase0-----------------");
	}

	 @Test
	public void testQuerySelectCase1() {
		System.out.println("------------testQueryParserCase1-----------------");
		String query = "select count(Employee.id) as total, sum(Employee.salary) as totalCost "
				+ "from Employee, Director " + "where Employee.id=Director.id and Employee.salary>1500";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(query));
		System.out.println("------------testQueryParserCase1-----------------");
	}

	@Test
	public void testQuerySelectCase2() { // question here -> do we need the 1rst
											// distributed query?

		System.out.println("------------testQueryParserCase2-----------------");
		String query = "select count(Employee.salary) as total " + "from Employee " + "where Employee.salary>1500";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(query));
		System.out.println("------------testQueryParserCase2-----------------");
	}

    @Test
	public void testQuerySelectCase3() {// should detect NO JOIN
		System.out.println("------------testQueryParserCase3-----------------");
		String s = "select  count(A.id) as \"count\", C.salary as \"sal\", "
				+ "C.name as \"employee\", count(*) as total from A , B, C, D where "
				+ "A.id=B.id and C.name=B.name and " + "C.age<B.age or C.age<>A.age and D.name=B.name";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(s));
		System.out.println("------------testQueryParserCase3-----------------");
	}

	 @Test
	public void testQuerySelectCase4() {// should detect JOIN on NAME
		System.out.println("------------testQueryParserCase4-----------------");
		String s = "select  count(A.id) as \"count\", C.salary as \"sal\", "
				+ "C.name as \"employee\", count(*) as total from A , B, C, D where "
				+ "A.id=B.id and C.name=B.name and A.name=B.name and "
				+ "C.age<B.age or C.age<>A.age and D.name=B.name";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryParserCase4-----------------");
	}

	 @Test
	public void testQuerySelectCase5() {// should detect JOIN on NAME , sum
		System.out.println("------------testQueryParserCase5-----------------");
		String s = "select  sum(A.id) as \"sum\", C.salary as \"sal\", "
				+ "C.name as \"employee\", count(*) as total from A , B, C, D where "
				+ "A.id=B.id and C.name=B.name and A.name=B.name and "
				+ "C.age<B.age or C.age<>A.age and D.name=B.name";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryParserCase5-----------------");
	}

	 @Test
	public void testQuerySelectCase6() {// should detect JOIN on NAME
		System.out.println("------------testQueryParserCase6-----------------");
		String s = "select distinct count(A.id) as \"count\", C.salary as \"sal\", "
				+ "C.name as \"employee\", count(*) as total from A , B, C, D where "
				+ "A.id=B.id and C.name=B.name and A.name=B.name and "
				+ "C.age<B.age or C.age<>A.age and D.name=B.name";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryParserCase6-----------------");
	}

	 @Test
	public void testQuerySelectCase7() {// should NOT DETECT JOIN
		System.out.println("------------testQueryParserCase7-----------------");
		String s = "select * from A , B, C, D where " + "A.id=B.id and C.name=B.name and A.name=B.name and "
				+ "C.age<B.age or C.age<>A.age and D.name like \"Jim\" ";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryParserCase7-----------------");
	}

	@Test
	public void testQuerySelectCase8() {// should detect JOIN on NAME
		System.out.println("------------testQueryParserCase8-----------------");
		String s = "select * from A  where " + "A.id>20 ";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryParserCase8-----------------");
	}

	@Test
	public void testQuerySelectCase9() {// should detect JOIN on NAME
		System.out.println("------------testQueryParserCase9-----------------");
		String s = "Select * from A where A.name=\"JIM\" ";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryParserCase9------------------");
	}

	
	//@Test
	public void testQuerySelectCase10() {// should detect JOIN on NAME
		System.out.println("------------testQueryParserCase10-----------------");
		String s = "Select A.id from A, B, C where A.id=B.id and C.name=B.name ";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryParserCase10------------------");
	}

}
