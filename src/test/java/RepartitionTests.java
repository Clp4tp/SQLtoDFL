import org.junit.Test;

import parser.Parser;

public class RepartitionTests {

	@Test
	public void testQuerySelectCase2() { // question here -> do we need the 1rst
											// distributed query?
		//TODO run this there are some problems
		System.out.println("------------testQueryParserCase2-----------------");
		String query = "select count(Employee.salary) as total " + "from Employee " + "where Employee.salary>1500";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(query));
		System.out.println("------------testQueryParserCase2-----------------");
	}

//	@Test
//	public void testQuerySelectCase10() {// should detect JOIN on NAME
//		System.out.println("------------testQueryParserCase10-----------------");
//		String s = "Select A.id from A, B, C where A.id=B.id and C.name=B.name ";
//		Parser parser = new Parser();
//		System.out.println(parser.processQuery(s));
//
//		System.out.println("------------testQueryParserCase10------------------");
//	}

}
