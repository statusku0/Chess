import java.util.HashMap;
import java.util.HashSet;
/*
 * TODO: detect castles
 * 
 * TODO: Change concept: instead of deleting nodes via dfs, mark them
 * as "blocked". This means that the piece graphs all maintain the same 
 * structure -- it's just that some parts of the graph may have "blocked"
 * nodes, i.e. nodes that the piece cannot access at the moment. 
 * Also, consider moving a piece as a 2-step process: taking the piece off 
 * the board, and putting the piece onto the board. Thus, I only need to 
 * consider these 2 cases: 
 * 1. Taking a piece off an arbitrary board.
 * 2. Putting a piece onto an arbitrary board.
 * This also simplifies considering all the different rules:
 * Place the piece onto the board, see what nodes have other pieces on
 * them, and then do dfs to mark nodes as blocked. Then, apply the rules
 * the block more nodes.
 * 
 * 
 * Every time you reach a connection in a piece graph, analyze the rules
 * to determine whether you can continue to the next node (to mark nodes
 * blocked or unblocked)
 * 
 * From the perspective of the piece graph, the only things affecting the
 * graph are occupied pieces and out-of-bounds.
 * 
 * When placing a piece onto a board and blocking nodes, keep track of 
 * what occupied pieces and out-of-bounds boundaries cause a node to be 
 * blocked. Then, when removing a piece from a square, for each 
 * piece graph that contains nodes blocked because of that piece, 
 * unblock nodes that were originally blocked by that removed piece. 
 */

// 8x8 board
// black on top, white on bottom

public class Board {
	
	Square[][] grid;
	HashMap<Piece, Square> occupied_list; // maps piece to 
									      //the square that it is in
	
	int en_passant_indicator; // 1 if pawn has just moved 2 squares 
	String en_passant_color; // color of player that moved the pawn
	int en_passant_row; // row of the pawn
	int en_passant_col; // column of the pawn
	
	int pawn_promotion_counter; // used to assign a number to pieces
								// born through pawn promotion
	
	private String[] pieces = {"pawn", "rook", "knight",
			"bishop", "queen", "king"};
	
	private int[][] positions = {{1, 2, 3, 4, 5, 3, 2, 1},
							     {0, 0, 0, 0, 0, 0, 0, 0},
			 					 {-1, -1, -1, -1, -1, -1, -1, -1},
			 					 {-1, -1, -1, -1, -1, -1, -1, -1},
			 					 {-1, -1, -1, -1, -1, -1, -1, -1},
			 					 {-1, -1, -1, -1, -1, -1, -1, -1},
			 					 {0, 0, 0, 0, 0, 0, 0, 0},
			 					 {1, 2, 3, 4, 5, 3, 2, 1}};
	
	public Square getSquare(int row, int col) {
		if (inBounds(row, col))
			return grid[row][col];
		
		return null;
	}
	
	public boolean inBounds(int row, int col) {
		if (row < 0 || col < 0 
				|| row >= 8 || col >= 8) {
			return false;
		}
		
		return true;
	}
	
	Board() {
		// establish initial chess piece layout
		grid = new Square[8][8];
		
		// initialize variables
		en_passant_indicator = 0;
		en_passant_color = "";
		en_passant_row = 0;
		en_passant_col = 0;
		pawn_promotion_counter = -1;
		
		occupied_list = new HashMap<Piece, Square>();
		
		// initialize squares and place pieces down
		
		String color = "";
		for (int k1 = 0; k1 < 8; k1++) {
			for (int k2 = 0; k2 < 8; k2++) {
				grid[k1][k2] = new Square(k1, k2);
						
				if (positions[k1][k2] != -1) {
					if (k1 < 4) {
						color = "black";
					} else {
						color = "white";
					}
					Piece new_piece = new Piece(
							pieces[positions[k1][k2]], color, k2);
					placePieceOntoSquare(new_piece, getSquare(k1, k2));
					
					occupied_list.put(new_piece, grid[k1][k2]);
				}
			}
		}
		
		// construct intrinsic graphs
		IntrinsicGraphs.initGraphs();
		
		// update piece graphs
		for (int k1 = 0; k1 < 8; k1++) {
			for (int k2 = 0; k2 < 8; k2++) {
				Square sq = getSquare(k1, k2);
				
				if (sq.isOccupied()) {
					computePieceGraph(sq.getOccupiedEntry().getPiece(), sq);
				}
			}
		}
	}
	
