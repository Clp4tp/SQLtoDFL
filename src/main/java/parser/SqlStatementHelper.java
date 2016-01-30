package parser;

public class SqlStatementHelper {

    private String type; //general type SELECT, UPDATE, DELETE
    private SqlInfo statementInfo; //THE SELECT PART
    private SqlInfo fromInfo; //THE FROM INFO
    private SqlInfo whereInfo;

    public SqlStatementHelper() {
        
    }
    
    
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public SqlInfo getStatementInfo() {
        return statementInfo;
    }

    public void setStatementInfo(SqlInfo statementInfo) {
        this.statementInfo = statementInfo;
    }

    public SqlInfo getFromInfo() {
        return fromInfo;
    }

    public void setFromInfo(SqlInfo fromInfo) {
        this.fromInfo = fromInfo;
    }

    public SqlInfo getWhereInfo() {
        return whereInfo;
    }

    public void setWhereInfo(SqlInfo whereInfo) {
        this.whereInfo = whereInfo;
    }
    
    
}
