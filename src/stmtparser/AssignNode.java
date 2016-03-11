package stmtparser;

public class AssignNode extends SimpleNode{
	public AssignNode(StmtParser p, int i) {
		super(p, i);
	}
	public AssignNode(int i) {
		super(i);
	}
	public String varName = null;
	public String toString() { 
		return StmtParserTreeConstants.jjtNodeName[id] + " " + varName + ((decl)? " decl" : "");
	}
	public boolean decl = false;
	public boolean isDecl(){
		return decl;
	}
}