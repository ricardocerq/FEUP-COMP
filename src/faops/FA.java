package faops;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

import faops.BooleanOperation.BinaryBooleanOperation;
import stmtparser.SimpleNode;
import utils.Pair;

public class FA {
	private int initialState;
	private int numStates;
	private Map<String, ArrayList<ArrayList<Integer>>> delta = new HashMap<String, ArrayList<ArrayList<Integer>>>();
	private ArrayList<Integer> finalStates = new ArrayList<Integer>();
	private boolean isDFA;
	public boolean isDFA(){
		return isDFA;
	}

	/**
	 * Analyzes the the FA to ascertain if it is a Deterministic Finite Automaton(DFA).
	 *
	 * @return		true if it is a DFA. Otherwise returns false.
	 */
	public boolean computeIsDFA() {
		Iterator<Entry<String, ArrayList<ArrayList<Integer>>>> it = delta.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<ArrayList<Integer>>> pair = (Map.Entry<String, ArrayList<ArrayList<Integer>>>) it
					.next();
			String t = pair.getKey();
			ArrayList<ArrayList<Integer>> u = pair.getValue();
			if (t.equals("")) {
				for (ArrayList<Integer> a : u) {
					if (a.size() != 0){
						isDFA = false;
						return isDFA;
					}
				}
			}
			for (ArrayList<Integer> a : u) {
				if (a.size() != 1){
					isDFA = false;
					return isDFA;
				}
			}
		}
		isDFA = true;
		return isDFA;
	}
	
	public FA() {

	}
	public FA(FA fa) {
		this.initialState = fa.initialState;
		this.setNumStates(fa.getNumStates());
		fa.delta.forEach(new BiConsumer<String, ArrayList<ArrayList<Integer>>>() {
			@Override
			public void accept(String t, ArrayList<ArrayList<Integer>> u) {
				 ArrayList<ArrayList<Integer>> copy = new ArrayList<ArrayList<Integer>>();
				 for(ArrayList<Integer> a : u){
					 copy.add(new ArrayList<Integer>(a));
				 }
				 FA.this.delta.put(t, copy);
			}
		});
		this.finalStates = new ArrayList<Integer>(fa.finalStates);
	}
	public FA(int initialState, int numStates,
			Map<String, ArrayList<ArrayList<Integer>>> delta,
			ArrayList<Integer> finalStates) {
		this.initialState = initialState;
		this.setNumStates(numStates);
		this.delta = delta;
		this.finalStates = finalStates;
	}

	/**
	 * Set the initial state of the FA
	 *
	 * @param	initial 	new initial state
	 */
	public void setInitialState(int initial) {
		this.initialState = initial;
	}

	/**
	 * Increases the number of states and adds the size of the array of transitions.
	 */
	public void incNumStates() {
		this.setNumStates(this.getNumStates() + 1);
		this.delta.forEach(new BiConsumer<String, ArrayList<ArrayList<Integer>>>() {
			@Override
			public void accept(String t, ArrayList<ArrayList<Integer>> u) {
				 u.add(new ArrayList<Integer>());
			}
		});
	}

	/**
	 * Adds a state as a final state
	 * 
	 * @param	state 	number that represents of the new final state
	 */
	public void addFinalState(int state) {
		this.finalStates.add(state);
	}

	/**
	 * Adds a transition between two states
	 *
	 * @param	symbol 		input of transition necessary to make the transition
	 * @param 	srcState 	source state
	 * @param 	dstState	destiny state
	 */
	public void addTransition(String symbol, int srcState, int dstState) {
		 //System.out.println("Adding transition " + srcState + " -> " + dstState  + " : " + symbol);
		if (delta.containsKey(symbol)) {
			//System.out.println("Contains key");
			while(delta.get(symbol).size()<srcState+1){
				delta.get(symbol).add(new ArrayList<Integer>());
			}
			delta.get(symbol).get(srcState).add(dstState);
		} else {
			//System.out.println("Does not contain key");
			ArrayList<ArrayList<Integer>> states = new ArrayList<ArrayList<Integer>>();
			for (int i = 0; i < getNumStates(); i++) { // should be final number
				states.add(new ArrayList<Integer>());
			}
			states.get(srcState).add(dstState);
			delta.put(symbol, states);
		}
	}
	/**
	 * Sorts in ascending way an array of states and removes duplicates
	 * 
	 * @param 	states 		set of arrays to be ordered
	 * @param 	numStates 	max number of states of the FA
	 */
	public static void sortAndRemoveDups(ArrayList<Integer> states,
			int numStates) {
		int[] statesFound = new int[numStates];
		for (Integer i : states) {
			statesFound[i]++;
		}
		states.clear();
		for (int i = 0; i < statesFound.length; i++) {
			if (statesFound[i] > 0)
				states.add(i);
		}
	}

	/** 
	 * 	Runs the FA and validates the given input
	 *
	 *	@param 	input 	set of strings to be validated by the FA
	 * 	@return 		true if the input is validated by the FA. If not returns false.
	 */
	public boolean process(String[] input) {
		ArrayList<Integer> currentStates = new ArrayList<Integer>();
		currentStates.add(initialState);
		applyInput("", currentStates, currentStates);
		ArrayList<Integer> nextStates = new ArrayList<Integer>();
		ArrayList<Integer> temp = null;
		for (String s : input) {
			applyInput(s, currentStates, nextStates);
			applyInput("", nextStates, nextStates);
			sortAndRemoveDups(nextStates, this.getNumStates());
			if (nextStates.size() == 0)
				return false;
			temp = currentStates;
			currentStates = nextStates;
			nextStates = temp;
			nextStates.clear();
		}

		for (Integer state : currentStates) {
			if (isFinal(state))
				return true;
		}
		return false;
	}

	/**
	 * 	Checks the current set of states and adds to a given set of states the possible destination states given a certain input
	 *  
	 *	@param 	input 			given input to be analyzed
	 * 	@param 	currentState 	current set of states
	 * 	@param 	nextState 		set of possible destinations where the results will be added
	 */
	private void applyInput(String input, ArrayList<Integer> currentState,
			ArrayList<Integer> nextState) {
		ArrayList<ArrayList<Integer>> transitions = delta.get(input);
		if (transitions != null)
			for (int i = 0; i < currentState.size(); i++){// Integer state : currentState) {
				nextState.addAll(transitions.get(currentState.get(i)));
			}
	}

	/**
	 * 	Checks if a given state belongs to the set of final states of the FA	
	 *
	 *	@param 	state 	state given to ascertain
	 *	@return 		true if belongs to the set of final states. If not returns false
	 */
	private boolean isFinal(int state) {
		return finalStates.contains(state);
	}

	/**
	 * 	Checks if the given set of states contains a state that belongs to the set of final states of the FA
	 *
	 *	@param 	states 	set of states to be ascertain
	 * 	@return 		true if there is at least a state that belongs to the set of final states. If not returns false
	 */
	private boolean isFinal(ArrayList<Integer> states) {
		for(Integer i : states){
			if(isFinal(i))
				return true;
		}
		return false;
	}

	/** 
	 * 	Ensures all sets representing transitions have size equal to the number of states.
	 */
	public void finalizeConstruction(){
		delta.forEach(new BiConsumer<String, ArrayList<ArrayList<Integer>>>() {
				@Override
				public void accept(String t, ArrayList<ArrayList<Integer>> u) {
					while(u.size()<getNumStates()){
						u.add(new ArrayList<Integer>());
					}
				}
			});
	}

	/**
	 * 	get the DFA equivalent to this FA
	 * 	@return 	resulting DFA
	 */
	public FA toDFA() {
		FA out = new FA();
		//System.out.println("Starting DFA conversion");
		ArrayList<Integer> currentStates = new ArrayList<Integer>();
		currentStates.add(initialState);
		applyInput("", currentStates, currentStates);
		sortAndRemoveDups(currentStates, this.getNumStates());
		ArrayList<Integer> nextStates = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> createdSetsofStates = new ArrayList<ArrayList<Integer>>();
		createdSetsofStates.add(currentStates);
		out.incNumStates();
		out.setInitialState(0);
		for (int currentState = 0; currentState < createdSetsofStates.size(); currentState++) { 
			final int currentStateCopy = currentState;
			final ArrayList<Integer> states = createdSetsofStates.get(currentState);
			//System.out.println("1))" + states);
			for(Integer i : states){
				if(isFinal(i)){
					out.addFinalState(currentState);
				}
			}
			delta.forEach(new BiConsumer<String, ArrayList<ArrayList<Integer>>>() {
				@Override
				public void accept(String t, ArrayList<ArrayList<Integer>> u) {
					if(t.equals(""))
						return;
					nextStates.clear();
					applyInput(t, states, nextStates);
					applyInput("", nextStates, nextStates);
					sortAndRemoveDups(nextStates, getNumStates());
					//System.out.print("    "+ states + " " + currentStateCopy + " -(" + t + ")->" + nextStates);
					int dstIndex = createdSetsofStates.indexOf(nextStates);
					if(dstIndex == -1){
						//System.out.println(" " + createdSetsofStates.size());
						out.incNumStates();
						out.addTransition(t, currentStateCopy, createdSetsofStates.size());
						createdSetsofStates.add(new ArrayList<Integer>(nextStates));
						//System.out.println("Added state : " + nextStates);
					}else{
						//System.out.println(" " + dstIndex);
						out.addTransition(t, currentStateCopy, dstIndex);
					}
					//System.out.println("2))" + states);
				}
			});
		}
		//System.out.println("Ending DFA conversion");
		out.finalizeConstruction();
		out.isDFA = true;
		return out;
	}

	/**
	 * 	Calculates the super set of the alphabets of the given FAs
	 *	
	 * 	@param 	fas 	set of FAs
	 * 	@return 		the super set of the alphabets	
	 */
	public static ArrayList<String> alphabetSuperset(FA... fas){
		ArrayList<String> alphabet = new ArrayList<String>();
		BiConsumer<String, ArrayList<ArrayList<Integer>>> consumer = new BiConsumer<String, ArrayList<ArrayList<Integer>>>() {
			@Override
			public void accept(String t, ArrayList<ArrayList<Integer>> u) {
				if (!alphabet.contains(t))
					alphabet.add(t);
			}
		};
		for (FA fa : fas) {
			fa.delta.forEach(consumer);
		}
		return alphabet;
	}
	/**
	 * 	Generates the FA from the product of a set of FAs and the sets the final nodes of the generated FA according to a given abstract sintax tree
	 * 	@param 	ast 	abstract sintax tree
	 * 	@param 	fas 	set FAs to be multiplied
	 */
	/*public static FA product(SimpleNode ast, FA... fas) {
		FA out = new FA();
		System.out.println("Starting FA product...");
		// Find superset of all alphabets
		ArrayList<String> alphabet = alphabetSuperset(fas);
		
		ArrayList<ArrayList<ArrayList<Integer>>> createdSetsofStates = new ArrayList<ArrayList<ArrayList<Integer>>>(); 
		ArrayList<ArrayList<Integer>> currentStates = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> nextStates = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < fas.length; i++) {
			ArrayList<Integer> faStates = new ArrayList<Integer>();
			faStates.add(fas[i].initialState);
			currentStates.add(faStates);
			nextStates.add(new ArrayList<Integer>());
		}
		createdSetsofStates.add(currentStates);
		out.incNumStates();
		out.setInitialState(0);
		for (int currentState = 0; currentState < createdSetsofStates.size(); currentState++) { 
			currentStates = createdSetsofStates.get(currentState);
			if(isFinalMultiple(ast, fas, currentStates)){
				out.addFinalState(currentState);
			}
			for(String symbol : alphabet){
				for (ArrayList<Integer> set : nextStates) {
					set.clear();
				}
				for(int fa = 0; fa < fas.length; fa++){
					fas[fa].applyInput(symbol, currentStates.get(fa), nextStates.get(fa));
					fas[fa].applyInput("", nextStates.get(fa), nextStates.get(fa));
					sortAndRemoveDups(nextStates.get(fa), fas[fa].numStates);
				}
				System.out.print("    "+ currentStates + " " + currentState + " -(" + symbol + ")->" + nextStates);
				int dstIndex = createdSetsofStates.indexOf(nextStates);
				if(dstIndex == -1){
					System.out.println(" " + createdSetsofStates.size());
					out.incNumStates();
					out.addTransition(symbol, currentState, createdSetsofStates.size());
					ArrayList<ArrayList<Integer>> saved = new ArrayList<ArrayList<Integer>>();
					for(ArrayList<Integer> a : nextStates){
						saved.add(new ArrayList<Integer>(a));
					}
					createdSetsofStates.add(saved);
					System.out.println("Added state : " + nextStates);
				}else{
					System.out.println(" " + dstIndex);
					out.addTransition(symbol, currentState, dstIndex);
				}
			}
		}
		System.out.println("Ending FA product...");
		out.finalizeConstruction();
		out.isDFA = true;
		return out;
	}*/
	/**
	 * 	Generates the FA resulting from the application of a boolean operation to two FAs
	 * 	@param 	op 		operation to apply
	 * 	@param 	fa1 	first FA
	 *  @param 	fa2 	second FA
	 *  @return  	    resulting FA
	 */
	public static FA product(BinaryBooleanOperation op, FA fa1, FA fa2) {
		FA out = new FA();
		//System.out.println("Starting FA product...");
		if(!fa1.isDFA)
			fa1 = fa1.toDFA();
		if(!fa2.isDFA)
			fa2 = fa2.toDFA();
		FA[] fas = {fa1,fa2};
		// Find superset of all alphabets
		ArrayList<String> alphabet = alphabetSuperset(fas);
		
		ArrayList<ArrayList<ArrayList<Integer>>> createdSetsofStates = new ArrayList<ArrayList<ArrayList<Integer>>>(); 
		ArrayList<ArrayList<Integer>> currentStates = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> nextStates = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < fas.length; i++) {
			ArrayList<Integer> faStates = new ArrayList<Integer>();
			faStates.add(fas[i].initialState);
			currentStates.add(faStates);
			nextStates.add(new ArrayList<Integer>());
		}
		createdSetsofStates.add(currentStates);
		out.incNumStates();
		out.setInitialState(0);
		for (int currentState = 0; currentState < createdSetsofStates.size(); currentState++) { 
			currentStates = createdSetsofStates.get(currentState);
			if(isFinalBinary(op, fas, currentStates)){
				out.addFinalState(currentState);
			}
			for(String symbol : alphabet){
				for (ArrayList<Integer> set : nextStates) {
					set.clear();
				}
				for(int fa = 0; fa < fas.length; fa++){
					fas[fa].applyInput(symbol, currentStates.get(fa), nextStates.get(fa));
					fas[fa].applyInput("", nextStates.get(fa), nextStates.get(fa));
					sortAndRemoveDups(nextStates.get(fa), fas[fa].getNumStates());
				}
				//System.out.print("    "+ currentStates + " " + currentState + " -(" + symbol + ")->" + nextStates);
				int dstIndex = createdSetsofStates.indexOf(nextStates);
				if(dstIndex == -1){
					//System.out.println(" " + createdSetsofStates.size());
					out.incNumStates();
					out.addTransition(symbol, currentState, createdSetsofStates.size());
					ArrayList<ArrayList<Integer>> saved = new ArrayList<ArrayList<Integer>>();
					for(ArrayList<Integer> a : nextStates){
						saved.add(new ArrayList<Integer>(a));
					}
					createdSetsofStates.add(saved);
					//System.out.println("Added state : " + nextStates);
				}else{
					//System.out.println(" " + dstIndex);
					out.addTransition(symbol, currentState, dstIndex);
				}
			}
		}
		//System.out.println("Ending FA product...");
		out.finalizeConstruction();
		out.isDFA = true;
		return out;
	}
	
	private static boolean isFinalBinary(BinaryBooleanOperation op, FA[] fas, ArrayList<ArrayList<Integer>> currentStates) {
		return op.apply(fas[0].isFinal(currentStates.get(0)), fas[1].isFinal(currentStates.get(1)));
	}

	private static boolean isFinalMultiple(SimpleNode ast, FA[] fas,ArrayList<ArrayList<Integer>> states) {
		for(int i = 0; i < fas.length; i++){
			if(!fas[i].isFinal(states.get(i)))
				return false;
		}
		return true;
	}
	public static FA xor(FA fa1, FA fa2) {
		return product(BooleanOperation.xor , fa1, fa2);
	}
	public static FA intersect(FA fa1, FA fa2) {
		return product(BooleanOperation.and , fa1, fa2);
	}
	public static FA union(FA fa1, FA fa2) {
		return product(BooleanOperation.or , fa1, fa2);
	}
	
	/**
	 * 	Returns a string that represents the current FA.
	 * 	@return 	a string that represents the current FA
	 */
	public String toString(){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		this.toDot(ps);
		String out = null;
		try {
			out = os.toString("UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			ps.close();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}

	/** 
	 * 	Exports the current FA to a PrintStream in the .dot format
	 * 	@param 	os 	output stream to which the FA will be exported. 
	 */
	public void toDot(PrintStream os) {
		os.println("digraph {");
		os.println("\trankdir=LR;");
		os.println("\tnode [shape=point,color=white,fontcolor=white]; start;");
		for (int currentState = 0; currentState < getNumStates(); currentState++) {
			if (isFinal(currentState))
				os.print("\tnode [shape=doublecircle, color=black, fontcolor=black]; ");
			else
				os.print("\tnode [shape=circle, color=black, fontcolor=black]; ");
			os.println("q" + currentState + ";");
		}
		os.println("\tstart -> q" + initialState + ";");
		ArrayList<String> symbols = new ArrayList<String>();
		for (int currentState = 0; currentState < getNumStates(); currentState++) {
			for (int dstState = 0; dstState < getNumStates(); dstState++) {
				symbols.clear();
				final int currentCopy = currentState;
				final int dstCopy = dstState;
				delta.forEach(new BiConsumer<String, ArrayList<ArrayList<Integer>>>() {
					@Override
					public void accept(String t, ArrayList<ArrayList<Integer>> u) {
						if (u.get(currentCopy).contains(dstCopy))
							symbols.add(t);
					}
				});
				if (symbols.size() != 0) {
					os.print("\tq" + currentState + " -> q" + dstState
							+ " [label=\"");
					for (int i = 0; i < symbols.size(); i++) {
						if (symbols.get(i).equals(""))
							os.print("!");
						else
							os.print(symbols.get(i));
						if (i != symbols.size() - 1)
							os.print(",");
					}
					os.println("\"];");
				}

			}
		}
		os.println("}");
	}

	/**
	 * 	Returns the FA that results from the junction of the two given FAs
	 * 	@param 	a 	first FA
	 * 	@param 	b 	second FA
	 *	@return 	FA that results of the junction
	 */
	public static FA join(FA a, FA b){
		FA out = new FA();
		out.setNumStates(a.getNumStates() + b.getNumStates());
		/*for(int i = 0; i < a.finalStates.size(); i++){
			out.addFinalState(a.finalStates.get(i));
		}
		for(int i = 0; i < b.finalStates.size(); i++){
			out.addFinalState(b.finalStates.get(i)+a.numStates);
		}*/
		ArrayList<String> alphabet = alphabetSuperset(a,b);
		for(String s : alphabet){
			ArrayList<ArrayList<Integer>> trans = new ArrayList<ArrayList<Integer>>();
			for(int i = 0; i < out.getNumStates(); i++)
				trans.add(new ArrayList<Integer>());
			out.delta.put(s, trans);
		}
		a.delta.forEach(new BiConsumer<String, ArrayList<ArrayList<Integer>>>(){
			@Override
			public void accept(String t, ArrayList<ArrayList<Integer>> u) {
				ArrayList<ArrayList<Integer>> trans = out.delta.get(t);
				for(int i = 0; i < u.size(); i++){
					trans.get(i).addAll(u.get(i));
				}
			}
		});
		b.delta.forEach(new BiConsumer<String, ArrayList<ArrayList<Integer>>>(){
			@Override
			public void accept(String t, ArrayList<ArrayList<Integer>> u) {
				ArrayList<ArrayList<Integer>> trans = out.delta.get(t);
				for(int i = 0; i < u.size(); i++){
					ArrayList<Integer> copy = new ArrayList<Integer>(u.get(i));
					for(int j = 0; j < copy.size(); j++){
						copy.set(j, copy.get(j) + a.getNumStates());
					}
					trans.get(i+a.getNumStates()).addAll(copy);
				}
			}
		});
		out.initialState = a.initialState;
		return out;
	}

	/**
	 *	Concatenates two FAs and returns the resulting FA
	 * 	@param 	a 	first FA
	 * 	@param 	b 	second FA
	 * 	@retunr 	resulting FA
	 */
	public static FA cat(FA a, FA b){
		FA out = join(a,b);
		for(Integer i : a.finalStates){
			out.addTransition("", i, b.initialState + a.getNumStates());
		}
		for(int i = 0; i < b.finalStates.size(); i++){
			out.addFinalState(b.finalStates.get(i)+a.getNumStates());
		}
		return out;
	}

	/**
	 * 	Applies the clean-star operator to the given FA and returns the resulting FA
	 * 	@param 	a 	FA to which the clean-star operator will be applied
	 *	@return 	FA resulting from the operation  
	 */
	public static FA star(FA a){
		FA out = new FA(a);
		for(Integer i : a.finalStates){
			out.addTransition("", i, a.initialState);
		}
		if(!a.isFinal(a.initialState))
			out.finalStates.add(a.initialState);
		return out;
	}

	/** 
	 * 	Performs a quick union between two FAs by adding another inital state
	 * 	and adding epsilon transitions from the new initial state to the inital states of the given FAs 
	 *	
	 * 	@param 	a 	first FA
	 * 	@param 	b 	second FA
	 *	@return 	FA resulting from the operation  
	 */
	public static FA quickUnion(FA a, FA b){
		FA out = FA.join(a,b);
		out.initialState = out.getNumStates();
		out.incNumStates();
		out.addTransition("", out.initialState, a.initialState);
		out.addTransition("", out.initialState, b.initialState+a.getNumStates());
		for(int i = 0; i < a.finalStates.size(); i++){
			out.addFinalState(a.finalStates.get(i));
		}
		for(int i = 0; i < b.finalStates.size(); i++){
			out.addFinalState(b.finalStates.get(i)+a.getNumStates());
		}
		return out;
	}
	/**
	 * 	Generates a minimized FA that matches the current FA
	 * 	@return 	minimized FA
	 * @throws FAException 
	 */
	public FA minimized() throws FAException{
		FA out = new FA();
		FA toMinimize = null;
		if(!this.isDFA())
			throw new FAException("FA must be DFA to be minimized !");
		else toMinimize = this;
		//System.out.println("Minimizing");
		boolean[][] distinct = new boolean[this.getNumStates()][this.getNumStates()];
		
		//initialize matrix
		for (int i = 0; i < toMinimize.getNumStates() - 1; i++) {
			for (int j = i + 1; j < toMinimize.getNumStates(); j++) {
				boolean iFinal = toMinimize.isFinal(i);
				boolean jFinal = toMinimize.isFinal(j);
				if (iFinal != jFinal) {
					distinct[i][j] = true; // if one state is final and the other isn't, they are different
					distinct[j][i] = true;
				}
			}
		}
		boolean changed;
		do {
			changed = false;
			for (int i = 0; i < toMinimize.getNumStates() - 1; i++) {
				for (int j = i + 1; j < toMinimize.getNumStates(); j++) {
					if (!distinct[i][j]) {
						Iterator<Entry<String, ArrayList<ArrayList<Integer>>>> it = delta
								.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry<String, ArrayList<ArrayList<Integer>>> pair = (Map.Entry<String, ArrayList<ArrayList<Integer>>>) it
									.next();
							String t = pair.getKey();
							ArrayList<ArrayList<Integer>> u = pair.getValue();
							if (distinctSets(u.get(i), u.get(j), distinct)) { //if states lead to states that are different on a given symbol, they are themselves different
								//System.out.println("States " + i + " and " + j + " are different\n");
								distinct[i][j] = true;
								distinct[j][i] = true;
								changed = true;
								break;
							}
						}
					}
				}
			}
		} while (changed); // stop when no changes made
		
		ArrayList<Pair<Pair<Integer, Integer>, ArrayList<Integer>>> equivalentSets = new ArrayList<>();
		
		for (int i = 0; i < toMinimize.getNumStates(); i++) {
			int equivalent = 0;
			for (; equivalent < equivalentSets.size(); equivalent++) {
				if (!distinct[i][equivalentSets.get(equivalent).left.right]) 
					break;		
			}
			if (equivalent == equivalentSets.size()) { // not found
				//System.out.println("Adding state " + i + " as state " + out.numStates);
				ArrayList<Integer> temp = new ArrayList<Integer>();
				temp.add(i);
				equivalentSets.add(new Pair<Pair<Integer, Integer>, ArrayList<Integer>>(new Pair<Integer, Integer>(out.getNumStates(), i), temp));
				out.incNumStates();
			} else {
				equivalentSets.get(equivalent).right.add(i);
			}
		}
	
		Iterator<Entry<String, ArrayList<ArrayList<Integer>>>> it = delta.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<ArrayList<Integer>>> pair = (Map.Entry<String, ArrayList<ArrayList<Integer>>>) it.next();
			String t = pair.getKey();
			ArrayList<ArrayList<Integer>> u = pair.getValue();
			for(int i = 0; i < u.size(); i++){
				int newi = findNewState(i,equivalentSets);
				if(newi == -1){
					continue;
				}
				for(int j = 0; j < u.get(i).size(); j++){
					int newj = findNewState(u.get(i).get(j),equivalentSets);
					if(newj == -1){
						continue;
					}
					out.addTransition(t, newi, newj);
				}
			}
		}
		for (int i = 0; i < equivalentSets.size(); i++) {
			if (toMinimize.isFinal(equivalentSets.get(i).left.right))
				out.addFinalState(equivalentSets.get(i).left.left);
		}
		out.finalizeConstruction();
		return out;
	}
	
	/*public FA removeUnreachable() {
		
	}
	
	public FA removeStates(ArrayList<Integer> list){
		FA out = new FA();
		ArrayList<Pair<Integer, Integer>> mapping = new ArrayList<>();
		int currentNum = 0;
		for(int i = 0; i < this.numStates; i++){
			if(list.contains(i)){
				mapping.add(new Pair<Integer, Integer>(i, -1));
				currentNum++;
			} else {
				mapping.add(new Pair<Integer, Integer>(i, i-currentNum));
			}
		}
	}*/
	
	private int findNewState(int state, ArrayList<Pair<Pair<Integer, Integer>, ArrayList<Integer>>> equivalentSets) {
		for (int j = 0; j < equivalentSets.size(); j++) {
			for (int n = 0; n < equivalentSets.get(j).right.size(); n++) {
				if(state == equivalentSets.get(j).right.get(n))
					return equivalentSets.get(j).left.left;
			}
		}
		return -1;
	}

	/** 
	 *	Verifies if the given sets of states of a DFA are distinct from each other
	 * 	@param 	set1 		first set of states
	 * 	@param 	set2		second set of states
	 *	@param	distinct 	matrix in which for each pair of states tells if they are different or not
 	 */
	private boolean distinctSets(ArrayList<Integer> set1,
			ArrayList<Integer> set2, boolean[][] distinct) {
		if(set1.size() == 0 || set2.size() == 0)
			return false;
		return distinct[set1.get(0)][set2.get(0)];
	}

	/**
	 * 	Generates a FA for a given symbol
	 * 	@param 	symbol 	symbol used to generate the FA
	 * 	@result 		resulting FA  
	 */
	public static FA fromSymbol(String symbol){
		FA out = new FA();
		out.incNumStates();
		out.initialState = 0;
		if(symbol.equals("")){
			out.finalStates.add(out.initialState);
		}else {
			out.incNumStates();
			out.addTransition(symbol, out.initialState, out.getNumStates()-1);
			out.finalStates.add(out.getNumStates()-1);
		}
		return out;
	}

	public FA reverse() {
		FA out = new FA();
		out.initialState = 0;
		out.incNumStates();
		out.setNumStates(out.getNumStates() + this.getNumStates());
		Iterator<Entry<String, ArrayList<ArrayList<Integer>>>> it = delta.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<ArrayList<Integer>>> pair = (Map.Entry<String, ArrayList<ArrayList<Integer>>>) it.next();
			String t = pair.getKey();
			ArrayList<ArrayList<Integer>> u = pair.getValue();
			for(int i = 0; i < u.size(); i++){
				for(int j = 0; j < u.get(i).size(); j++){
					out.addTransition(t, u.get(i).get(j)+1, i+1);
				}
			}
		}
		out.addFinalState(this.initialState+1);
		for(int i = 0; i < this.finalStates.size(); i++){
			out.addTransition("", 0, this.finalStates.get(i) + 1);
		}
		return out;
	}

	public FA not() {
		FA toconvert = null;
		if(!this.isDFA)
			toconvert = this.toDFA();
		else toconvert = this;
		FA out = new FA(toconvert);
		out.finalStates.clear();
		for(int i = 0; i < out.getNumStates(); i++){
			if(!toconvert.isFinal(i))
				out.finalStates.add(i);
		}
		return out;
	}

	public int getNumStates() {
		return numStates;
	}

	public void setNumStates(int numStates) {
		this.numStates = numStates;
	}
}





