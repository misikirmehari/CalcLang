/*
    This class provides a recursive descent parser of CalcLang
    creating a parse tree.

*/

import java.util.*;
import java.io.*;

public class Parser {

	private Lexer lex;

	public Parser(Lexer lexer) {
		lex = lexer;
	}

/** The Parse Tree starts from this method and recursively goes through
		the whole file */
	public Node parseStatements() {
		Node first = parseStatement();
		Node second = null;
		Token token = lex.getToken();

		if (token.isKind("eof")) {
			second = new Node(token);
			return new Node("statements", first, second, null);
		} else {
			lex.putBack(token);
			second = parseStatements();
		}

		return new Node("statements", first, second, null);
	}

/** Parse statements */
	private Node parseStatement() {
		Token token = lex.getToken();
		Node first = null;
		Node second = null;
		Node third = null;
		String info = "";

		if (token.isKind("id")) {
			info = "id";
			first = new Node (token);

			Token token2 = lex.getToken();

			if (token2.matches("single", "=")) {

				second = new Node(token2);
				third = parseExpression();

				return new Node("statement", info ,first, second, third);
			} else {
				lex.putBack(token);
				first = parseExpression();
				return new Node("statement", info, first, null, null);
			}

		} else if (token.isKind("msg")) {
			info = "msg";
			first = new Node(token);
			Token token1 = lex.getToken();

			errorCheck(token1, "string");

			second = new Node(token1);

			return new Node("statement", info ,first, second, null);

		} else if (token.isKind("print")) {
			info = "print";
			Token token1 = lex.getToken();


			if (token1.isKind("id")) {
				lex.putBack(token1);

				first = new Node (token);

				second = parseExpression();
				return new Node("statement",info, first, second, null);
			}
			else if (token1.isKind("num")) {
				lex.putBack(token1);

				first = new Node (token);

				second = parseExpression();

				return new Node("statement", info,first, second, null);
			}

		} else if (token.isKind("newline")) {

			info = "newline";
			first = new Node(token);

			return new Node("statement",info, first, null, null);

		} else if (token.isKind("input")) {

			info = "input";
			first = new Node (token);

			Token token1 = lex.getToken();

			errorCheck(token1, "string");

			second = new Node(token1);

			Token token2 = lex.getToken();

			errorCheck(token2, "id");

			third = new Node(token2);

			return new Node("statement",info, first, second, third);

		}



		return new Node (token);
	}
/** Parse expression */
	  private Node parseExpression() {
	         Token token1 = lex.getToken();

	        if(token1.isKind("num") || token1.isKind("id")){
	            Token token2 = lex.getToken();
	            if(!token2.isKind("single")){
	                lex.putBack(token2);
	                lex.putBack(token1);
	                Node first = parseTerm();
	                return new Node("expression",first, null, null);
	            }else{
	                if(token2.matches("single", "*") || token2.matches("single", "/") || token2.matches("single", ")")){
	                    lex.putBack(token2);
	                    lex.putBack(token1);
	                    Node first = parseTerm();
	                    return new Node("expression",first, null, null);
	                }else{

	                	lex.putBack(token1);

	                    Node first = parseTerm();

	                    Node second = parseExpression();

	                    return new Node("expression",first, new Node(token2), second);
	                }

	            }
	        }

	        else if(token1.isKind("single")){
	            lex.putBack(token1);

	            Node first = parseTerm();

	            return new Node("expression",first, null, null);
	        }

	        else if(token1.isKind("bif")){

	        	lex.putBack(token1);

	        	Node first = parseTerm();

	        	return new Node("expression",first, null, null);
	        }

	        return new Node(token1);
	    }

	  private Node parseTerm() {

		Token token1 = lex.getToken();
		if (token1.isKind("num") || token1.isKind("id")) {
			Token token2 = lex.getToken();

			if (!token2.isKind("single")) {
				lex.putBack(token2);
				lex.putBack(token1);
				Node first = parseFactor();
				return new Node("term", first, null, null);
			}

			else if (token2.getDetails().equals(")")) {
				lex.putBack(token2);
				lex.putBack(token1);
				Node first = parseFactor();
				return new Node("term", first, null, null);
			}

			else {
				lex.putBack(token1);
				Node first = parseFactor();
				Node second = parseTerm();
				return new Node("term", first, new Node(token2), second);
			}
		}

		else if (token1.isKind("single")) {
			lex.putBack(token1);
			Node first = parseFactor();
			return new Node("term", first, null, null);
		}

		else if (token1.isKind("bif")) {
			lex.putBack(token1);
			Node first = parseFactor();
			return new Node("term", first, null, null);
		}

		return new Node(token1);
	}

	private Node parseFactor() {
		Token token1 = lex.getToken();

		if (token1.isKind("id")) {

			Token token2 = lex.getToken();

			if (!token2.matches("single", "(")) {
				lex.putBack(token2);
				return new Node("factor", new Node(token1), null, null);
			} else {
				errorCheck(token2, "single", "(");
				Node first = parseExpression();
				Token token3 = lex.getToken();
				errorCheck(token3, "single", ")");
				return new Node("factor", first, null, null);
			}

		} else if (token1.isKind("num")) {

			return new Node("factor", new Node(token1), null, null);

		} else if (token1.matches("single", "(")) {

			Node first = new Node(token1);
			Node second = parseExpression();
			Token token2 = lex.getToken();
			errorCheck(token2, "single", ")");
			Node third = new Node(token2);
			return new Node("factor", first, second, third);

		} else if (token1.matches("single", "-")) {

			Node first = new Node(token1);
			Node second = parseFactor();
			return new Node("factor", first, second, null);

		} else if (token1.isKind("bif")) {

			Node first = new Node(token1);
			Token token = lex.getToken();
			errorCheck(token, "single", "(");
			Node second = parseExpression();
			Token token2 = lex.getToken();
			errorCheck(token2, "single", ")");
			return new Node("factor", first, second, null);

		}

		return new Node(token1);
	}

	// check whether token is correct kind
	private void errorCheck(Token token, String kind) {
		if (!token.isKind(kind)) {
			System.out.println("Error:  expected " + token + " to be of kind " + kind);
			System.exit(1);
		}
	}

	// check whether token is correct kind and details
	private void errorCheck(Token token, String kind, String details) {
		if (!token.isKind(kind) || !token.getDetails().equals(details)) {
			System.out.println("Error:  expected " + token + " to be kind=" + kind + " and details=" + details);
			System.exit(1);
		}
	}

}