	public void computePieceGraph(Piece p, Square s) {
		// assumes piece already occupies square
		
		System.out.println("updating " + p.getName() + " graph at " 
		+ s.getRow() + " " + s.getCol());
		
		IntrinsicGraphs.Node root = IntrinsicGraphs.getIntrinsicGraph(
				this, p, s);
		
		// dfs (for all pieces except pawns)
		computePieceGraphRecursive(p, s, root);
		
	}
	
	public void computePieceGraphRecursive(Piece p, Square central_s, 
			IntrinsicGraphs.Node cur) {
		
		Entry cur_entry = cur.convertToSquare(this, central_s).getEntry(p);
		
		System.out.println("searching " + cur.neighbors.size() + " neighbors");
		
		for (IntrinsicGraphs.Node neighbor : cur.neighbors) {
			Square neighbor_sq = neighbor.convertToSquare(this, central_s);
			if (neighbor_sq != null) {

				Entry new_entry = new Entry(p, 0, 0, 0); // marked as
														// unoccupied


				if (neighbor_sq.isOccupied()) {
					
					System.out.println("occupied square found");
					
					if (!neighbor_sq.getOccupiedEntry().
							getPiece().getColor().equals(p.getColor())) {
						
						System.out.println("...of different color");
						
						if (!neighbor.isMoveOnly()) {
							System.out.println(
									"Adding " + neighbor_sq.getRow() + " " 
							+ neighbor_sq.getCol() + " as a neighbor");
							
							// add new entry to neighbor_sq
							neighbor_sq.addEntry(new_entry);
							
							// connect cur_entry with neighbor_sq
							cur_entry.addNeighbor(neighbor_sq);
						}

					}

					// end dfs on this branch
					continue;

				}

				System.out.println("Adding " + neighbor_sq.getRow() 
				+ " " + neighbor_sq.getCol() + " as a neighbor");
				
				// add new entry to neighbor_sq
				neighbor_sq.addEntry(new_entry);
				
				// connect cur_entry with neighbor_sq
				cur_entry.addNeighbor(neighbor_sq);

				computePieceGraphRecursive(p, central_s, neighbor);
				
			}

		}
	}
	
	public void deletePieceGraph(Piece p, Square s, int delete_root) {
		// mark entries as deleted (mark root as deleted if delete_root == 1)
		deletePieceGraphRecursive(p, s, delete_root);
		
		// actually delete with garbage collector
		runGarbageCollector();
		
	}
	
	public void deletePieceGraphRecursive(Piece p, Square s, int delete) {
		// get entry
		Entry e = s.getEntry(p);
		
		// mark as deleted
		if (delete == 1) {
			e.markAsDeleted();
		}
		
		for (Square neighbor_sq : e.getNeighbors()) {
			deletePieceGraphRecursive(p, neighbor_sq, 1);
		}
	}
	
	public void runGarbageCollector() {
		// actually deletes entries marked as deleted
		
		for (int k1 = 0; k1 < 8; k1++) {
			for (int k2 = 0; k2 < 8; k2++) {
				Square s = getSquare(k1, k2);
				
				for (Entry e : s.getEntries()) {
					if (e.isMarkedDeleted()) {
						s.deleteEntry(e);
					}
				}
			}
		}
	}
	
	public HashSet<Square> getPieceGraph(Piece p, Square s) {
		System.out.println("getting piece graph of " + p.getName() + " on " 
	+ s.getRow() + " " + s.getCol());
		
		HashSet<Square> sqs = new HashSet<Square>();
		
		getPieceGraphRecursive(p, s, sqs);
		
		return sqs;

	}
	
