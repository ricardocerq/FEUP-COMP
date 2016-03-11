package fileparser;

public class StateNode extends SimpleNode{
	public StateNode(FileParser p, int i) {
		super(p, i);
	}
	public StateNode(int i) {
		super(i);
	}
	public String name;
	public boolean isFinalState;
	public boolean isStartState;
	public String toString() { 
		return FileParserTreeConstants.jjtNodeName[id] + " " + name + " " + (isStartState ? "Start" : (isFinalState ? "Final" : ""));
	}
	public String toString(String prefix) { return prefix + toString(); }
}
