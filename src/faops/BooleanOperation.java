package faops;

public interface BooleanOperation {
	
	public interface BinaryBooleanOperation {
		public boolean apply(boolean isFinalA, boolean isFinalB);
	}
	public interface UnaryBooleanOperation {
		public boolean apply(boolean isFinal);
	}
	
	public static BinaryBooleanOperation xor = new BinaryBooleanOperation(){
		@Override
		public boolean apply(boolean isFinalA, boolean isFinalB){
			return isFinalA ^ isFinalB;
		}
	};
	public static BinaryBooleanOperation and = new BinaryBooleanOperation(){
		@Override
		public boolean apply(boolean isFinalA, boolean isFinalB){
			return isFinalA && isFinalB;
		}
	};
	public static BinaryBooleanOperation or = new BinaryBooleanOperation(){
		@Override
		public boolean apply(boolean isFinalA, boolean isFinalB){
			return isFinalA || isFinalB;
		}
	};
	public static UnaryBooleanOperation not = new UnaryBooleanOperation(){
		@Override
		public boolean apply(boolean isFinal){
			return !isFinal;
		}
	};
}
