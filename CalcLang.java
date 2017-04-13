/**
  Implement whole process of taking a CalcLang source file and execute the language.

	- Misikir  A . Mehari
*/
import java.util.Scanner;

public class CalcLang {
	public static void main (String[]args){
		Scanner keys = new Scanner( System.in );
	    System.out.print("Enter CalcLang file name: ");
	    String name = keys.nextLine();

			/** Creates a new Instance of the Lexer and
			pass the name of the source file */
			Lexer lex = new Lexer(name);

			/** Creates a new Instance of the Parser and
			pass the lexer as an argument */
	    Parser parser = new Parser(lex);

			/** Creates a root node and parse the statements
			found in the source file */
	    Node root = parser.parseStatements();

			/** Final stage where the source file is executed
			by the execute method found in the Node class */
			root.execute();

	}


}
