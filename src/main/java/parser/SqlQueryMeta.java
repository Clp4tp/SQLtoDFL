package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class SqlQueryMeta {
    private static Logger log = LoggerFactory.getLogger(SqlQueryMeta.class);
    private static final String[] functions = { "count", "avg", "min", "max" };
    private final SqlSelect call;
    private boolean hasGroupBy;
    private boolean hasOrderBy;
    private boolean hasWhere;
    private boolean hasHaving;
    private SqlNode from;
    private SqlNode where;
    private SqlNodeList select;
    private SqlNodeList orderby;
    private SqlNodeList groupby;
    public List<String[]> selectIdentifiers;
    public List<String[]> whereIdentifiers;
    public List<String[]> groupByIdentifiers;
    private ArrayList<String> fromTables;
    private List<SqlBasicCall> joinOperations;
    private Multimap<String, String> aliasMap = HashMultimap.create();
    private Multimap<String, Multimap<String, String>> functionsMapPerTable = HashMultimap.create();
    private List<List<String>> functionsToTables = new ArrayList<>();
    // private SqlSimpleNode selectList;

  

	public SqlQueryMeta(SqlSelect call) {
	this.call = call;
	selectIdentifiers = new ArrayList<>();
	hasOrderBy = ((SqlSelect) call).hasOrderBy(); // getGroup();
	hasWhere = ((SqlSelect) call).hasWhere();
	hasGroupBy = (((SqlSelect) call).getGroup() == null) ? false : true;
	hasHaving = (((SqlSelect) call).getHaving() == null) ? false : true;

	from = ((SqlSelect) call).getFrom();
	where = ((SqlSelect) call).getWhere();
	select = ((SqlSelect) call).getSelectList();
	orderby = ((SqlSelect) call).getOrderList();
	groupby = ((SqlSelect) call).getGroup();

	fromTables = new ArrayList<>();
	if (this.from instanceof SqlJoin) {
	    analyzeFrom((SqlJoin) this.from);
	} else if (this.from instanceof SqlIdentifier) {
	    fromTables.add(((SqlIdentifier) from).getSimple());
	}

	// classify the
	// selectList = new SqlSimpleNode();
	// selectList.setOperator(operator);

    }

    /**
     * For each table participating in the sql statement, find its participating
     * fields and export them to a String[][] table. Each row of this table
     * corresponds to the table in use and afterwards its participating fields
     * 
     * @return
     * 
     */
    public Multimap<String, String> findTableParticipatingIdentifiers(List<String[]> clause) {

	Multimap<String, String> filteredData = HashMultimap.create();
	for (String[] row : clause) {
	    if (row.length > 1) // everything contained here is a function such
		filteredData.put(row[0], row[1]);
	}

	return filteredData;
    }

    
    
    public void createDFLStatement(String tableName, List<String> tableIdentifiers) {
	log.info("adwdaw");
	log.info("adwdaw");
    }

    public void analyzeFrom(SqlJoin from) {
	// SqlJoin fr = (SqlJoin) from;
	// String t = fr.getJoinType().name();

	if (from.getLeft() instanceof SqlIdentifier) {
	    fromTables.add(((SqlIdentifier) from.getLeft()).getSimple());
	    fromTables.add(((SqlIdentifier) from.getRight()).getSimple());
	    return;
	}
	analyzeFrom((SqlJoin) from.getLeft());
	fromTables.add(((SqlIdentifier) from.getRight()).getSimple());
    }

    public void findUniqueTableRows() {

    }

    public List<String[]> getWhereIdentifiers() {
	return whereIdentifiers;
    }

    public void setWhereIdentifiers(List<String[]> whereIdentifiers) {
	this.whereIdentifiers = whereIdentifiers;
    }

    public List<String[]> getSelectIdentifiers() {
	return selectIdentifiers;
    }

    public void setSelectIdentifiers(List<String[]> selectIdentifiers) {
	this.selectIdentifiers = selectIdentifiers;
    }

    // public SqlSimpleNode getSelectList() {
    // return selectList;
    // }
    //
    // public void setSelectList(SqlSimpleNode selectList) {
    // this.selectList = selectList;
    // }

    // mundane
    public boolean isHasGroupBy() {
	return hasGroupBy;
    }

    public void setHasGroupBy(boolean hasGroupBy) {
	this.hasGroupBy = hasGroupBy;
    }

    public boolean isHasOrderBy() {
	return hasOrderBy;
    }

    public void setHasOrderBy(boolean hasOrderBy) {
	this.hasOrderBy = hasOrderBy;
    }

    public boolean isHasWhere() {
	return hasWhere;
    }

    public void setHasWhere(boolean hasWhere) {
	this.hasWhere = hasWhere;
    }

    public boolean isHasHaving() {
	return hasHaving;
    }

    public void setHasHaving(boolean hasHaving) {
	this.hasHaving = hasHaving;
    }

    public SqlNode getFrom() {
	return from;
    }

    public void setFrom(SqlNode from) {
	this.from = from;
    }

    public SqlNode getWhere() {
	return where;
    }

    public void setWhere(SqlNode where) {
	this.where = where;
    }

    public SqlNodeList getSelect() {
	return select;
    }

    public void setSelect(SqlNodeList select) {
	this.select = select;
    }

    public SqlNodeList getOrderby() {
	return orderby;
    }

    public void setOrderby(SqlNodeList orderby) {
	this.orderby = orderby;
    }

    public SqlNodeList getGroupby() {
	return groupby;
    }

    public void setGroupby(SqlNodeList groupby) {
	this.groupby = groupby;
    }

    public ArrayList<String> getFromTables() {
	return fromTables;
    }

    public void setFromTables(ArrayList<String> fromTables) {
	this.fromTables = fromTables;
    }

    public SqlSelect getCall() {
	return call;
    }

    public List<String[]> getGroupByIdentifiers() {
	return groupByIdentifiers;
    }

    public void setGroupByIdentifiers(List<String[]> groupByIdentifiers) {
	this.groupByIdentifiers = groupByIdentifiers;
    }

    public Multimap<String, String> getAliasMap() {
	return aliasMap;
    }

    public void setAliasMap(Multimap<String, String> aliasMap) {
	this.aliasMap = aliasMap;
    }

    public Multimap<String, Multimap<String, String>> getFunctionsMapPerTable() {
	return functionsMapPerTable;
    }

    public void setFunctionsMapPerTable(Multimap<String, Multimap<String, String>> functionsMap) {
	this.functionsMapPerTable = functionsMap;
    }

    public void setJoinOperations(List<SqlBasicCall> joinOperations) {
        this.joinOperations = joinOperations;
    }

    public List<SqlBasicCall> getJoinOperations() {
	return joinOperations;
    }

    public List<List<String>> getFunctionsToTables() {
  		return functionsToTables;
  	}

  	public void setFunctionsToTables(List<List<String>> functionsToTables) {
  		this.functionsToTables = functionsToTables;
  	}
}
