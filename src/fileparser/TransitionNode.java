package fileparser;

import java.util.ArrayList;
import java.util.List;

public class TransitionNode extends SimpleNode{

	public TransitionNode(FileParser p, int i) {
		super(p, i);
	}
	public TransitionNode(int i) {
		super(i);
	}
	public void addTransition(String symbol){
		if(symbol.equals("!"))
			symbols.add("");
		else symbols.add(symbol);
	}
	public String src;
	public String dst;
	public List<String> symbols = new ArrayList<String>();
	public String toString() { return FileParserTreeConstants.jjtNodeName[id] + " " + src + " -> " + dst + " : " + symbols.toString(); }
	public String toString(String prefix) { return prefix + toString(); }
}
