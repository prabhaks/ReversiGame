public class Node {
	char[][] board;
	int alpha;
	int beta;
	int depth;
	int value;
	int row;
	int column;

	private void copy(char[][] newb, char[][] oldb){
		for (int i=0;i<8;i++){
			for (int j=0;j<8;j++){
				newb[i][j]=oldb[i][j];
			}
		}
	}

	public Node(char[][] board, int depth, int value, int row, int column) {
		this.board = new char[8][8];
		copy(this.board,board);
		this.depth = depth;
		this.value = value;
		this.row = row;
		this.column = column;
	}

	public Node(char[][] board, int alpha, int beta, int depth, int value,
			int row, int column) {
		this.board = new char[8][8];
		copy(this.board,board);
		this.alpha = alpha;
		this.beta = beta;
		this.depth = depth;
		this.value = value;
		this.row = row;
		this.column = column;
	}

	public char[][] getBoard() {
		return board;
	}

	public void setBoard(char[][] board) {
		this.board = board;
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public int getBeta() {
		return beta;
	}

	public void setBeta(int beta) {
		this.beta = beta;
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
}
