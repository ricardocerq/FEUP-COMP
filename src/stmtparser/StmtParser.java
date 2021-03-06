/* Generated By:JJTree&JavaCC: Do not edit this line. StmtParser.java */
package stmtparser;
import faops.FA;
import fileparser.FileParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import gui.FADrawer;
import regexparser.RegexParser;
import java.util.Scanner;
import java.util.NoSuchElementException;

public class StmtParser/*@bgen(jjtree)*/implements StmtParserTreeConstants, StmtParserConstants {/*@bgen(jjtree)*/
  protected static JJTStmtParserState jjtree = new JJTStmtParserState();private static Map<String, FA> vars = new HashMap< String, FA>();
  private static int drawNumber = 0;

  private InputStream in;
  public StmtParser(String file) {

        try {
                if(file != null){
                        this.in = new FileInputStream(file);
                }else{
                        this.in = System.in;
                }
        }catch(FileNotFoundException e) {
                System.out.println(e.getMessage());
                this.in = System.in;
        }
  }

  public StmtParser() {
        this.in = System.in;
  }

  public void run(){
        new StmtParser(in);
    new FileParser(in);
    new RegexParser(in);
    while (true)
    {
      try
      {
        System.out.println("Reading...");
        SimpleNode n = StmtParser.Root();
        if(n == null)
        {
                System.out.println("Finished Evaluating");
                break;
        }
        System.out.println("Expressions:");
        n.dump("");
        System.out.println("\u005cn");
                eval(n);
        //StmtParser.ReInit(in);      }
      catch (Throwable e)
      {
        System.out.println("[ERROR] - " + e.getMessage());
        StmtParser.ReInit(in);
        try {
                        System.in.skip(1000);
                } catch (IOException e1) {
                        e1.printStackTrace();
                }
      }
    }
  }

  public static void main(String args [])
  {
        StmtParser parser;
        if(args.length > 0)
                parser = new StmtParser(args[0]);
        else
                parser = new StmtParser();
        parser.run();

  }
  public static FileInputStream getFile(String filename)  throws FileNotFoundException
  {
        return new FileInputStream(new File("files/" + filename));
  }
  public static PrintStream outFile(String filename) throws FileNotFoundException
  {
        return new PrintStream(new FileOutputStream("files/" + filename, false));
  }
  public FA eval(SimpleNode node) throws Exception
  {
    FA out = null;
    switch(node.id){
                case StmtParserTreeConstants.JJTASSIGN:
                        AssignNode an = (AssignNode) node;
                        if (an.isDecl()) {
                                if(vars.get(an.varName) != null)
                                        throw new EvalException("Variable " + an.varName + " was redeclared");
                        }
                        out = eval((SimpleNode)node.jjtGetChild(0));
                        vars.put(an.varName, out);
                        break;
                case StmtParserTreeConstants.JJTOP:
                        OpNode on = (OpNode) node;
                        if(on.isBinary()){
                                SimpleNode lhs = (SimpleNode) node.jjtGetChild(0);
                                SimpleNode rhs = (SimpleNode) node.jjtGetChild(1);
                                if(on.op == Operations.Op.DUMP){
                                        out = eval(lhs);
                                        try {
                                                out.toDot(outFile(((StringNode)rhs).getString()));
                                        } catch (FileNotFoundException e) {
                                                throw new EvalException("Could not write to file " + ((StringNode)rhs).getString() + ": " + e.getMessage());
                                        }
                                } else if(on.op == Operations.Op.TEST){
                                        out = eval(lhs);
                    String f = ((StringNode)rhs).getString();
                     InputStream is = null;
                     if(f.equals("stdin"))
                          is = this.in;
                     else is = getFile(f);
                     System.out.println("Testing ($ to end)");
                     Scanner scanner = new Scanner(is);
                     String s;
                     try{
                             while((s = scanner.nextLine()) != null && !s.equals("$")){
                                  System.out.println(out.process(s.split(""))? "yes": "no");
                             }
                     } catch(NoSuchElementException e){

                     }
                     System.out.println("Finished Testing");
                                }
                                else if(on.op == Operations.Op.UNI){
                                        FA fa = eval(lhs);
                                        FA fb = eval(rhs);
                                        out = faops.FA.union(fa,fb);
                                } else if(on.op == Operations.Op.CAT){
                                        FA fa = eval(lhs);
                                        FA fb = eval(rhs);
                                        out = faops.FA.cat(fa,fb);
                                } else if(on.op == Operations.Op.INT){
                                        FA fa = eval(lhs);
                                        FA fb = eval(rhs);
                                        out = faops.FA.intersect(fa,fb);
                                } else if(on.op == Operations.Op.XOR){
                                    FA fa = eval(lhs);
                                        FA fb = eval(rhs);
                                        out = faops.FA.xor(fa,fb);
                                }
                        }
                        else {
                          SimpleNode arg = (SimpleNode) node.jjtGetChild(0);
                          if(on.op == Operations.Op.NEW){
                             try {
                                                FileParser.ReInit(getFile(((StringNode)arg).getString()));
                                                try {
                                                        out = FileParser.toFa(FileParser.Start());
                                                } catch (fileparser.ParseException e) {
                                                        throw new EvalException("Could not parse file " + ((StringNode)arg).getString() + " " + e.getMessage());
                                                }
                                        } catch (FileNotFoundException e) {
                                                throw new EvalException("Could not open file " + ((StringNode)arg).getString());
                                        }
                          } else if(on.op == Operations.Op.FROMREGEX){
                            try {
                        out = RegexParser.parseString(((StringNode)arg).getString());
                } catch(Exception e) {
                        throw new EvalException("Error in Regex " + ((StringNode)arg).getString());
                }
                          } else if(on.op == Operations.Op.PRINT){
                            out = eval(arg);
                            System.out.println(out);
                          } else if(on.op == Operations.Op.DRAW){
                            out = eval(arg);
                            new FADrawer(out, ++drawNumber);
                          } else if(on.op == Operations.Op.STAR){
                            out = faops.FA.star(eval(arg));
                          } else if(on.op == Operations.Op.REV){
                            out = eval(arg).reverse();
                          } else if(on.op == Operations.Op.NOT){
                            out = eval(arg).not();
                          } else if(on.op == Operations.Op.TODFA){
                            out = eval(arg).toDFA();
                          } else if(on.op == Operations.Op.MIN){
                            out = eval(arg).minimized();
                          }
                        }
                        break;
                case StmtParserTreeConstants.JJTSTRING:
                        StringNode sn = (StringNode) node;
                        break;
                case StmtParserTreeConstants.JJTSYMBOL:
                        SymNode syn = (SymNode) node;
                        out = vars.get(syn.varName);
                        if(out == null)
                                throw new EvalException("Variable " + syn.varName + " does not exist");
                        break;
                default:
                        for(int i = 0; i < node.jjtGetNumChildren(); i++){
                          try
                  {
                          eval((SimpleNode)node.jjtGetChild(i));
                          }
                          catch (Throwable e)
                      {
                        System.out.println("[ERROR] - " + e.getMessage());
                      }
                        }
    }
        return out;
  }

  static final public SimpleNode Root() throws ParseException {
 /*@bgen(jjtree) Root */
 SimpleNode jjtn000 = (SimpleNode)StmtNodeFactory.jjtCreate(JJTROOT);
 boolean jjtc000 = true;
 jjtree.openNodeScope(jjtn000);Token t = null;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 0:
        t = jj_consume_token(0);
        break;
      case OPEN:
      case NEW:
      case FROMREGEX:
      case REV:
      case NOT:
      case STAR:
      case FA:
      case TODFA:
      case MIN:
      case PRINT:
      case DRAW:
      case SYM:
      case ENDL:
        label_1:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case ENDL:
            ;
            break;
          default:
            jj_la1[0] = jj_gen;
            break label_1;
          }
          jj_consume_token(ENDL);
        }
        label_2:
        while (true) {
          Stmt();
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case OPEN:
          case NEW:
          case FROMREGEX:
          case REV:
          case NOT:
          case STAR:
          case FA:
          case TODFA:
          case MIN:
          case PRINT:
          case DRAW:
          case SYM:
            ;
            break;
          default:
            jj_la1[1] = jj_gen;
            break label_2;
          }
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case ENDL:
          jj_consume_token(ENDL);
          break;
        case 0:
          jj_consume_token(0);
          break;
        default:
          jj_la1[2] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[3] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
    {if (true) return t==null? jjtn000 : null;}
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
    throw new Error("Missing return statement in function");
  }

  static final public boolean Stmt() throws ParseException {
                       String decl = null;
    try {
     SimpleNode jjtn001 = (SimpleNode)StmtNodeFactory.jjtCreate(JJTASSIGN);
     boolean jjtc001 = true;
     jjtree.openNodeScope(jjtn001);
      try {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case FA:
          decl = Decl();
          break;
        default:
          jj_la1[4] = jj_gen;
          ;
        }
        Expression0();
                                    jjtree.closeNodeScope(jjtn001,  decl != null);
                                    jjtc001 = false;
                                   ((AssignNode)jjtn001).varName = decl; ((AssignNode)jjtn001).decl = true;
      } catch (Throwable jjte001) {
     if (jjtc001) {
       jjtree.clearNodeScope(jjtn001);
       jjtc001 = false;
     } else {
       jjtree.popNode();
     }
     if (jjte001 instanceof RuntimeException) {
       {if (true) throw (RuntimeException)jjte001;}
     }
     if (jjte001 instanceof ParseException) {
       {if (true) throw (ParseException)jjte001;}
     }
     {if (true) throw (Error)jjte001;}
      } finally {
     if (jjtc001) {
       jjtree.closeNodeScope(jjtn001,  decl != null);
     }
      }
      jj_consume_token(END_STMT);
    } catch (ParseException e) {
        skip(END_STMT);
        {if (true) return false;}
    }
 {if (true) return true;}
    throw new Error("Missing return statement in function");
  }

  static final public void Expression0() throws ParseException {
                           String assign = null;
    if (jj_2_1(2)) {
      Assign2();
    } else {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case OPEN:
      case NEW:
      case FROMREGEX:
      case REV:
      case NOT:
      case STAR:
      case TODFA:
      case MIN:
      case PRINT:
      case DRAW:
      case SYM:
        Expression();
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  static final public void Expression() throws ParseException {
                          String op;
    Expression2();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case UN:
      op = BinaryOpTier1();
      Expression();
                                                         SimpleNode jjtn001 = (SimpleNode)StmtNodeFactory.jjtCreate(JJTOP);
                                                         boolean jjtc001 = true;
                                                         jjtree.openNodeScope(jjtn001);
      try {
                                                         jjtree.closeNodeScope(jjtn001,  2);
                                                         jjtc001 = false;
                                                         ((OpNode)jjtn001).setOp(op);
      } finally {
                                                         if (jjtc001) {
                                                           jjtree.closeNodeScope(jjtn001,  2);
                                                         }
      }
      break;
    default:
      jj_la1[6] = jj_gen;
      ;
    }
  }

  static final public void Expression2() throws ParseException {
                           String op;
    Expression3();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DOT:
    case INT:
    case XOR:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DOT:
        jj_consume_token(DOT);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DUMP:
        case TEST:
          op = Func();
                                      SimpleNode jjtn001 = (SimpleNode)StmtNodeFactory.jjtCreate(JJTOP);
                                      boolean jjtc001 = true;
                                      jjtree.openNodeScope(jjtn001);
          try {
                                      jjtree.closeNodeScope(jjtn001,  2);
                                      jjtc001 = false;
                                     ((OpNode)jjtn001).setOp(op);
          } finally {
                                      if (jjtc001) {
                                        jjtree.closeNodeScope(jjtn001,  2);
                                      }
          }
          break;
        case OPEN:
        case NEW:
        case FROMREGEX:
        case REV:
        case NOT:
        case STAR:
        case TODFA:
        case MIN:
        case PRINT:
        case DRAW:
        case SYM:
          Expression2();
                                                   SimpleNode jjtn002 = (SimpleNode)StmtNodeFactory.jjtCreate(JJTOP);
                                                   boolean jjtc002 = true;
                                                   jjtree.openNodeScope(jjtn002);
          try {
                                                   jjtree.closeNodeScope(jjtn002,  2);
                                                   jjtc002 = false;
                                                   ((OpNode)jjtn002).setOp(".");
          } finally {
                                                   if (jjtc002) {
                                                     jjtree.closeNodeScope(jjtn002,  2);
                                                   }
          }
          break;
        default:
          jj_la1[7] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      case INT:
      case XOR:
        op = BinaryOpTier2();
        Expression2();
                                        SimpleNode jjtn003 = (SimpleNode)StmtNodeFactory.jjtCreate(JJTOP);
                                        boolean jjtc003 = true;
                                        jjtree.openNodeScope(jjtn003);
        try {
                                        jjtree.closeNodeScope(jjtn003,  2);
                                        jjtc003 = false;
                                        ((OpNode)jjtn003).setOp(op);
        } finally {
                                        if (jjtc003) {
                                          jjtree.closeNodeScope(jjtn003,  2);
                                        }
        }
        break;
      default:
        jj_la1[8] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    default:
      jj_la1[9] = jj_gen;
      ;
    }
  }

  static final public void Expression3() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case REV:
    case NOT:
    case STAR:
    case TODFA:
    case MIN:
    case PRINT:
    case DRAW:
      UnaryOp();
      break;
    case SYM:
      Symbol();
      break;
    case OPEN:
      jj_consume_token(OPEN);
      Expression();
      jj_consume_token(CLOSE);
      break;
    case NEW:
    case FROMREGEX:
      New();
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  static final public String Func() throws ParseException {
                       Token op;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DUMP:
      op = jj_consume_token(DUMP);
      break;
    case TEST:
      op = jj_consume_token(TEST);
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(OPEN);
    String();
    jj_consume_token(CLOSE);
          {if (true) return op.image;}
    throw new Error("Missing return statement in function");
  }

  static final public void String() throws ParseException {
                         /*@bgen(jjtree) String */
                         SimpleNode jjtn000 = (SimpleNode)StmtNodeFactory.jjtCreate(JJTSTRING);
                         boolean jjtc000 = true;
                         jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(STRING);
                             jjtree.closeNodeScope(jjtn000, true);
                             jjtc000 = false;
          ((StringNode)jjtn000).setString(t.image);
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  static final public String Decl() throws ParseException {
                      Token t;
    jj_consume_token(FA);
    t = jj_consume_token(SYM);
    jj_consume_token(EQUALS);
    {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

  static final public String Assign() throws ParseException {
                       Token t;
    t = jj_consume_token(SYM);
    jj_consume_token(EQUALS);
    {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

  static final public void Assign2() throws ParseException {
                        /*@bgen(jjtree) Assign */
                        SimpleNode jjtn000 = (SimpleNode)StmtNodeFactory.jjtCreate(JJTASSIGN);
                        boolean jjtc000 = true;
                        jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(SYM);
               ((AssignNode)jjtn000).varName = t.image;
      jj_consume_token(EQUALS);
      Expression();
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  static final public void Symbol() throws ParseException {
                        /*@bgen(jjtree) Symbol */
                        SimpleNode jjtn000 = (SimpleNode)StmtNodeFactory.jjtCreate(JJTSYMBOL);
                        boolean jjtc000 = true;
                        jjtree.openNodeScope(jjtn000);Token t;
    try {
      t = jj_consume_token(SYM);
    jjtree.closeNodeScope(jjtn000, true);
    jjtc000 = false;
        ((SymNode)jjtn000).varName = t.image;
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  static final public void UnaryOp() throws ParseException {
                     /*@bgen(jjtree) Op */
                     SimpleNode jjtn000 = (SimpleNode)StmtNodeFactory.jjtCreate(JJTOP);
                     boolean jjtc000 = true;
                     jjtree.openNodeScope(jjtn000);Token t;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case REV:
        t = jj_consume_token(REV);
        break;
      case NOT:
        t = jj_consume_token(NOT);
        break;
      case STAR:
        t = jj_consume_token(STAR);
        break;
      case MIN:
        t = jj_consume_token(MIN);
        break;
      case TODFA:
        t = jj_consume_token(TODFA);
        break;
      case PRINT:
        t = jj_consume_token(PRINT);
        break;
      case DRAW:
        t = jj_consume_token(DRAW);
        break;
      default:
        jj_la1[12] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    ((OpNode)jjtn000).setOp(t.image);
      jj_consume_token(OPEN);
      Expression0();
      jj_consume_token(CLOSE);
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  static final public void New() throws ParseException {
                 /*@bgen(jjtree) Op */
                 SimpleNode jjtn000 = (SimpleNode)StmtNodeFactory.jjtCreate(JJTOP);
                 boolean jjtc000 = true;
                 jjtree.openNodeScope(jjtn000);Token t;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NEW:
        t = jj_consume_token(NEW);
        break;
      case FROMREGEX:
        t = jj_consume_token(FROMREGEX);
        break;
      default:
        jj_la1[13] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    ((OpNode)jjtn000).setOp(t.image);
      jj_consume_token(OPEN);
      String();
      jj_consume_token(CLOSE);
    } catch (Throwable jjte000) {
    if (jjtc000) {
      jjtree.clearNodeScope(jjtn000);
      jjtc000 = false;
    } else {
      jjtree.popNode();
    }
    if (jjte000 instanceof RuntimeException) {
      {if (true) throw (RuntimeException)jjte000;}
    }
    if (jjte000 instanceof ParseException) {
      {if (true) throw (ParseException)jjte000;}
    }
    {if (true) throw (Error)jjte000;}
    } finally {
    if (jjtc000) {
      jjtree.closeNodeScope(jjtn000, true);
    }
    }
  }

  static final public String BinaryOpTier1() throws ParseException {
                               Token t;
    t = jj_consume_token(UN);
    {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

  static final public String BinaryOpTier2() throws ParseException {
                               Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INT:
      t = jj_consume_token(INT);
      break;
    case XOR:
      t = jj_consume_token(XOR);
      break;
    default:
      jj_la1[14] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    {if (true) return t.image;}
    throw new Error("Missing return statement in function");
  }

  static final public void skip(int kind) throws ParseException {
  ParseException e = generateParseException();
  System.out.println(e.toString());
  Token t;
  do {
    t = getNextToken();
  } while (t.kind != kind);
  }

  static private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  static private boolean jj_3R_3() {
    if (jj_scan_token(SYM)) return true;
    if (jj_scan_token(EQUALS)) return true;
    return false;
  }

  static private boolean jj_3_1() {
    if (jj_3R_3()) return true;
    return false;
  }

  static private boolean jj_initialized_once = false;
  /** Generated Token Manager. */
  static public StmtParserTokenManager token_source;
  static SimpleCharStream jj_input_stream;
  /** Current token. */
  static public Token token;
  /** Next token. */
  static public Token jj_nt;
  static private int jj_ntk;
  static private Token jj_scanpos, jj_lastpos;
  static private int jj_la;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[15];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x40000000,0x9b87340,0x40000001,0x49b87341,0x80000,0x9b07340,0x10000,0xbf07340,0x48400,0x48400,0x9b07340,0x2400000,0x1b07000,0x300,0x48000,};
   }
  static final private JJCalls[] jj_2_rtns = new JJCalls[1];
  static private boolean jj_rescan = false;
  static private int jj_gc = 0;

  /** Constructor with InputStream. */
  public StmtParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public StmtParser(java.io.InputStream stream, String encoding) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new StmtParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  static public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public StmtParser(java.io.Reader stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new StmtParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  static public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public StmtParser(StmtParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser. ");
      System.out.println("       You must either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(StmtParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 15; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  static private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  static final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  static private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  static final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  static private int[] jj_expentry;
  static private int jj_kind = -1;
  static private int[] jj_lasttokens = new int[100];
  static private int jj_endpos;

  static private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  static public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[32];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 15; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 32; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  static final public void enable_tracing() {
  }

  /** Disable tracing. */
  static final public void disable_tracing() {
  }

  static private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  static private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
