package parser;

import java.util.List;
import java.util.logging.Logger;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.commons.lang.NotImplementedException;

public class JoinCondition {
	private static Logger log = Logger.getLogger(JoinCondition.class.toString());
	private String[] left;
	private String[] right;
	private String operator;
	private boolean isSimple = false;

	public JoinCondition(SqlBasicCall call) {
		operator = call.getOperator().toString();
		SqlNode[] operands = call.operands;
		if (operands[0] instanceof SqlIdentifier && operands[1] instanceof SqlIdentifier) {
			SqlIdentifier op1 = (SqlIdentifier) operands[0];
			SqlIdentifier op2 = (SqlIdentifier) operands[1];
			if (op1.isSimple() && op2.isSimple()) {
				left = new String[] { op1.getSimple() };
				right = new String[] { op2.getSimple() };
				isSimple = true;
			} else {
				left = new String[] { op1.names.get(0), op1.names.get(1) };
				right = new String[] { op2.names.get(0), op2.names.get(1) };
			}
		}
		if (!isSimple) {
			if (!left[1].equals(right[1])) {
				throw new NotImplementedException("Maybe try joining on the same attribute?");
			}
		}
	}

	public boolean hasConnection(JoinCondition neighbor) {
		if (this.left[0].equals(neighbor.left[0]) && this.left[1].equals(neighbor.left[1])
				|| this.left[0].equals(neighbor.right[0]) && this.left[1].equals(neighbor.right[1])) {
			return true;
		}
		if (this.right[0].equals(neighbor.left[0]) && this.right[1].equals(neighbor.left[1])
				|| this.right[0].equals(neighbor.right[0]) && this.right[1].equals(neighbor.right[1])) {
			return true;
		}

		return false;
	}


	public static String findCycleImproved(List<JoinCondition> graph, SqlQueryMeta query) {
		if (query.getFromTables().size() == 2) {
			if (graph.size() >= 1) {
				// so if we have 2 tables and actually the graph is bigger than
				// one we will definitely have a joining attribute.
				return graph.get(0).left[1]; // pick any since left[1] ==
												// right[1]
			}
		} else {
			int count = 1; // I already have a connections //TODO probably zero?
			String directJoin = "";
			for (int i = 0; i < graph.size(); i++) {
				JoinCondition node = graph.get(i);
				for (int j = i; j < graph.size(); j++) {
					JoinCondition checkNode = graph.get(j);
					if (node != checkNode) {
						if (node.hasConnection(checkNode)) {
							count++;
							directJoin = node.left[1];
						}
					}
				}
				if (count == query.getFromTables().size() - 1) {
					return directJoin;
				}
			}
			log.info("count is " + count);
		}
		return "";
	}
}
