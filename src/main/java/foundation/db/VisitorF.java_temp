package foundation.db;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.FromList;
import com.foundationdb.sql.parser.ResultColumn;
import com.foundationdb.sql.parser.SelectNode;
import com.foundationdb.sql.parser.Visitable;
import com.foundationdb.sql.parser.Visitor;

public class VisitorF implements Visitor {

	@Override
	public boolean skipChildren(Visitable node) throws StandardException {
		if (node instanceof FromList) {
			return true;
		}
		return false;
	}

	@Override
	public boolean stopTraversal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Visitable visit(Visitable node) throws StandardException {
		if (node instanceof ResultColumn) {
			ResultColumn resultColumn = (ResultColumn) node;
			System.out.println(resultColumn.getName());
		}
		return null;
	}

	@Override
	public boolean visitChildrenFirst(Visitable node) {
		if (node instanceof SelectNode)
			return true;
		return false;
	}
	

}
