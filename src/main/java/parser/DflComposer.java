package parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.PrettyPrinter;
import com.google.common.collect.Multimap;

public final class DflComposer {

	private static Charset charset = Charset.forName("UTF-8");

	private static Logger log = LoggerFactory.getLogger(DflComposer.class);

	public static void writeTableToFile(Path path, SqlQueryMeta query, int noPartitions, String partitionAttr) {

		Multimap<String, String> selectMap = query.findTableParticipatingIdentifiers(query.getSelectIdentifiers());
		Multimap<String, String> whereMap = query.findTableParticipatingIdentifiers(query.getWhereIdentifiers());
		String dfl = "";
		for (String table : query.getFromTables()) {

			String dflStmt = "distributed create table temp" + table + " to " + noPartitions + " on " + partitionAttr
					+ " as \n";
			selectMap.putAll(whereMap);
			String selectStmt = " Select " + (prettyPrint(selectMap.get(table).toString())).toLowerCase() + " "
					+ "from  " + table + "\n\n";
			dfl+=dflStmt + selectStmt;
		}
		
		
			try (BufferedWriter writer = Files.newBufferedWriter(path, charset)) {
				writer.write(dfl  , 0, dfl.length());
				
			} catch (IOException x) {
				log.error("Exception {}", x.getMessage());
			}

		

	}

	private static String prettyPrint(String s) {

		if (s.contains("["))
			return new String(s.replace("[", "").replace("]", ""));

		return "";
	}

}
