package fileparser;

import java.util.ArrayList;

public class StateNode extends SimpleNode{
	public StateNode(FileParser p, int i) {
		super(p, i);
	}
	public StateNode(int i) {
		super(i);
	}
	public ArrayList<String> names = new ArrayList<>();
	public boolean isFinalState;
	public boolean isStartState;
	public String toString() { 
		String out =  FileParserTreeConstants.jjtNodeName[id] + " ";
		for(String s : names){
			out += s + " ";
		}
		return out + (isStartState ? "Start" : (isFinalState ? "Final" : ""));
	}
	public String toString(String prefix) { return prefix + toString(); }
	public void addState(String state){
		names.add(StateNode.removeQuotes(state));
	};
	public static String removeQuotes(String or){
		if(or.charAt(0) == '\"' && or.charAt(or.length()-1) == '\"'){
			return or.substring(1, or.length()-1);
		} else return or;
	}
}
