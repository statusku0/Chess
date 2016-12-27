public class Piece {
	String name;
	String color;

	Piece(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}
	
	public Piece clonePiece() {
		Piece p = new Piece(name, color);
		
		return p;
	}
	
	public boolean equals(Piece p1, Piece p2) {
		if (p1.name.equals(p2.name) && p1.color.equals(p2.color)) {
			return true;
		}
		
		return false;
	}
	
}