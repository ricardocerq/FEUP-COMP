package stmtparser;


public class SymNode extends SimpleNode{
	public SymNode(StmtParser p, int i) {
		super(p, i);
	}
	public SymNode(int i) {
		super(i);
	}
	public String varName;
	public String toString() { 
		return StmtParserTreeConstants.jjtNodeName[id] + " " + varName;
	}
}
