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
	String[] joinOnTablesDirect;
	List<JoinCondition> graph;
	List<List<JoinCondition>> possibleRepartitions;
	List<List<JoinCondition>> repartitions;
	int partitionGrade;
	int currentGrade;

	public PartitionManager(List<JoinCondition> graph, SqlQueryMeta query) {
		this.graph = graph;
		this.query = query;
		this.partitionGrade = query.getFromTables().size();
		this.currentGrade = 0;
		this.possibleRepartitions = new ArrayList<>();
		this.masterPartition = detectMasterPartition();
		if (masterPartition.equals("") && query.OPMODE!=null) {

			// findMostProminentRepartition();
			// findMostProminentPartition();
			repartitions = new ArrayList<>();
			int i = 1;
			int step = 0;
			do {
				findMostProminent(partitionGrade - i);
				i++;
			} while (partitionGrade - i >= 1);

			joinOnTablesDirect = new String[repartitions.size() - 1];
			for (int k = 1; k < repartitions.size(); k++) {
				joinOnTablesDirect[k - 1] = repartitions.get(k).get(0).joinAttribute;
			}
		}
	}

	private void findMostProminent(int size) {
		List<List<JoinCondition>> toBeRemoved = new ArrayList<>();
		for (List<JoinCondition> possible : possibleRepartitions) {
			if (possible.size() == size) {
				// if it fits I sits
				Set<String> tablesContained = new HashSet<>(); // witch tables
																// are present?
				for (List<JoinCondition> repInner : repartitions) {
					for (JoinCondition elem : repInner) {
						for (String tbl : elem.tables)
							tablesContained.add(tbl);
					}
				}

				boolean addCandidate = true;
				for (JoinCondition candidate : possible) {
					if (tablesContained.contains(candidate.getLeft()[0])
							&& tablesContained.contains(candidate.getRight()[0])) {
						addCandidate = false;
						break;
					}
				}
				if (addCandidate) {
					repartitions.add(possible);
					// return ;
				}

			}
		}
	}

	private String detectMasterPartition() {
		if (graph.size() != 0) {
			// I already have a connections //TODO probably zero?
			String directJoin = "";
			for (JoinCondition node : graph) {
				int count = 1;
				List<JoinCondition> local = new ArrayList<>();
				List<JoinCondition> local2 = new ArrayList<>();
				local2.add(node);
				possibleRepartitions.add(local2);
				for (int j = 0; j < graph.size(); j++) {
					JoinCondition checkNode = graph.get(j);

					if (node != checkNode && node.hasConnection(checkNode)) {
						count++;
						local.add(checkNode);
						directJoin = node.getLeft()[1];
						local2 = new ArrayList<>();
						local2.add(node);
						local2.add(checkNode);
						possibleRepartitions.add(local2);
					}
				}
				local.add(node);
				if (count == query.getFromTables().size() - 1) { // consider
					directJoin = node.getLeft()[1];											// moving
					return directJoin;
				}
				
				if (local.size() > 0) {
					possibleRepartitions.add(local);
				}
			}
		}
		
		return "";
	}

	// NOT USED
	private void findMostProminentRepartition() {
		// TODO not working correctly
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

	// NOT USED
	private void findMostProminentPartition() {
		repartitions = new ArrayList<>();
		int max = query.getFromTables().size(), currmax = 0;

		for (int i = max - 1; i > 0; i--) {
			for (List<JoinCondition> possible : possibleRepartitions) {
				if (possible.size() == i) { // biggestsubset
					if (repartitions.size() == 0) { // no repartition available
													// yet
						repartitions.add(possible);
						i -= possible.size();
					} else {
						if (possible.size() - i >= 1) {
							// is this repartition the most prominent?
							// i.e. its tables should not be contained in the
							// repartitions
							Set<String> tablesContained = new HashSet<>();
							for (List<JoinCondition> repInner : repartitions) {
								for (JoinCondition elem : repInner) {
									for (String tbl : elem.tables)
										tablesContained.add(tbl);
								}
							}
							// for each condition to be valid each Join
							// condition
							// should have at least one right/left not present
							// in
							// the repartitions
							boolean addCandidate = true;

							for (JoinCondition candidate : possible) {
								if (tablesContained.contains(candidate.getLeft()[0])
										&& tablesContained.contains(candidate.getRight()[0])) {
									addCandidate = false;
									break;
								}
							}
							if (addCandidate) {
								repartitions.add(possible);
								i -= possible.size();
							}
						}
					}
				}
			}

		}
	}

}
