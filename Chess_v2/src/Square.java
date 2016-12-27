import java.util.ArrayList;

public class Square {
	
	private int row;
	private int col;
	
	private ArrayList<Entry> entries;
	
	Square(int row, int col) {
		this.row = row;
		this.col = col;
		entries = new ArrayList<Entry>();
	}
	
	Square() {
		
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public ArrayList<Entry> getEntries() {
		return entries;
	}
	
	public void addEntry(Entry entry) {
		entries.add(entry);
		entry.home_square = this;
	}
	
	public boolean isOccupied() {
		for (Entry e : entries) {
			if (e.isOccupied()) {
				return true;
			}
		}
		
		return false;
	}
	
	public Entry getOccupiedEntry() {
		for (Entry e : entries) {
			if (e.isOccupied()) {
				return e;
			}
		}
		
		System.out.println("Occupying entry not found");
		return new Entry();
	}
	
	public Entry getEntry(Piece piece) {
		for (Entry e : entries) {
			if (e.getPiece().equalsPiece(piece)) {
				return e;
			}
		}
		
		System.out.println("Piece not found in square");
		return new Entry();
	}
	
	public Piece getOccupyingPiece() {
		// assuming square is occupied
		
		return getOccupiedEntry().getPiece();
	}
	
	public void deleteEntry(Entry e) {
		
		int index_to_delete = 0;
		
		for (int k1 = 0; k1 < entries.size(); k1++) {
			if (entries.get(k1).equalsEntry(e)) {
				index_to_delete = k1;
			}
		}
		
		entries.remove(index_to_delete);
	}

}
