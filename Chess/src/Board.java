import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

// black on top, white on bottom

/* TODO:
 * - check checker didn't detect check
 */

/*TODO:
 * - AI neural network: 64 networks, each one for each square,
 * ai aims to replicte the result of the network as much as possible. 
 * Neural network: Board input -> Board output
 */
public class Board {
	
	// number i => pieces[i];
	// -1 => empty square
	
	// top left corner is Square (0,0)
	
	private String[] pieces = {"empty", "pawn", "rook", "knight",
			"bishop", "queen", "king"};
	
	public Square[][] board;
	private int size;
	private int init = 0;
	
	private int[][] positions = {{2, 3, 4, 5, 6, 4, 3, 2},
								 {1, 1, 1, 1, 1, 1, 1, 1},
								 {0, 0, 0, 0, 0, 0, 0, 0},
								 {0, 0, 0, 0, 0, 0, 0, 0},
								 {0, 0, 0, 0, 0, 0, 0, 0},
								 {0, 0, 0, 0, 0, 0, 0, 0},
								 {1, 1, 1, 1, 1, 1, 1, 1},
								 {2, 3, 4, 5, 6, 4, 3, 2}};
	
	Board() {
		this.board = new Square[8][8];
		this.size = 8;
		
		initBoard();
	}
	
	/*
	 * sets up the board properly for a new game
	 */
	
	private void initBoard() {
		for (int k1 = 0; k1 < size; k1++) {
			for (int k2 = 0; k2 < size; k2++) {
				String color = "";
				
				if (k1 < 4) {
					color = "black";
				} else {
					color = "white";
				}
				
				board[k1][k2] = 
						new Square(new Piece(
								getPiece(positions[k1][k2]), color), 
								k1, k2);
			}
			
		}
		
		init = 1;
		
	}
	
	public Square getSquare(int row, int col) {
		return board[row][col];
	}
	public String getPiece(int num) {
		return pieces[num];
	}
	
	public int getSize() {
		return size;
	}
	
	private void showBoard() throws Exception {
		if (init == 0) {
			throw new Exception("board not yet initialized");
		}
		
		int max_word_length = 0;
		
		for (int k1 = 0; k1 < pieces.length; k1++) {
			if (max_word_length < pieces[k1].length()) {
				max_word_length = pieces[k1].length();
			}
		}
		
		for (int k1 = 0; k1 < size; k1++) {
			
			for (int k4 = 0; k4 < 8 * (max_word_length + 1); k4++) {
				System.out.print("-");
			}
			System.out.println();

			for (int k2 = 0; k2 < size; k2++) {
				System.out.print(board[k1][k2].piece.getName());
				
				for (int k3 = 0; k3 < max_word_length 
						- board[k1][k2].piece.getName().length(); k3++) {
					System.out.print(" ");
				}
				System.out.print("|");
			}
			
			System.out.println();
		}
	}
	
	// establish rules for each piece by
	// outputting the list of squares available to a piece.
	// capture = 1 -> I'm only interested in squares that pieces can 
	// capture
	
