package parser;

import java.util.List;

public class SqlInfo {

    private final String type;
    private final List<String> list;
     
    public SqlInfo(String t, List l){
        this.type = t;
        this.list=l; 
    }
    
    public String getType(){
        return this.type;
    }
    
    public List<String> getList(){
        return this.list;
    }
    
    
}
