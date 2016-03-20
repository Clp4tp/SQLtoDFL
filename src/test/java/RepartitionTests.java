import org.apache.calcite.jdbc.CalcitePrepare.Query;
import org.apache.calcite.linq4j.QueryProviderImpl;
import org.apache.calcite.plan.RelTrait;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelNodes;
import org.junit.Test;

import parser.Parser;

public class RepartitionTests {

	// @Test
	public void testQuerySelectCase1() {// should detect repartition on NAME
		System.out.println("------------testQueryParserCase10-----------------");
		String s = "Select A.id from A, B, C where A.id=B.id and C.name=B.name ";
		Parser parser = new Parser("REP");
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryParserCase10------------------");
	}

	//@Test
	public void testQuerySelectCase7() {// should NOT DETECT repartition
		System.out.println("------------testQueryParserCase7-----------------");
		String s = "select * from A , B, C, D, E, F, G where " + "A.id=B.id and  A.name=B.name and C.name=B.name and "
				+ "D.id = B.id and E.id = B.id and F.name=C.name and C.name = A.name and G.id=A.id ";
		Parser parser = new Parser("REP");
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryParserCase7-----------------");
	}

	@Test
	public void testQuerySelectCase8() {// should NOT DETECT JOIN
		System.out.println("------------testQueryParserCase7-----------------");
		String s = "select A.name from A , B, C, D where " + "A.id=B.id and  A.name=B.name and C.name=B.name and "
				+ "D.id = B.id  ";
		Parser parser = new Parser("REP");
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryParserCase7-----------------");
	}

	@Test
	public void testQuerySelectCase6() {// should detect direct JOIN on NAME
		System.out.println("------------testQueryParserCase6-----------------");
		String s = "select distinct count(A.id) as \"count\", count(*) as total from A , B, C, D where "
				+ "A.id=B.id and C.name=B.name and A.name=B.name and "
				+ "C.age<B.age or C.age<>A.age and D.name=B.name";
		Parser parser = new Parser("REP");
		System.out.println(parser.processQuery(s));
		System.out.println("------------testQueryParserCase6-----------------");
	}

}
