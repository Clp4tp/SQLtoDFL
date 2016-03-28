import org.apache.calcite.jdbc.CalcitePrepare.Query;
import org.apache.calcite.linq4j.QueryProviderImpl;
import org.apache.calcite.plan.RelTrait;
import org.apache.calcite.plan.volcano.VolcanoPlanner;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelNodes;
import org.junit.Test;

import parser.Parser;

public class RepartitionTests {

	//@Test
	public void testQueryRepartitionSelectCase1() {
		System.out.println("------------testQueryRepartitionSelectCase1-----------------");
		String s = "Select A.id from A, B, C where A.id=B.id and C.name=B.name ";
		Parser parser = new Parser("REP");
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryRepartitionSelectCase1------------------");
	}

	//@Test
	public void testQueryRepartitionSelectCase2() {
		System.out.println("------------testQueryRepartitionSelectCase2-----------------");
		String s = "select * from A , B, C, D, E, F, G where " + "A.id=B.id and  A.name=B.name and C.name=B.name and "
				+ "D.id = B.id and E.id = B.id and F.name=C.name and C.name = A.name and G.id=A.id ";
		Parser parser = new Parser("REP");
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryRepartitionSelectCase2-----------------");
	}

	//@Test
	public void testQueryRepartitionSelectCase3() {
		System.out.println("------------testQueryRepartitionSelectCase3-----------------");
		String s = "select A.name from A , B, C, D where " + "A.id=B.id and  A.name=B.name and C.name=B.name and "
				+ "D.id = B.id  ";
		Parser parser = new Parser("REP");
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryRepartitionSelectCase3-----------------");
	}
	
	//@Test
	public void testQueryRepartitionSelectCase4() {
		System.out.println("------------testQueryRepartitionSelectCase4-----------------");
		String s = "select distinct count(A.id) as \"count\", count(*) as total from A , B, C, D where "
				+ "A.id=B.id and C.name=B.name and A.name=B.name and "
				+ "C.age<B.age or C.age<>A.age and D.name=B.name";
		Parser parser = new Parser("REP");
		System.out.println(parser.processQuery(s));
		System.out.println("------------testQueryRepartitionSelectCase4-----------------");
	}
	
	//@Test
	public void testQueryRepartitionSelectCase5() {
		System.out.println("------------testQueryRepartitionSelectCase5-----------------");
		String s = "select A.id from A , B, C, D, E, F, G where " + "A.id=B.id and  A.name=B.name and C.name=B.name and "
				+ "D.id = B.id and E.id = B.id and F.name=C.name and C.age = G.age and G.age=B.age ";
		Parser parser = new Parser("REP");
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryRepartitionSelectCase5-----------------");
	}


}
