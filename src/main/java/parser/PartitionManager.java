package parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.calcite.sql.SqlBasicCall;

public class PartitionManager {
	Stack<JoinCondition> stack;
	SqlQueryMeta query;
	String masterPartition;
	List<List<JoinCondition>> possibleRepartitions;
	List<List<JoinCondition>> repartitions;

	public PartitionManager(Stack<JoinCondition> graph, SqlQueryMeta query) {
		this.stack = graph;
		this.query = query;
		this.possibleRepartitions = new ArrayList<>();
		this.masterPartition = detectMasterPartition();
		if (masterPartition.equals("")) {

			findMostProminentRepartition();
		}

	}

	private void findMostProminentRepartition() {
		//TODO not working correctly
		repartitions = new ArrayList<>();
		int max = query.getFromTables().size(), curr = 0;
		for (int i = max; i > 0; i--) {
			for (List<JoinCondition> inner : possibleRepartitions) {
				if (inner.size() == i) {
					if (repartitions.size() != 0) {
						boolean contained = false;
						for (List<JoinCondition> repInner : repartitions) {
							if (repInner.get(0).hasConnection(inner.get(0))) {
								contained = true;
								// curr++;
								break;
							}
						}
						if (!contained && max - curr - i > 0) {
							boolean axio = false;
							Set<String> tablesContained = new HashSet<>();
							for (List<JoinCondition> repInner : repartitions) {
								for (JoinCondition elem : repInner) {
									for (String tbl : elem.tables)
										tablesContained.add(tbl);
								}
							}
							for (JoinCondition e : inner) {
								if (!tablesContained.contains(e.getRight()[0])
										|| !tablesContained.contains(e.getLeft()[0])) {
									axio = true;
									// curr+=i;
									// repartitions.add(inner);
								}
							}
							if (axio) {
								curr += i;
								repartitions.add(inner);
								possibleRepartitions.remove(inner);
								break;
							}
						}
					} else {
						repartitions.add(inner);
						possibleRepartitions.remove(inner);
						curr += i;
						break;
					}
				}
			}
			if (curr == max - 1) {
				return;
			}
		}

	}

	private String detectMasterPartition() {
		if (stack.size() != 0) {
			int count = 1; // I already have a connections //TODO probably zero?
			String directJoin = "";
			List<JoinCondition> local = new ArrayList<>();
			JoinCondition node = stack.pop();
			List<JoinCondition> local2 = new ArrayList<>();
			local2.add(node);
			possibleRepartitions.add(local2);
			for (int j = 0; j < stack.size(); j++) {
				JoinCondition checkNode = stack.elementAt(j);
				if (node.hasConnection(checkNode)) {
					count++;
					local.add(checkNode);
					directJoin = node.getLeft()[1];
				}
			}
			local.add(node);
			if (count == query.getFromTables().size() - 1) { // consider moving
				return directJoin;
			}
			if (local.size() > 0) {
				possibleRepartitions.add(local);
			}
			detectMasterPartition();
		}
		return "";
	}

}
