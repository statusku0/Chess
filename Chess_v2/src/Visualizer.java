
import java.awt.Color;
import java.util.HashSet;

import princeton.StdDraw;

public class Visualizer {

	public static void draw(int row, int col, int N, Color c, int circle) {
		StdDraw.setPenColor(c);
		StdDraw.filledSquare(col + .5, N - row - .5, .45);

		if (circle == 1) {
			StdDraw.setPenColor(Color.PINK);
			StdDraw.circle(col + .5, N - row - .5, .3);
		}
	}

	public static void initGrid() {

		StdDraw.setXscale(0, 8);
		StdDraw.setYscale(0, 8);

	}
	
	public static void visualizeSquares(HashSet<Square> sqs) {
		
		for (Square s : sqs) {
			draw(s.getRow(), s.getCol(), 8, Color.BLUE, 0);
		}
	}

	public static void visualizeBoard(Board b) {

		for (int k1 = 0; k1 < 8; k1++) {
			for (int k2 = 0; k2 < 8; k2++) {
				Square cur_sq = b.getSquare(k1, k2);
				String name = "";
				String color = "";
				
				if (cur_sq.isOccupied()) {
					Entry occ_en = cur_sq.getOccupiedEntry();
					name = occ_en.getPiece().getName();
					color = occ_en.getPiece().getName();
				}

				int circle = 0;

				if (color == "white") {
					circle = 1;
				} else {
					circle = 0;
				}

				if (name.equals("rook")) {
					draw(k1, k2, 8, Color.BLACK, circle);
				}
				if (name.equals("knight")) {
					draw(k1, k2, 8, Color.GREEN, circle);
				}
				if (name.equals("bishop")) {
					draw(k1, k2, 8, Color.MAGENTA, circle);
				}
				if (name.equals("queen")) {
					draw(k1, k2, 8, Color.RED, circle);
				}
				if (name.equals("king")) {
					draw(k1, k2, 8, Color.YELLOW, circle);
				}
				if (name.equals("pawn")) {
					draw(k1, k2, 8, Color.ORANGE, circle);
				}
				if (name.equals("")) {
					if ((k1 + k2) % 2 == 0) {
						draw(k1, k2, 8, Color.LIGHT_GRAY, 0);
					} else {
						draw(k1, k2, 8, Color.GRAY, 0);
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		Board test_board = new Board();
		initGrid();
		
		visualizeBoard(test_board);
		
		
		visualizeSquares(test_board.getPieceGraph(
				test_board.getSquare(1, 3).getOccupyingPiece(), 
				test_board.getSquare(1, 3)));
				

	}

}