	public HashSet<Square> listOfAvailableSquares(
			String name, Square s, int capture) {
		
		HashSet<Square> avail_squares = new HashSet<Square>();
		
		if (name.equals("empty")) {
			return avail_squares;
		}
		
		int[] dir = {-1, 1};
		
		if (name.equals("rook")) {
			int[] ori = {0, 1};
			
			for (int k1 = 0; k1 < 2; k1++) {
				for (int k2 = 0; k2 < 2; k2++) {
					for (int offset = 0; offset < 8; offset++) {
						int r = s.row;
						int c = s.col;

						if (ori[k2] == 0) {
							c = s.col + (dir[k1] * offset);
						} else {
							r = s.row + (dir[k1] * offset);
						}
						
						if (inBounds(r, c)) {
							if (!isEmpty(r, c) && offset != 0 
									&& s.piece.color.equals(
											board[r][c].piece.color)) {
								break;
							}

							avail_squares.add(new Square(new Piece("", "black"), r, c));

							if (!isEmpty(r, c) && offset != 0) {
								break;
							}
						}
					}
				}
			}
			
		}
		
		if (name.equals("knight")) {
			// scan squares and see if |slope| = 1/2 or 2
			
			for (int k1 = -2; k1 <= 2; k1++) {
				for (int k2 = -2; k2 <= 2; k2++) {
					double slope = 
							Math.abs((double) k1 / (double) k2);
					if (slope == 0.5 || slope == 2) {
						if (inBounds(s.row + k1, s.col + k2) && 
								(isEmpty(s.row + k1, s.col + k2) ||
										!board[s.row + k1][s.col + k2].
										piece.color.equals(s.piece.color))) {
							avail_squares.add(new Square(
									new Piece("", "black"), 
									s.row + k1, s.col + k2));
						}
					}
				}
			}
		}
		
		if (name.equals("bishop")) {
			
			for (int k1 = 0; k1 < 2; k1++) {
				for (int k2 = 0; k2 < 2; k2++) {
					for (int offset = 0; offset < 8; offset++) {
						int r_offset = dir[k1] * offset;
						int c_offset = dir[k2] * offset;
						
						int r = s.row + r_offset;
						int c = s.col + c_offset;

						if (inBounds(r, c)) {
							if (!isEmpty(r, c) && offset != 0 
									&& s.piece.color.equals(
											board[r][c].piece.color)) {
								break;
							}

							avail_squares.add(new Square(
									new Piece("", "black"), r, c));

							if (!isEmpty(r, c) && offset != 0) {
								break;
							}
						}
					}
				}
			}
			
		}
		
		if (name.equals("queen")) {
			for (Square sq : listOfAvailableSquares("rook", s, capture)) {
				avail_squares.add(sq);
			}
			
			for (Square sq : listOfAvailableSquares("bishop", s, capture)) {
				avail_squares.add(sq);
			}
			
		}
		
		if (name.equals("king")) {
			for (int k1 = -1; k1 <= 1; k1++) {
				for (int k2 = -1; k2 <= 1; k2++) {
					if (inBounds(s.row + k1, s.col + k2) && 
							(isEmpty(s.row + k1, s.col + k2) ||
									!board[s.row + k1][s.col + k2].
									piece.color.equals(s.piece.color))) {
						avail_squares.add(new Square(
								new Piece("", "black"), 
								s.row + k1, s.col + k2));
					}
				}
			}
		}
		
		if (name.equals("pawn")) {
			
			// moving one square forward 
			
			if (capture == 0) { 
				if (s.piece.color.equals("white")) {
					if (inBounds(s.row - 1, s.col) 
							&& isEmpty(s.row - 1, s.col)) {
						avail_squares.add(new Square(
								new Piece("", "black"), s.row - 1, s.col));
					}
				} else if (s.piece.color.equals("black")) {
					if (inBounds(s.row + 1, s.col) 
							&& isEmpty(s.row + 1, s.col)) {
						avail_squares.add(new Square(
								new Piece("", "black"), s.row + 1, s.col));
					}
				}
			}
			
			// en passe
			
			int offset = 0;
			
			if (s.piece.color == "black") {
				offset = dir[1];
			} else if (s.piece.color == "white") {
				offset = dir[0];
			}
			
			// left side
			int lr = s.row;
			int lc = s.col - 1;
			if (lc >= 0) {
				if (!board[lr][lc].piece.name.equals("empty") && 
						!board[lr][lc].piece.color.equals(s.piece.color)) {
					if (inBounds(lr + offset, lc) && isEmpty(lr + offset, lc)) {
						avail_squares.add(
								new Square(
										new Piece("", "black"), lr + offset, lc));
					}
				}
			}
			
			// right side
			int rr = s.row;
			int rc = s.col + 1;
			if (rc < 8) {
				if (!board[rr][rc].piece.name.equals("empty") && 
						!board[rr][rc].piece.color.equals(s.piece.color)) {
					if (inBounds(rr + offset, rc) && isEmpty(rr + offset, rc)) {
						avail_squares.add(
								new Square(
										new Piece("", "black"), rr + offset, rc));
					}
				}
			}
			
			// capture
			for (int k1 = 0; k1 < 2; k1++) {
				int r = s.row;
				
				if (s.piece.color == "black") {
					r += 1;
				} else if (s.piece.color == "white") {
					r -= 1;
				}
				
				int c = s.col + dir[k1];
				
				if (inBounds(r, c) && !board[r][c].piece.color.equals(
						s.piece.color)) {
					if (!board[r][c].piece.name.equals("empty")) {
						avail_squares.add(new Square(new Piece("", "black"), r, c));
					}
				}

			}
			
			// moving 2 squares if in initial position
			if (s.row == 1 && s.piece.color.equals("black") &&
					isEmpty(s.row + 2, s.col)) {
				avail_squares.add(new Square(new Piece("", "black"), 
						s.row + 2, s.col));
			}
			if (s.row == 6 && s.piece.color.equals("white") &&
					isEmpty(s.row - 2, s.col)) {
				avail_squares.add(new Square(new Piece("", "black"), 
						s.row - 2, s.col));
			}

		}
		
		// remove self 
		for (Square sq : avail_squares) {
			if (sq.equals(s)) {
				avail_squares.remove(sq);
			}
		}
		
		return avail_squares;
	}
	
	public boolean inBounds(int row, int col) {
		if (row >= 8 || col >= 8 || row < 0 || col < 0) {
			return false;
		}
		
		return true;
	}
	
	public boolean isEmpty(int row, int col) {
		if (!inBounds(row, col)) {
			return false;
		}
		if (board[row][col].piece.name.equals("empty")) {
			return true;
		}
		
		return false;
	}
	
	/*
	 * Move piece on s1 to s2. 
	 * Returns true if something is captured.
	 * ** Not for pawns that get promoted once reaching 
	 * the last row.
	 */
	public boolean makeMove(Square s1, Square s2) {
		System.out.println(s1.row + " " + s1.col);
		
		boolean captured = false;
		
		if (!s2.piece.name.equals("empty")) {
			captured = true;
			System.out.println("piece on row : " + s2.row 
					+ ", col : " + s2.col + " captured");
		}
		s2.piece = s1.piece;
		s1.piece = new Piece("empty", "white");
		
		
		return captured;
	}
	
