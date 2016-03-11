package fileparser;

public class NodeFactory {
	public static Node jjtCreate(int id) {
		if(id == FileParserTreeConstants.JJTTRANSITION)
			return new TransitionNode(id);
		else if(id == FileParserTreeConstants.JJTSTATE)
			return new StateNode(id);
		else return new SimpleNode(id);
	}
}
