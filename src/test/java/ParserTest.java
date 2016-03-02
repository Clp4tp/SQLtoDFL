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

    @Test
    public void testQueryParserCase0() {
    	System.out.println("------------testQueryParserCase0-----------------");
    	String query  = "select Employee.id "+
    					"from Employee, Director "+
    					"where Employee.id=Director.id and Employee.salary>1500";
    	Parser parser = new Parser();
    	System.out.println(parser.processQuery(query));
    	System.out.println("------------testQueryParserCase0-----------------");
    }
    
    
    @Test
    public void testQuerySelectCase1(){
    	System.out.println("------------testQueryParserCase1-----------------");
    	String query  = "select count(Employee.id) as total, sum(Employee.salary) as totalCost "+
    					"from Employee, Director "+
    					"where Employee.id=Director.id and Employee.salary>1500";
    	Parser parser = new Parser();
    	System.out.println(parser.processQuery(query));
    	System.out.println("------------testQueryParserCase1-----------------");
    }

    @Test
    public void testQuerySelectCase2(){
    	System.out.println("------------testQueryParserCase2-----------------");
    	String query  = "select count(Employee.salary) as total "+
    					"from Employee "+
    					"where Employee.salary>1500";
    	Parser parser = new Parser();
    	System.out.println(parser.processQuery(query));
    	System.out.println("------------testQueryParserCase2-----------------");
    }
}
