import java.util.Scanner;

public class CalcLang {
	public static void main (String[]args){
		Scanner keys = new Scanner( System.in );
	    System.out.print("Enter CalcLang file name: ");
	    String name = keys.nextLine();
	    Lexer lex = new Lexer(name);
	    Parser parser = new Parser(lex);

	    Node root = parser.parseStatements();

	   // TreeViewer viewer = new TreeViewer("Parse Tree", 0, 0, 800, 500, root );

	    root.execute();

	}


}
