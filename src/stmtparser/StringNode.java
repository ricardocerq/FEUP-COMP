package stmtparser;

public class StringNode extends SimpleNode{
	public StringNode(StmtParser p, int i) {
		super(p, i);
	}
	public StringNode(int i) {
		super(i);
	}
	private String string = null;
	public String toString() { 
		return StmtParserTreeConstants.jjtNodeName[id] + " " + string;
	}
	public void setString(String s){
		string = s.substring(1, s.length()-1);
	}
	public String getString(){
		return string;
	}
}
