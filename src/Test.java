import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import stmtparser.StmtParser;


public class Test {
	
	public static void main(String args[]) throws FileNotFoundException{
		FileInputStream expectedResults = new FileInputStream("test/expectedResults.txt");
		String results = "test/results.txt";
		String tests ="test/test.txt";
		StmtParser parser = new StmtParser(tests, results);
		parser.run();
		
		Scanner scannerExpectedResults = new Scanner(expectedResults);
		Scanner scannerResults = new Scanner(new FileInputStream(results));
		
		System.out.println("UNITARY RESULTS");
		int i = 0;
		while(scannerExpectedResults.hasNextLine() && scannerResults.hasNextLine()){
			i++;
			String expectedLine = scannerExpectedResults.nextLine();
			String resultLine = scannerResults.nextLine();
			System.out.print("Test no"+i+" - ");
			if(expectedLine.equals(resultLine)){
				System.out.print("OK\n");
			}else{
				System.out.print("FAILED\n");
			}
		}
	}
}
