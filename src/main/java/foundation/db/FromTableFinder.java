package foundation.db;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.FromTable;
import com.foundationdb.sql.parser.ResultColumn;
import com.foundationdb.sql.parser.TableName;
import com.foundationdb.sql.parser.Visitable;
import com.foundationdb.sql.parser.Visitor;

public class FromTableFinder implements Visitor {
    public Visitable visit(Visitable node) throws StandardException {
        if(node instanceof FromTable) {
            FromTable ft = (FromTable)node;
            TableName name = ft.getOrigTableName();
            String alias = ft.getCorrelationName();
            if(name != null) {
                System.out.print(name);
                if(alias != null) {
                    System.out.print(" AS " + alias);
                }
                System.out.println();
            } else if (alias != null) {
                String type = node.getClass().getSimpleName();
                System.out.println(type + " AS " + alias);
            }
            
        }
        
        if (node instanceof ResultColumn) {
			ResultColumn resultColumn = (ResultColumn) node;
			System.out.println(resultColumn.getName());
		}
        
        return null;
    };
    
    @Override
    public boolean visitChildrenFirst(Visitable node) {
        return false;
    }
    
    @Override
    public boolean stopTraversal() {
        return false;
    }
    
    @Override
    public boolean skipChildren(Visitable node) {
        return false;
    }
}