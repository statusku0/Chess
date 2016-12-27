public class Square {

	Piece piece;

	int row;
	int col;

	Square(Piece piece, int row, int col) {
		this.piece = piece;
		this.row = row;
		this.col = col;
	}
	
	public Square cloneSquare() {
		Square s = new Square(piece.clonePiece(), row, col);
		
		return s;
	}
	
	public boolean equals(Square s1, Square s2) {
		if (s1.piece.equals(s2.piece) && s1.row == s2.row 
				&& s1.col == s2.col) {
			return true;
		}
		
		return false;
	}
}