	public void getPieceGraphRecursive(Piece p, Square s, HashSet<Square> sqs) {
		
		Entry e = s.getEntry(p);
		
		System.out.println("on square on " + s.getRow() + " " + s.getCol());
		
		for (Square sq : e.getNeighbors()) {
			sqs.add(sq);
			
			getPieceGraphRecursive(p, sq, sqs);
		}
	}
	
	public void placePieceOntoSquare(Piece p, Square s) {
		Entry e = new Entry(p, 1, 0, 0); // marked as occupied
		
		s.addEntry(e);
	}
	
	// move p from s1 to s2
	public void makeMove(Piece p, Square s1, Square s2, 
			String pawn_promotion_piece) {
		
		// check if piece is pawn and it moves 2 squares (enables en passant possibility)
		if (p.getName().equals("pawn") && Math.abs(s2.getRow() - s1.getRow()) == 2) {
			en_passant_indicator = 1;
			en_passant_color = p.getColor();
			en_passant_row = s2.getRow();
			en_passant_col = s2.getCol();
		} else if (en_passant_indicator == 1 && p.getColor().equals(en_passant_color)){
			// turn off en_passant_indicator if player that initiated it makes another move
			en_passant_indicator = 0;
		}
		
		// check if piece is pawn and it did an en passant move
		if (p.getName().equals("pawn") && s2.getCol() != s1.getCol() 
				&& !s2.isOccupied()) {
			// capture pawn that got captured
			Square captured_sq = getSquare(s1.getRow(), s2.getCol());
			
			captured_sq.deleteEntry(captured_sq.getOccupiedEntry());
		}
		
		Piece p_new = new Piece();
		
		if (p.getName().equals("pawn") && 
				(s2.getRow() == 0 || s2.getRow() == 7)) {
			// pawn promotion
			p_new = new Piece(pawn_promotion_piece, 
					p.getColor(), pawn_promotion_counter);
			pawn_promotion_counter--;

		} else {
			p_new = p.clonePiece();
		}
		
		// add new occupied entry for p_new in s2
		s2.addEntry(new Entry(p_new, 1, 0, 0));
		
		// delete piece graph for p on s1 (and delete from occupied_list)
		deletePieceGraph(p, s1, 1);
		occupied_list.remove(p);
		
		// compute new piece graph for p_new on s2
		computePieceGraph(p_new, s2);
		
		// update other piece graphs
		for (Entry e : s2.getEntries()) {
			Piece pi = e.getPiece();
			
			if (!p_new.equals(pi)) {
				deletePieceGraph(pi, s2, 0);
			}
			
			// capture
			if (e.isOccupied()) {
				s2.deleteEntry(e);
				// delete pi from occupied_list
				occupied_list.remove(pi);
			}
		}
		
		// garbage collection
		runGarbageCollector();
		
		// update occupied_list
		occupied_list.put(p_new, s2);
	}
	
	// returns true if pawn (piece p) on s1 can en passant to s2
	public boolean canEnPassant(Piece p, Square s1, Square s2, int direction) {
		
		if (en_passant_indicator == 0) {
			return false;
		}
		
		// again, direction = -1 -> down, direction = 1 -> up

		if (s2.getRow() == s1.getRow() - direction && Math.abs(s2.getCol() - s1.getCol()) == 1) { 
			
			// above checks if s2 is in the correct spot

			Square captured_sq = getSquare(s1.getRow(), s2.getCol());
			
			if (captured_sq.isOccupied() && captured_sq.getOccupyingPiece().getName().equals("pawn")) {
				Piece occ_p = captured_sq.getOccupyingPiece();

				if (!occ_p.getColor().equals(p.getColor()) && s2.getRow() == en_passant_row
						&& s2.getCol() == en_passant_col) {
					return true;
				}
			}

		}
		
		return false;
		
	}

	public static void main(String[] args) {

	}

}
