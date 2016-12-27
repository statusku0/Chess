
// uniquely determined by name and color and num (used to distinguish between
// identical pieces) (set as column num of piece in initial layout) 
// (for instance, knight for white on 1st column)

// special case: if piece is born from pawn promotion, 
// it gets a negative number assigned by a counter

public class Piece {
	
	private String name;
	private String color;
	private int num;
	
	Piece(String name, String color, int num) {
		this.name = name;
		this.color = color;
		this.num = num;
	}
	
	Piece() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public String getColor() {
		return color;
	}
	
	public boolean equalsPiece(Piece p) {
		if (p.name.equals(this.name) 
				&& p.color.equals(this.color) && p.num == this.num) {
			return true;
		}
		
		return false;
	}
	
	public Piece clonePiece() {
		Piece p_copy = new Piece(name, color, num);
		
		return p_copy;
	}

}
