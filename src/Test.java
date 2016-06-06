import stmtparser.StmtParser;


public class Test {
	
	public static void main(String args[]){
		StmtParser parser = new StmtParser("test/test.txt");
		parser.run();
	}
}
