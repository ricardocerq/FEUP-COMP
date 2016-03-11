package stmtparser;


public class OpNode extends SimpleNode{
	public OpNode(StmtParser p, int i) {
		super(p, i);
	}
	public OpNode(int i) {
		super(i);
	}
	public Operations.Op op = null;
	public boolean binary = false;
	
	public String toString() { 
		return StmtParserTreeConstants.jjtNodeName[id]  + ((op!=null)? " " + Operations.toString(op) : "");
	}
	public void setOp(String op){
		this.op = Operations.toOp(op);
		this.binary = Operations.isBinary(this.op);
	}
	public boolean isBinary(){
		return binary;
	}
}