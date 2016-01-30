package foundation.db;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.advise.SqlSimpleParser;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;

import com.foundationdb.sql.parser.SQLParser;
import com.foundationdb.sql.parser.StatementNode;

public class FoundationParser {
	public void attachShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Bye!");

			}
		});
		System.out.println("Shut Down Hook Attached.");
	}
	
	public static void main(String[] args) throws Exception {
		
		SQLParser parser = new SQLParser();
		new FoundationParser().attachShutDownHook();
		StatementNode stmt = null;
//		while (true) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String s = "";
//			while (!s.contains(";")) {
				s = s + br.readLine();
				s = s.replace(";", " ").replace("\n", " ");
				s = "SELECT A.id from A, B where A.id=B.id";
				stmt = parser.parseStatement(s);
				stmt.treePrint();
				VisitorF visitor  = new VisitorF();
//				stmt.accept(visitor);
				stmt.accept(new FromTableFinder());
//			}
			
//		}
	}
}