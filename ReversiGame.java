import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReversiGame {
	private int taskType;
	private char myPlayer;
	private Integer cutoff;
	private static final char BLANK = '*';
	private static final char BLACK = 'X';
	private static final char WHITE = 'O';
	private BufferedReader in = null;
	private BufferedWriter out = null;
	char initBoard[][];
	private Node finalNode;
	private int terminalCount = 0;
	private boolean validMove = false;
	private int[][] eval = { { 99, -8, 8, 6, 6, 8, -8, 99 },
			{ -8, -24, -4, -3, -3, -4, -24, -8 }, { 8, -4, 7, 4, 4, 7, -4, 8 },
			{ 6, -3, 4, 0, 0, 4, -3, 6 }, { 6, -3, 4, 0, 0, 4, -3, 6 },
			{ 8, -4, 7, 4, 4, 7, -4, 8 }, { -8, -24, -4, -3, -3, -4, -24, -8 },
			{ 99, -8, 8, 6, 6, 8, -8, 99 } };
	private int[][] evalComp = { { 120, -20, 20, 5, 5, 20, -20, 120 },
			{ -20, -40, -5, -5, -5, -5, -40, -20 },
			{ 20, -5, 15, 3, 3, 15, -5, 20 }, { 5, -5, 3, 3, 3, 3, -5, 5 },
			{ 5, -5, 3, 3, 3, 3, -5, 5 }, { 20, -5, 15, 3, 3, 15, -5, 20 },
			{ -20, -40, -5, -5, -5, -5, -40, -20 },
			{ 120, -20, 20, 5, 5, 20, -20, 120 } };

	private static class SortedList<N extends Node> implements Comparator<N> {
		public int compare(N o1, N o2) {
			int node1 = o1.getValue();
			int node2 = o2.getValue();
			if (node1 == node2) {
				if (o1.getRow() == o2.getRow())
					return o1.getColumn() - o2.getColumn();
				else
					return o1.getRow() - o2.getRow();
			} else
				return node1 - node2;
		}
	}

	ArrayList<Node> getAllSuccessors(Node node, char player) {
		ArrayList<Node> moves = new ArrayList<Node>();
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (node.getBoard()[i][j] == player) {
					List<Node> succ = getSuccessors(node, i, j, player);
					for (Node nod : succ) {
						if (!contains(moves, nod)) {
							moves.add(nod);
						}
					}
				}
			}
		}
		return moves;
	}

	boolean contains(List<Node> moves, Node node) {
		for (Node n : moves) {
			if (n.getColumn() == node.getColumn()
					&& n.getRow() == node.getRow()) {
				return true;
			}
		}
		return false;
	}

	ArrayList<Node> getSuccessors(Node node, int row, int col, char player) {
		ArrayList<Node> moves = new ArrayList<Node>();
		// move in all 8 possible directions to find next state
		for (int i = 0; i < 8; i++) {
			int x = dirs[i][0];
			int y = dirs[i][1];
			// checking for boundary conds, when player is in 1st row or column.
			int r = row + y, c = col + x;
			while (r > -1 && r < 8 && c > -1 && c < 8) {
				if (node.getBoard()[r][c] != opposite(player)) {
					if (r == row + y && c == col + x)
						break;
					else {
						if (node.getBoard()[r][c] == BLANK) {
							// check value here
							if (taskType == 3 || taskType == 4) {
								moves.add(new Node(node.getBoard(), node
										.getAlpha(), node.getBeta(), node
										.getDepth() + 1, node.getValue(), r, c));
							} else {
								moves.add(new Node(node.getBoard(), node
										.getDepth() + 1, node.getValue(), r, c));
							}
							break;

						} else
							break;
					}
				}
				r = y + r;
				c = x + c;
			}
		}
		return moves;
	}

	static char opposite(char player) {
		if (player == BLACK) {
			return WHITE;
		} else
			return BLACK;
	}

	// 8 directions in x axis and y axis
	public final int[][] dirs = new int[][] { { 1, 1 }, { 1, 0 }, { 1, -1 },
			{ 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, 1 } };

	// make move to specified child node
	// if we know starting and end position and direction , then we can move
	// only in those direction without moving into all 8 directions.
	public void makeMove(Node childNode, char player) {
		int row = childNode.getRow();
		int col = childNode.getColumn();
		childNode.getBoard()[row][col] = player;
		// move in all 8 possible directions to find next state
		for (int i = 0; i < 8; i++) {
			int x = dirs[i][0];
			int y = dirs[i][1];
			// checking for boundary conds, when player is in 1st row or column.
			int r = row + y, c = col + x;
			while (r > -1 && r < 8 && c > -1 && c < 8) {
				if (childNode.getBoard()[r][c] != opposite(player)) {
					if (r == row + y && c == col + x)
						break;
					else {
						if (childNode.getBoard()[r][c] == BLANK) {
							break;
						} else {
							for (int a = row + y, b = col + x; a * y <= r * y
									&& b * x <= c * x; a += y, b += x) {
								childNode.getBoard()[a][b] = player;
							}
						}
					}
				}
				r = y + r;
				c = x + c;
			}
		}
	}

	public int evalFunc(Node node) {
		int rating = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (node.getBoard()[i][j] == myPlayer) {
					rating += eval[i][j];
				} else if (node.getBoard()[i][j] == opposite(myPlayer)) {
					rating -= eval[i][j];
				}
			}
		}
		return rating;
	}

	public int evalFuncComp(Node node) {
		int rating = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (node.getBoard()[i][j] == myPlayer) {
					rating += evalComp[i][j];
				} else if (node.getBoard()[i][j] == opposite(myPlayer)) {
					rating -= evalComp[i][j];
				}
			}
		}
		return rating;
	}

	int MAX(int val1, int val2) {
		return val1 > val2 ? val1 : val2;
	}

	int MIN(int val1, int val2) {
		return val1 < val2 ? val1 : val2;
	}

	private void printMinOutput(Node node) {

		try {
			if (node.getRow() == -1) {
				out.write("pass,"
						+ node.getDepth()
						+ ","
						+ (node.getValue() == 2147483647 ? "Infinity" : node
								.getValue()));
				out.newLine();
			} else {
				if (node.getDepth() == 0) {
					out.write("root,");
				} else
					out.write((char) (node.getColumn() + 'a') + ""
							+ (node.getRow() + 1) + ",");
				out.write(node.getDepth()
						+ ","
						+ (node.getValue() == 2147483647 ? "Infinity" : node
								.getValue()));
				out.newLine();
			}
		} catch (IOException ioe) {
			System.err.println("Error occurred while printing output: "
					+ ioe.getLocalizedMessage());
		}
	}

	private void printMaxOutput(Node node) {
		try {
			if (node.getRow() == -1) {
				out.write("pass,"
						+ node.getDepth()
						+ ","
						+ (node.getValue() == -2147483648 ? "-Infinity" : node
								.getValue()));
				out.newLine();
			} else {
				if (node.getDepth() == 0) {
					out.write("root,");
				} else
					out.write((char) (node.getColumn() + 'a') + ""
							+ (node.getRow() + 1) + ",");
				out.write(node.getDepth()
						+ ","
						+ (node.getValue() == -2147483648 ? "-Infinity" : node
								.getValue()));
				out.newLine();
			}

		} catch (IOException ioe) {
			System.err.println("Error occurred while printing output: "
					+ ioe.getLocalizedMessage());
		}
	}

	boolean terminalMM(Node node) {
		if (terminalCount == 2) {
			return true;
		}
		boolean flagX = false, flagY = false;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (node.getBoard()[i][j] == BLACK) {
					flagX = true;
				} else if (node.getBoard()[i][j] == WHITE) {
					flagY = true;
				}
			}
		}
		if ((flagX && !flagY) || (flagY && !flagX)) {
			return true;
		}
		return false;
	}

	int maxVal(Node node, char player) {
		// assuming cutoff always greater than zero, if not then need no set row
		// and column of start configuration.
		if (node.getDepth() == cutoff || terminalMM(node)) {
			int val = evalFunc(node);
			node.setValue(val);
			if (taskType != 1)
				printMaxOutput(node);
			return val;
		}

		node.setValue(-2147483648);
		int v = node.getValue();
		if (taskType != 1) {
			printMaxOutput(node);
		}
		List<Node> successors = getAllSuccessors(node, player);
		if (successors.isEmpty()) {
			terminalCount++;
			node.setDepth(node.getDepth() + 1);
			int row = node.getRow();
			node.setRow(-1);
			int t = minVal(node, opposite(player));
			node.setRow(row);
			node.setDepth(node.getDepth() - 1);
			node.setValue(t);
			if (t > finalNode.getValue() && node.getDepth() == 1) {
				finalNode.setRow(node.getRow());
				finalNode.setColumn(node.getColumn());
				finalNode.setValue(t);
				validMove = true;
			}
			if (taskType != 1)
				printMaxOutput(node);
			return t;
		}
		Collections.sort(successors, new SortedList<Node>());
		terminalCount = 0;
		for (Node succ : successors) {
			makeMove(succ, player);
			int t = minVal(succ, opposite(player));
			if (t > v) {
				if (t > finalNode.getValue() && succ.getDepth() == 1) {
					finalNode.setRow(succ.getRow());
					finalNode.setColumn(succ.getColumn());
					finalNode.setValue(t);
					validMove = true;
				}
				v = t;
			}
			node.setValue(v);
			if (taskType != 1)
				printMaxOutput(node);
		}
		return v;
	}

	int minVal(Node node, char player) {
		// assuming cutoff always greater than zero, if not then need no set row
		// and column of start configuration.

		if (node.getDepth() == cutoff || terminalMM(node)) {
			int val = evalFunc(node);
			node.setValue(val);
			if (taskType != 1) {
				printMinOutput(node);
			}
			return val;
		}

		node.setValue(2147483647);
		int v = node.getValue();
		if (taskType != 1) {
			printMinOutput(node);
		}
		List<Node> successors = getAllSuccessors(node, player);
		if (successors.isEmpty()) {
			terminalCount++;
			node.setDepth(node.getDepth() + 1);
			int row = node.getRow();
			node.setRow(-1);
			int t = maxVal(node, opposite(player));
			node.setRow(row);
			node.setDepth(node.getDepth() - 1);
			node.setValue(t);
			if (t > finalNode.getValue() && node.getDepth() == 1) {
				finalNode.setRow(node.getRow());
				finalNode.setColumn(node.getColumn());
				finalNode.setValue(t);
				validMove = true;
			}
			if (taskType != 1)
				printMinOutput(node);
			return t;
		}
		Collections.sort(successors, new SortedList<Node>());
		terminalCount = 0;
		for (Node succ : successors) {
			makeMove(succ, player);
			int t = maxVal(succ, opposite(player));
			if (t < v) {
				v = t;
			}
			node.setValue(v);
			if (taskType != 1)
				printMinOutput(node);
		}
		return v;
	}

	void printOutput(char[][] board) {
		BufferedWriter out = null;
		BufferedReader in = null;
		try {
			out = new BufferedWriter(new FileWriter("output.txt"));
			in = new BufferedReader(new FileReader("temp.txt"));
			for (int i = 0; i < 8; i++) {
				for (int j = 0; j < 8; j++) {
					out.write(board[i][j]);
				}
				out.newLine();
			}
			String s;
			while ((s = in.readLine()) != null) {
				out.write(s);
				out.newLine();
			}
		} catch (IOException ioe) {
			System.err.println("Error occurred while writing output file: "
					+ ioe.getLocalizedMessage());
		} finally {
			closeInput(in);
			new File("temp.txt").delete();
			closeOutput(out);
		}
	}

	void runGreedy(char[][] board) {
		cutoff = 1;
		finalNode = new Node(board, 0, -2147483648, 0, 0);
		if (!terminal(finalNode))
			maxVal(finalNode, myPlayer);
		closeOutput(out);
		if (validMove)
			makeMove(finalNode, myPlayer);
		printOutput(finalNode.getBoard());
	}

	void runMinimax(char[][] board) {
		finalNode = new Node(board, 0, -2147483648, 0, 0);
		try {
			out.write("Node,Depth,Value");
			out.newLine();
		} catch (IOException ioe) {
			System.err.println("Error occurred while printing output: "
					+ ioe.getLocalizedMessage());
		}
		if (!terminal(finalNode))
			maxVal(finalNode, myPlayer);
		closeOutput(out);
		if (validMove)
			makeMove(finalNode, myPlayer);
		printOutput(finalNode.getBoard());
	}

	private void printMinValueOutput(Node node) {
		try {
			if (node.getRow() == -1) {
				out.write("pass,"
						+ node.getDepth()
						+ ","
						+ (node.getValue() == 2147483647 ? "Infinity" : node
								.getValue())
						+ ","
						+ (node.getAlpha() == -2147483648 ? "-Infinity" : node
								.getAlpha())
						+ ","
						+ (node.getBeta() == 2147483647 ? "Infinity" : node
								.getBeta()));
				out.newLine();
			} else {
				if (node.getDepth() == 0) {
					out.write("root,");
				} else
					out.write((char) (node.getColumn() + 'a') + ""
							+ (node.getRow() + 1) + ",");
				out.write(node.getDepth()
						+ ","
						+ (node.getValue() == 2147483647 ? "Infinity" : node
								.getValue())
						+ ","
						+ (node.getAlpha() == -2147483648 ? "-Infinity" : node
								.getAlpha())
						+ ","
						+ (node.getBeta() == 2147483647 ? "Infinity" : node
								.getBeta()));
				out.newLine();
			}

		} catch (IOException ioe) {
			System.err.println("Error occurred while printing output: "
					+ ioe.getLocalizedMessage());
		}
	}

	private void printMaxValueOutput(Node node) {
		try {
			if (node.getRow() == -1) {
				out.write("pass,"
						+ node.getDepth()
						+ ","
						+ (node.getValue() == -2147483648 ? "-Infinity" : node
								.getValue())
						+ ","
						+ (node.getAlpha() == -2147483648 ? "-Infinity" : node
								.getAlpha())
						+ ","
						+ (node.getBeta() == 2147483647 ? "Infinity" : node
								.getBeta()));
				out.newLine();
			} else {
				if (node.getDepth() == 0) {
					out.write("root,");
				} else
					out.write((char) (node.getColumn() + 'a') + ""
							+ (node.getRow() + 1) + ",");
				out.write(node.getDepth()
						+ ","
						+ (node.getValue() == -2147483648 ? "-Infinity" : node
								.getValue())
						+ ","
						+ (node.getAlpha() == -2147483648 ? "-Infinity" : node
								.getAlpha())
						+ ","
						+ (node.getBeta() == 2147483647 ? "Infinity" : node
								.getBeta()));
				out.newLine();
			}

		} catch (IOException ioe) {
			System.err.println("Error occurred while printing output: "
					+ ioe.getLocalizedMessage());
		}

	}

	int maxValue(Node node, char player) {
		// assuming cutoff always greater than zero, if not then need no set row
		// and column of start configuration.
		if (node.getDepth() == cutoff || termAB(node)) {
			int val = evalFunc(node);
			node.setValue(val);
			printMaxValueOutput(node);
			return val;
		}

		node.setValue(-2147483648);
		int v = node.getValue();
		printMaxValueOutput(node);
		List<Node> successors = getAllSuccessors(node, player);
		if (successors.isEmpty()) {
			terminalCount++;
			node.setDepth(node.getDepth() + 1);
			int row = node.getRow();
			int beta = node.getBeta();
			node.setRow(-1);
			int t = minValue(node, opposite(player));
			node.setRow(row);
			node.setDepth(node.getDepth() - 1);
			node.setValue(t);
			if (t >= beta) {
				printMaxValueOutput(node);
				return t;
			}
			node.setAlpha(MAX(node.getAlpha(), t));
			node.setBeta(beta);
			printMaxValueOutput(node);
			if (t > finalNode.getValue() && node.getDepth() == 1) {
				finalNode.setRow(node.getRow());
				finalNode.setColumn(node.getColumn());
				finalNode.setValue(t);
				validMove = true;
			}
			return t;
		}
		Collections.sort(successors, new SortedList<Node>());
		terminalCount = 0;
		for (Node succ : successors) {
			makeMove(succ, player);
			succ.setAlpha(node.getAlpha());
			int t = minValue(succ, opposite(player));
			if (t > v) {
				if (t > finalNode.getValue() && succ.getDepth() == 1) {
					finalNode.setRow(succ.getRow());
					finalNode.setColumn(succ.getColumn());
					finalNode.setValue(t);
					validMove = true;
				}
				v = t;
			}
			if (v >= node.getBeta()) {
				node.setValue(v);
				printMinValueOutput(node);
				return v;
			}
			node.setAlpha(MAX(node.getAlpha(), v));
			node.setValue(v);
			printMaxValueOutput(node);
		}
		return v;
	}

	int minValue(Node node, char player) {
		// assuming cutoff always greater than zero, if not then need no set row
		// and column of start configuration.
		if (node.getDepth() == cutoff || termAB(node)) {
			int val = evalFunc(node);
			node.setValue(val);
			printMinValueOutput(node);
			return val;
		}

		node.setValue(2147483647);
		int v = node.getValue();
		printMinValueOutput(node);
		List<Node> successors = getAllSuccessors(node, player);
		if (successors.isEmpty()) {
			terminalCount++;
			int alpha = node.getAlpha();
			node.setDepth(node.getDepth() + 1);
			int row = node.getRow();
			node.setRow(-1);
			int t = maxValue(node, opposite(player));
			node.setRow(row);
			node.setDepth(node.getDepth() - 1);
			node.setValue(t);
			if (t <= alpha) {
				printMinValueOutput(node);
				return t;
			}
			node.setBeta(MIN(node.getBeta(), t));
			node.setAlpha(alpha);
			printMinValueOutput(node);
			if (t > finalNode.getValue() && node.getDepth() == 1) {
				finalNode.setRow(node.getRow());
				finalNode.setColumn(node.getColumn());
				finalNode.setValue(t);
				validMove = true;
			}
			return t;
		}
		Collections.sort(successors, new SortedList<Node>());
		terminalCount = 0;
		for (Node succ : successors) {
			makeMove(succ, player);
			succ.setBeta(node.getBeta());
			int t = maxValue(succ, opposite(player));
			if (t < v) {
				v = t;
			}
			if (v <= node.getAlpha()) {
				node.setValue(v);
				printMinValueOutput(node);
				return v;
			}
			node.setBeta(MIN(node.getBeta(), v));
			node.setValue(v);
			printMinValueOutput(node);
		}
		return v;
	}

	void closeInput(BufferedReader in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException io) {
				System.err.println("Error occurred while closing input file: "
						+ io.getLocalizedMessage());
			}

		}
	}

	void closeOutput(BufferedWriter out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException io) {
				System.err.println("Error occurred while closing output file: "
						+ io.getLocalizedMessage());
			}
		}
	}

	boolean termAB(Node node) {
		if (terminalCount == 2) {
			return true;
		}
		boolean flagX = false, flagY = false;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (node.getBoard()[i][j] == BLACK) {
					flagX = true;
				} else if (node.getBoard()[i][j] == WHITE) {
					flagY = true;
				}
			}
		}
		if ((flagX && !flagY) || (!flagX && flagY)) {
			return true;
		}
		return false;
	}

	boolean terminal(Node node) {
		boolean flagX = false, flagY = false;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (node.getBoard()[i][j] == BLACK) {
					flagX = true;
				} else if (node.getBoard()[i][j] == WHITE) {
					flagY = true;
				}
			}
		}
		if ((flagX && !flagY) || (!flagX && flagY)) {
			if (taskType != 1) {
				try {
					out.write("root," + node.getDepth() + "," + evalFunc(node));
					out.newLine();
				} catch (IOException ioe) {
					System.err.println("Error occurred while printing output: "
							+ ioe.getLocalizedMessage());
				}
			}
			return true;
		}
		return false;
	}

	boolean terminalAB(Node node) {
		boolean flagX = false, flagY = false;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (node.getBoard()[i][j] == BLACK) {
					flagX = true;
				} else if (node.getBoard()[i][j] == WHITE) {
					flagY = true;
				}
			}
		}
		if ((flagX && !flagY) || (!flagX && flagY)) {
			try {
				out.write("root,"
						+ node.getDepth()
						+ ","
						+ evalFunc(node)
						+ ","
						+ (node.getAlpha() == -2147483648 ? "-Infinity" : node
								.getAlpha())
						+ ","
						+ (node.getBeta() == 2147483647 ? "Infinity" : node
								.getBeta()));
				out.newLine();
			} catch (IOException ioe) {
				System.err.println("Error occurred while printing output: "
						+ ioe.getLocalizedMessage());
			}
			return true;
		}
		return false;
	}

	void runABPruning(char[][] board) {
		finalNode = new Node(board, -2147483648, 2147483647, 0, -2147483648, 0,
				0);
		try {
			out.write("Node,Depth,Value,Alpha,Beta");
			out.newLine();
		} catch (IOException ioe) {
			System.err.println("Error occurred while printing output: "
					+ ioe.getLocalizedMessage());
		}
		if (!terminalAB(finalNode))
			maxValue(finalNode, myPlayer);
		closeOutput(out);
		if (validMove)
			makeMove(finalNode, myPlayer);
		printOutput(finalNode.getBoard());
	}

	int maxValueComp(Node node, char player) {
		if (node.getDepth() == cutoff) {
			int val = evalFuncComp(node);
			node.setValue(val);
			return val;
		}
		node.setValue(-2147483648);
		int v = node.getValue();
		List<Node> successors = getAllSuccessors(node, player);
		if (successors.isEmpty()) {
			int val = evalFuncComp(node);
			node.setValue(val);
			return val;
		}
		Collections.sort(successors, new SortedList<Node>());
		for (Node succ : successors) {
			makeMove(succ, player);
			succ.setAlpha(node.getAlpha());
			int t = minValueComp(succ, opposite(player));
			if (t > v) {
				if (t > finalNode.getValue() && succ.getDepth() == 1) {
					finalNode.setRow(succ.getRow());
					finalNode.setColumn(succ.getColumn());
					finalNode.setValue(t);
				}
				v = t;
			}
			if (v >= node.getBeta()) {
				node.setValue(v);
				return v;
			}
			node.setAlpha(MAX(node.getAlpha(), v));
			node.setValue(v);
		}
		return v;
	}

	int minValueComp(Node node, char player) {
		if (node.getDepth() == cutoff) {
			int val = evalFuncComp(node);
			node.setValue(val);
			return val;
		}

		node.setValue(2147483647);
		int v = node.getValue();
		List<Node> successors = getAllSuccessors(node, player);
		if (successors.isEmpty()) {
			int val = evalFuncComp(node);
			node.setValue(val);
			return val;
		}
		Collections.sort(successors, new SortedList<Node>());
		for (Node succ : successors) {
			makeMove(succ, player);
			succ.setBeta(node.getBeta());
			int t = maxValueComp(succ, opposite(player));
			if (t < v) {
				v = t;
			}
			if (v <= node.getAlpha()) {
				node.setValue(v);
				return v;
			}
			node.setBeta(MIN(node.getBeta(), v));
			node.setValue(v);
		}
		return v;
	}

	void runCompetition(char[][] board) {
		cutoff = 12;
		try {
			out = new BufferedWriter(new FileWriter("output.txt"));
			finalNode = new Node(board, -2147483648, 2147483647, 0,
					-2147483648, 0, 0);
			maxValueComp(finalNode, myPlayer);
			out.write((char) (finalNode.getColumn() + 'a') + ""
					+ (finalNode.getRow() + 1));
			closeOutput(out);
		} catch (IOException ioe) {
			System.err.println("IO error occurred while printing output: "
					+ ioe.getLocalizedMessage());
		}
	}

	public ReversiGame() {
		String line;
		try {
			in = new BufferedReader(new FileReader("input.txt"));
			out = new BufferedWriter(new FileWriter("temp.txt"));
			taskType = Integer.parseInt(in.readLine());
			if (taskType != 1 && taskType != 2 && taskType != 3
					&& taskType != 4) {
				System.err
						.println("Invalid Task type : "
								+ taskType
								+ ". Valid inputs for task type are either 1, 2, 3 or 4");
				System.exit(1);
			}
			String player = in.readLine();
			myPlayer = player.charAt(0);
			if (myPlayer != BLACK && myPlayer != WHITE) { // should mind case
															// insensitivity?
				System.err.println("Invalid player : " + player
						+ ". Valid inputs for player are either X or O");
				System.exit(1);
			}
			String max_dep = in.readLine();
			if (taskType == 4) {
				// Double.parseDouble(depth);
			} else {
				cutoff = Integer.parseInt(max_dep);
			}
			initBoard = new char[8][8];
			for (int i = 0; i < 8; i++) {
				line = in.readLine();
				initBoard[i] = line.toCharArray();
			}

			if (taskType == 1) {
				runGreedy(initBoard);
			} else if (taskType == 2) {
				runMinimax(initBoard);
			} else if (taskType == 3) {
				runABPruning(initBoard);
			} else {
				runCompetition(initBoard);
			}
		} catch (IOException ioe) {
			System.err
					.println("Error occurred while performing file I/O operation: "
							+ ioe.getLocalizedMessage());
		} finally {
			closeInput(in);
		}
	}

	public static void main(String args[]) {
		new ReversiGame();
	}
}