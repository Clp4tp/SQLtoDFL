import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.*;

import parser.Parser;

public class ParserTest {
    private static Logger log = LoggerFactory.getLogger(ParserTest.class);

    //@Test
    public void testQueryParserCase() {

        String s = "select distinct count(A.id) as \"count\", C.salary,  C.name from A, B, C, D where A.id=B.id and C.name=B.name and "
                + "C.age<B.age or C.age<>A.age and D.name=B.name  group by A.id , C.name ";

        SqlParser b = SqlParser.create(s);
        SqlNode node = null;
        try {
            node = b.parseStmt();
            System.out.println("SQLParser :" + node.toString());
            org.apache.calcite.tools.Frameworks.ConfigBuilder configBuilder = Frameworks.newConfigBuilder();
            FrameworkConfig fC = configBuilder.build();
            Planner pl = Frameworks.getPlanner(fC);
            node = pl.parse(s);
        } catch (SqlParseException e) {
            log.error("SQLParseException");
            e.printStackTrace(System.err);
        }
        String ss = "SELECT DISTINCT COUNT(`A`.`ID`) AS `count`, `C`.`SALARY`, `C`.`NAME` FROM `A`, `B`,`C`,`D` "
                + "WHERE `A`.`ID` = `B`.`ID` AND `C`.`NAME` = `B`.`NAME` AND `C`.`AGE` < `B`.`AGE` OR `C`.`AGE` <> `A`.`AGE` AND `D`.`NAME` = `B`.`NAME` "
                + "GROUP BY `A`.`ID`, `C`.`NAME`";
        log.info("checking equality");
        String expected = node.toString().replace("\n", " ").replace(" ", "").trim();
//        String actual = assertEquals(node.toString().replace("\n", " ").replace(" ", "").trim(),
//                ss.replace(" ", "").trim());
    }
    
    
    @Test
    public void testQuerySelect_case_one(){
    	
    	String query  = "select count(Employee.id) as total, sum(Employee.salary) as totalCost "+
    					"from Employee, Director "+
    					"where Employee.id=Director.id and Employee.salary>1500";
    	
    
    	Parser parser = new Parser();
    	System.out.println(parser.processQuery(query));
    	
    }

}
