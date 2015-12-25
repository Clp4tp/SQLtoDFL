package parser;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.logging.Logger;

import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.advise.SqlSimpleParser;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Program;

public class Parser {

	public void attachShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Bye!");
			
			}
		});
		System.out.println("Shut Down Hook Attached.");
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SqlSimpleParser a = new SqlSimpleParser("parser");
		System.out.println(a.simplifySql("select * from users where users.id = 1 ;"));
		// SqlParser b = SqlParser.create("select * from jim");
		// try {
		// SqlNode node = b.parseQuery();
		// System.out.println(node.toString());
		// InputStreamReader cin = new InputStreamReader(System.in);
		// int i; char c;
		// try {
		// while((i=cin.read())!=-1)
		// {
		// // int to character
		// c=(char)i;
		//
		// // print char
		// System.out.println("Character Read: "+c);
		// }
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// } catch (SqlParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		//// System.out.println(node.toString());
		 Parser parser = new Parser();
		  parser.attachShutDownHook();
		  System.out.println("Last instruction of Program....");
			while (true) {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				try {
					String s = "";
					while (!s.contains(";")) {
						s = s + br.readLine();
					}
					// s[] = s.length() -1;
					s = s.replace(";", " ").replace("\n", " ");
					SqlSimpleParser simpleparser = new SqlSimpleParser("parser");
					
					System.out.println("SIMPLE PARSER :"+simpleparser.simplifySql(s));
					
					SqlParser b = SqlParser.create(s);
					SqlNode node = b.parseQuery();
					System.out.println("SQLParser :"+node.toString());
				} catch (SqlParseException | IOException e) {
					// TODO Auto-generated catch block
					// System.out.println(e.toString());
					// System.out.println(e.getLocalizedMessage());
					System.out.println(e.getMessage());
				}
			}

	}

}