	/*
	 * Used to promote pawn to "name" when it reaches the last row.
	 */
	public void makeMovePawn(Square s1, Square s2, String name) {
		s2.piece = s1.piece;
		s1.piece = new Piece("empty", "white");
		
		s2.piece.name = name;
	}
	
	/*
	 * Castle (side indicates king-side or queen-side)
	 */
	public void castle(String player, String side) {
		int row = 0;

		if (player.equals("white")) {
			row = 7;
		}

		if (side.equals("king")) {
			makeMove(board[row][4], board[row][6]);
			makeMove(board[row][7], board[row][5]);
		} else if (side.equals("queen")) {
			makeMove(board[row][4], board[row][2]);
			makeMove(board[row][0], board[row][3]);
		}
	}
	
	public HashSet<Square> getSquaresWithNameAndColor(String name, 
			String color) {
		HashSet<Square> sqs = new HashSet<Square>();
		
		for (int k1 = 0; k1 < 8; k1++) {
			for (int k2 = 0; k2 < 8; k2++) {
				if (board[k1][k2].piece.name.equals(name) &&
						board[k1][k2].piece.color.equals(color)) {
					sqs.add(board[k1][k2]);
				}
			}
		}
		
		return sqs;
	}
	
	public Board cloneBoard() {
		Board b = new Board();
		
		for (int k1 = 0; k1 < 8; k1++) {
			for (int k2 = 0; k2 < 8; k2++) {
				b.board[k1][k2] = board[k1][k2].cloneSquare();
			}
		}
		
		return b;
	}
	
	
	
	/*
	 * Checkmate checker (player is the one supposedly being checkmated).
	 * True if checkmate, false if not.
	 */
	public boolean isCheckmate(String player) throws IOException {
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader keyboard = new BufferedReader(in);
		
		System.out.println("checking for checkmate");
		
		// if king can move, return false (no checkmate)
		String opponent = "white";
		if (player.equals("white")) {
			opponent = "black";
		}
		
		if (!isCheck(this, player, opponent, 1)) {
			System.out.println("no check upon first scan");
			return false;
		}
		
		System.out.println("check on first scan - continue scanning");
		
		Board temp_board = cloneBoard();
		
		for (int k1 = 0; k1 < 8; k1++) {
			for (int k2 = 0; k2 < 8; k2++) {
				Square s = temp_board.getSquare(k1, k2);
				
				// see if player can do anything
				if (s.piece.color.equals(player)) {
					HashSet<Square> possible_list = 
							temp_board.listOfAvailableSquares(s.piece.name, s, 0);
					
					for (Square s2 : possible_list) {
						temp_board.makeMove(s, s2);
						
						
						Visualizer.visualizeBoard(temp_board);
						keyboard.readLine();
						
						if (!isCheck(temp_board, player, opponent, 0)) {
							return false;
						}
						
						// reset temp_board
						temp_board = cloneBoard();
					}
				}
			}
		}
		
		return true;
		
	}
	
	public void printSquareList(HashSet<Square> list) {
		for (Square s : list) {
			System.out.println(s.row + " " + s.col);
		}
	}
	
	/*
	 * Returns true if king is threatened and cannot move
	 */
	public boolean isCheck(Board b, String player, String opponent,
			int first_scan) throws IOException {
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader keyboard = new BufferedReader(in);
		
		HashSet<Square> sq_list = new HashSet<Square>();
		
		HashSet<Square> king_list = new HashSet<Square>();
		
		Square king_sq = new Square(new Piece("", "white"), 0, 0);
		
		for (int k1 = 0; k1 < b.getSize(); k1++) {
			for (int k2 = 0; k2 < b.getSize(); k2++) {
				Square s = b.getSquare(k1, k2);
				if (s.piece.name.equals("king")) {
					king_sq = s;
					king_list = b.listOfAvailableSquares("king", s, 0);
				}
				if (s.piece.color.equals(opponent)) {
					System.out.println("looking at : " + s.piece.name);
					sq_list.addAll(b.listOfAvailableSquares(s.piece.name, s, 1));
					
					System.out.println("targetting: ");
					printSquareList(listOfAvailableSquares(s.piece.name, s, 1));
				}
			}
		}
		
		Visualizer.visualizeSquares(sq_list);
		
		keyboard.readLine();
		
		
		if (!sq_list.contains(king_sq)) {
			return false; // king not threatened
		}
		
		if (first_scan == 1) {
			System.out.println("king is in check");
		}
		
		for (Square s : king_list) {
			if (!sq_list.contains(s)) {
				return false; // king in check but can move
			}
		}
		
		return true;
	}
	

	public static void main(String[] args) throws Exception {
		
		Board test_board = new Board();
		
		test_board.initBoard();
		test_board.showBoard();
	}

}
