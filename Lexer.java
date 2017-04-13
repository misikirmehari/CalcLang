/*  an instance of this class provides methods that produce a
    sequence of tokens following some Finite State Automata,
    with capability to put back tokens

*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.util.Stack;

public class Lexer {

	static String[] keywords = {"msg", "print", "newline", "input"};

    private static String[] builtInFunctions = { "sqrt", "exp", "sin", "cos"};

    String margin = "";

	// holds any number of tokens that have been put back
	private Stack<Token> stack;

	// the source of physical symbols
	private BufferedReader input;

	// one lookahead physical symbol
	private int lookahead;

	// construct a Lexer ready to produce tokens from a file
	public Lexer(String fileName) {
		try {
			input = new BufferedReader(new FileReader(fileName));
		} catch (Exception e) {
			error("Problem opening file named [" + fileName + "]");
		}

		stack = new Stack<Token>();
		lookahead = 0; // indicates no lookahead symbol present
	}// constructor

	// produce the next token
	public Token getNext() {
		if (!stack.empty()) {
			// produce the most recently putback token
			Token token = stack.pop();
			return token;
		} else {
			// produce a token from the input source
			int state = 1; // state of DFA

			String data = ""; // specific info for the token

			boolean done = false;

			int sym; // holds current symbol


			do {
				sym = getNextSymbol();

				// Start of the phase
				if (state == 1) {

					// Remains on state 1 if its newline | carriage return| space
					if (sym == 10 || sym == 13 || sym == 32) {
						// state stays at 1
					} else if (letter(sym)) {     // If it finds letters
						state = 2;
						data += (char) sym;

					} else if (sym == '*' || sym == '=' || sym == '(' || sym == ')' || sym == '+' || sym == '-'|| sym == '/') {
						state = 3;
						data += (char) sym;


					} else if (digit(sym)) {     // if it finds digits*
						state = 4;
						data += (char) sym;

					}	else if (sym == '\"') {
							state = 7;

					} else if (sym == -1) {// eof
						state = 9;

					} else {
						error("Error in lexical analysis phase with symbol " + sym + " in state " + state);
					}


					} else if (sym == '.') {
						state = 5;
						data += ".";





				} // state 1 ==================================================


				// =====================================================
				else if (state == 2) {
					if (letter(sym) || digit(sym)) {
						// stay in state 4
						data += (char) sym;
					} else {
						done = true;
						putBackSymbol(sym);
					}
				} // =====================================================
				else if (state == 5) {
					if (digit(sym)) {
						state = 4;
						data += (char) sym;
					} else {
						error("error in FA in state 5---must have digit " + "after .");
					}
				} // =====================================================
				else if (state == 3) {
					done = true;
					putBackSymbol(sym);
				} // =====================================================
				else if (state == 4) {
					if (digit(sym)) {
						// stay in state 6
						data += (char) sym;
					} else {
						done = true;
						putBackSymbol(sym);
					}
				} // =====================================================
				else if (state == 7) {
					if (sym == '\"') {
						state = 8;
					} else if (printable(sym)) {
						// stay in state 7;
						data += (char) sym;
					} else {
						error("unclosed string literal");
					}
				} // =====================================================
				else if (state == 8) {
					done = true;
					putBackSymbol(sym);
				} // =====================================================
//

				else if (state == 9) {
					done = true;
				} // ====================================================
				else {
					error("Unknown state " + state + " in Lexer");
				}

			} while (!done);

			// generate token depending on stopping state
			Token token;

			if (state == 2) {// reserved word, bif, or user-defined id
				for (int k = 0; k < keywords.length; k++)
					if (keywords[k].equals(data)) {
						token = new Token(data, "");
						return token;
					}

				for (int k = 0; k < builtInFunctions.length; k++)
                    if (builtInFunctions[k].equals(data)) {
                        token = new Token("bif", data);
                        return token;
                    }
				token = new Token("id", data);
				return token;
			}

			else if (state == 4) {// numeric token
				token = new Token("num", data);
				return token;
			}

			else if (state == 8) {// string literal
				token = new Token("string", data);
				return token;
			}


			else if (state == 3) {// special single symbol
				token = new Token("single", data);
				return token;
			}

			else if (state == 9) {// eof
				token = new Token("eof", "");
				return token;
			} else {// Lexer error
				error("somehow Lexer FA halted in inappropriate state " + state);
				return null;
			}
		}

	}// getNext

	public Token getToken() {
		Token token = getNext();
	//	System.out.println("                                   got token: " + token);
		return token;
	}

	public void putBack(Token token) {
	//	System.out.println(margin + "put back token " + token.toString());
		stack.push(token);
	}

	// next physical symbol is the lookahead symbol if there is one,
	// otherwise is next symbol from file
	private int getNextSymbol() {
		int result = -1;

		if (lookahead == 0) {// is no lookahead, use input
			try {
				result = input.read();
			} catch (Exception e) {
			}
		} else {// use the lookahead and consume it
			result = lookahead;
			lookahead = 0;
		}
		return result;
	}

	private void putBackSymbol(int sym) {
		if (lookahead == 0) {// sensible to put one back
			lookahead = sym;
		} else {
			System.out
					.println("Oops, already have a lookahead " + lookahead + " when trying to put back symbol " + sym);
			System.exit(1);
		}
	}// putBackSymbol

	private boolean letter(int code) {
		return 'a' <= code && code <= 'z' || 'A' <= code && code <= 'Z';
	}


	private boolean digit(int code) {
		return '0' <= code && code <= '9';
	}


	private boolean printable(int code) {
		return ' ' <= code && code <= '~';
	}


	// Error Message Method
	private static void error(String message) {
		System.out.println(message);
		System.exit(1);
	}

	// public static void main(String[] args) throws Exception {
	// 	System.out.print("Enter file name: ");
	// 	Scanner keys = new Scanner(System.in);
	// 	String name = keys.nextLine();
	//
	// 	Lexer lex = new Lexer(name);
	// 	Token token;
	//
	// 	do {
	// 		token = lex.getNext();
	// 		System.out.println(token.toString());
	// 	} while (!token.getKind().equals("eof"));
	//
	// }

}
