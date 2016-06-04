package stmtparser;

import java.util.Arrays;
import java.util.List;

public class Operations {
	public enum Op{ CAT, MUL, INT, NOT, REV, UNI, XOR, STAR, NEW, TODFA, DUMP, MIN, PRINT, DIF, DRAW, FROMREGEX, TEST};
	public static List<String> strings  = Arrays.asList(".", "x", "int", "not", "rev", "+", "xor", "star", "new", "dfa", "dump", "min", "print", "-", "draw", "fromRegex", "test");
	public static Op toOp(String s){
		return Op.values()[strings.indexOf(s)];
	}
	public static String toString(Op op){
		for(int i = 0; i < Op.values().length; i++){
			if(Op.values()[i].equals(op))
				return strings.get(i);
		}
		return "";
	}
	/*public static boolean isUnary(Op op){
		return op == Op.NOT || op == Op.REV || op == Op.STAR || op == Op.NEW || op == Op.TODFA || op == Op.MIN || op == Op.PRINT;
	}*/
	public static boolean isBinary(Op op){
		return op == Op.CAT ||op == Op.MUL ||op == Op.INT ||op == Op.UNI ||op == Op.XOR ||op == Op.DUMP || op == Op.TEST;
	}
}
