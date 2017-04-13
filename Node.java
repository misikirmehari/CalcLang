/*  a Node holds one node of a parse tree
    with several pointers to children used
    depending on the kind of node
*/

import java.util.*;
import java.io.*;
import java.awt.*;

public class Node {
	public static int count = 0; // maintain unique id for each node

	private int id;
	private String kind; // non-terminal or terminal category for the node
	private String info; // extra information about the node such as
							// the actual identifier for an I

	// references to children in the parse tree
	private Node first, second, third;

	// construct a common node with no info specified
	public Node(String k, Node one, Node two, Node three) {
		kind = k;
		info = "";
		first = one;
		second = two;
		third = three;
		id = count;
		count++;
		//System.out.println(this);
	}

	// construct a node with specified info
	public Node(String k, String inf, Node one, Node two, Node three) {
		kind = k;
		info = inf;
		first = one;
		second = two;
		third = three;
		id = count;
		count++;
		//System.out.println(this);
	}

	// construct a node that is essentially a token
	public Node(Token token) {
		kind = token.getKind();
		info = token.getDetails();
		first = null;
		second = null;
		third = null;
		id = count;
		count++;
		//System.out.println(this);
	}

	// public String toString() {
	// 	return "#" + id + "[" + kind + "," + info + "]";
	// }

	// produce array with the non-null children
	// in order
	private Node[] getChildren() {
		int count = 0;
		if (first != null)
			count++;
		if (second != null)
			count++;
		if (third != null)
			count++;
		Node[] children = new Node[count];
		int k = 0;
		if (first != null) {
			children[k] = first;
			k++;
		}
		if (second != null) {
			children[k] = second;
			k++;
		}
		if (third != null) {
			children[k] = third;
			k++;
		}

		return children;
	}

	// ******************************************************
	// graphical display of this node and its subtree
	// in given camera, with specified location (x,y) of this
	// node, and specified distances horizontally and vertically
	// to children
	public void draw(Camera cam, double x, double y, double h, double v) {

		System.out.println("draw node " + id);

		// set drawing color
		cam.setColor(Color.black);

		String text = kind;
		if (!info.equals(""))
			text += "(" + info + ")";
		cam.drawHorizCenteredText(text, x, y);

		// positioning of children depends on how many
		// in a nice, uniform manner
		Node[] children = getChildren();
		int number = children.length;
		System.out.println("has " + number + " children");

		double top = y - 0.75 * v;

		if (number == 0) {
			return;
		} else if (number == 1) {
			children[0].draw(cam, x, y - v, h / 2, v);
			cam.drawLine(x, y, x, top);
		} else if (number == 2) {
			children[0].draw(cam, x - h / 2, y - v, h / 2, v);
			cam.drawLine(x, y, x - h / 2, top);
			children[1].draw(cam, x + h / 2, y - v, h / 2, v);
			cam.drawLine(x, y, x + h / 2, top);
		} else if (number == 3) {
			children[0].draw(cam, x - h, y - v, h / 2, v);
			cam.drawLine(x, y, x - h, top);
			children[1].draw(cam, x, y - v, h / 2, v);
			cam.drawLine(x, y, x, top);
			children[2].draw(cam, x + h, y - v, h / 2, v);
			cam.drawLine(x, y, x + h, top);
		} else {
			System.out.println("no Node kind has more than 3 children???");
			System.exit(1);
		}

	}// draw

	// ----------------------------------------- Execute --------------------------------------------------//

	/** This method is used to execute the program using the parse tree
		obtained from the parser */
	public void execute() {

		if (this.kind.equals("eof")) {

			System.out.print("end of file reached");
			return;

		}
		else if (this.kind.equals("statements")) {

			if (second.kind.equals("eof")) {
				if (first.kind.equals("statement")) {
					first.execute();
				}
			} else {
				first.execute();
				second.execute();
			}
		}
		else if (this.kind.equals("statement")) {

			if (info.matches("msg")) {

				if (second != null && second.kind.equals("string") && second.info != null) {
					System.out.print(second.info);
				}

			}
			else if (info.matches("newline")) {

				System.out.println();

			}
			else if (info.matches("input")) {

				String consoleMessage = "";
				String id = "";
				Scanner consoleInput = new Scanner(System.in);

				if (second != null && second.kind.equals("string") && second.info != null) {
					consoleMessage = second.info;
				}

				if (third != null && third.kind.equals("id") && third.info != null) {
					id = third.info;
				}

				System.out.print(consoleMessage);

				double input = consoleInput.nextDouble();

				memory.insert(id, input);

				consoleInput.close();

			}
			else if (info.matches("print")) {

				if (second != null) {
					double return_value = second.evaluate();
					System.out.print(return_value);
				}

			}
			else if (info.matches("id") && second != null && second.info.equals("=") && third != null
					&& third.kind.equals("expression")) {

				String id = first.info;
				double num = third.evaluate();
				memory.insert(id, num);
			}
		}
	}

	// ----------------------------------------- Helper Methods --------------------------------------------------//

	private double builtInFunctions(Double num, String bif) {

		double return_value = num;

		if (bif.equals("cos")) {

			return_value = Math.sqrt(return_value);

		}
		else if (bif.equals("sin")) {

			return_value = Math.sin(num);

		}
		else if (bif.equals("sqrt")) {

			return_value = Math.cos(return_value);

		}
		else if (bif.equals("exp")) {

			return_value = Math.exp(return_value);
		}
		else {
			System.out.println("Error!");
			System.exit(1);
		}

		return return_value;

	}

	// ----------------------------------------- Evaluate --------------------------------------------------//
/** This method evaluates expression , term and factor
and returns a value to the execute method */
	private double evaluate() {

		double return_value = 0;

		if (this.kind.equals("expression")) {

			if (second == null) {
				return_value = first.evaluate();
			}
			else {
				if (second.kind.equals("single") && second.info.equals("+")) {
					return_value = first.evaluate() + third.evaluate();
				}
				else if (second.kind.equals("single") && second.info.equals("-")) {
					return_value = first.evaluate() - third.evaluate();

				}
			}

		}
		else if (this.kind.equals("term")) {

			if (second == null) {
				return_value = first.evaluate();
			}
			else {
				if (second.kind.equals("single") && second.info.equals("*")) {
					return_value = first.evaluate() * third.evaluate();
				}
				else if (second.kind.equals("single") && second.info.equals("/")) {
					return_value = first.evaluate() / third.evaluate();

				}
			}

		}
		else if (this.kind.equals("factor")) {

			if (second == null && first.kind.equals("num")) {
				return_value = Double.parseDouble(first.info);
			}
			else if (second == null && first.kind.equals("id")) {
				return_value = (double) memory.hash.get(first.info);
			}
			else if (second != null && first.kind.equals("bif")) {
				String function_type = first.info;
				return_value = second.evaluate();
				return_value = builtInFunctions(return_value, function_type);
			}
			else if (first.kind.equals("expression")) {
				return_value = first.evaluate();
			}
			else if (first.kind.equals("single") && first.info.equals("-") && second != null
					&& second.kind.equals("factor")) {
				return_value = (-1) * second.evaluate();
			}

		}

		return return_value;
	}



}// Node
