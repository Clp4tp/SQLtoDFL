import org.junit.Test;

import parser.Parser;

public class RepartitionTests {

	//@Test
	public void testQuerySelectCase2() { // question here -> do we need the 1rst
											// distributed query?
		//TODO run this there are some problems
		System.out.println("------------testQueryParserCase2-----------------");
		String query = "select count(Employee.salary) as total " + "from Employee " + "where Employee.salary>1500";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(query));
		System.out.println("------------testQueryParserCase2-----------------");
	}

	//@Test
	public void testQuerySelectCase10() {// should detect JOIN on NAME
		System.out.println("------------testQueryParserCase10-----------------");
		String s = "Select A.id from A, B, C where A.id=B.id and C.name=B.name ";
		Parser parser = new Parser();
		System.out.println(parser.processQuery(s));

		System.out.println("------------testQueryParserCase10------------------");
	}
	
	//@Test
		public void testQuerySelectCase7() {// should NOT DETECT JOIN
			System.out.println("------------testQueryParserCase7-----------------");
			String s = "select * from A , B, C, D, E, F, G where " + "A.id=B.id and  A.name=B.name and C.name=B.name and "
					+ "D.id = B.id and E.id = B.id and F.name=C.name and G.id=A.id ";
			Parser parser = new Parser();
			System.out.println(parser.processQuery(s));

			System.out.println("------------testQueryParserCase7-----------------");
		}

		
		 @Test
			public void testQuerySelectCase8() {// should NOT DETECT JOIN
				System.out.println("------------testQueryParserCase7-----------------");
				String s = "select * from A , B, C, D where " + "A.id=B.id and  A.name=B.name and C.name=B.name and "
						+ "D.id = B.id  ";
				Parser parser = new Parser();
				System.out.println(parser.processQuery(s));

				System.out.println("------------testQueryParserCase7-----------------");
			}
		 
		 //@Test
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

}
