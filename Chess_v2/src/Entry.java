import java.util.ArrayList;

public class Entry {
	
	private Piece piece; 
	private int occupied; // 1 if occupied, 0 if not
	private ArrayList<Square> neighbors;
	private int move_only; // 1 if piece cannot capture on this square, 0 otherwise
	private int toDelete; // 1 if entry should be deleted, 0 if not
	
	public Square home_square;
	
	Entry(Piece piece, int occupied, int move_only, int toDelete) {
		this.piece = piece;
		this.occupied = occupied;
		this.move_only = move_only;
		this.toDelete = toDelete;
		neighbors = new ArrayList<Square>();
	}
	
	Entry() {
		
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public boolean isOccupied() {
		if (occupied == 1) {
			return true;
		}
		
		return false;
	}
	
	public ArrayList<Square> getNeighbors() {
		return neighbors;
	}
	
	public int getMoveOnly() {
		return move_only;
	}
	
	public void addNeighbor(Square neighbor) {
		neighbors.add(neighbor);
	}
	
	public void markAsDeleted() {
		toDelete = 1;
	}
	
	public boolean isMarkedDeleted() {
		if (toDelete == 1) {
			return true;
		}
		return false;
	}
	
	public boolean equalsEntry(Entry e) {
		if (this.piece.equalsPiece(e.piece)) {
			// if two entries share the same piece, they are the same
			return true;
		}
		
		return false;
	}
}
