package Test;

import parser.*;
import parser.ast.ExitStmt;
import parser.ast.Prog;
import parser.ast.Stmt;
import visitors.evaluation.Eval;

import java.io.InputStreamReader;

public class TestMain {
    public static void main(String [] args){
        final String ANSI_BLUE = "\u001B[34m";
        final String ANSI_RESET = "\u001B[0m";
        Eval eval = new Eval();
        Stmt stmt;
        Tokenizer tokenizer = new StreamTokenizer(new InputStreamReader(System.in));
        Parser parser = new StreamParser(tokenizer);
            do {
                try{
                    System.err.print(ANSI_BLUE+"["+eval.getPrompt()+"]> "+ANSI_RESET);
                    stmt = ((StreamParser) parser).parseStmt();
                    if(stmt instanceof ExitStmt) break;
                    stmt.accept(eval);
                }
                catch(ParserException pe) {
                    System.err.println("Syntax error: " + pe.getMessage());

                    continue;
                }
            }while(true);
            System.err.println("Exited");
            System.exit(0);
    }
}
