package stmtparser;


public class StmtNodeFactory {
	public static Node jjtCreate(int id) {
		if(id == StmtParserTreeConstants.JJTOP)
			return new OpNode(id);
		else if(id == StmtParserTreeConstants.JJTSYMBOL)
			return new SymNode(id);
		else if(id == StmtParserTreeConstants.JJTASSIGN)
			return new AssignNode(id);
		else if(id == StmtParserTreeConstants.JJTSTRING)
			return new StringNode(id);
		else return new SimpleNode(id);
	}
}

