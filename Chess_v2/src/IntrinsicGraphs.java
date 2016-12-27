import java.util.ArrayList;

public class IntrinsicGraphs {
	
	public static Node RookRoot;
	public static Node KnightRoot;
	public static Node BishopRoot;
	public static Node QueenRoot;
	public static Node KingRoot;
	
	private static int[][] a1 = {{0, 0}, {1, 1}, {2, 2}, {3, 3}, {4, 4}, {5, 5},
			{6, 6}, {7, 7}};
	
	private static int[][] a2 = {{-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2},
			{-2, -1}, {-2, 1}};
	
	private static int[][] a3 = {{1, 0}, {1, -1}, {0, -1}, {-1, -1},
			{-1, 0}, {-1, 1}, {0, 1}, {1, 1}};
	
	private static int[][] a4 = {{-1, 1}, {1, 1}, {0, 1}, {0, 2}};
	
	public static char[][] grid = new char[33][33];
	
	public static class Node {
		int[] coord;
		int move_only; // if 1, piece cannot capture on this square
		
		ArrayList<Node> neighbors;
		
		Node(int[] coord) {
			this.coord = coord;
			move_only = 0;
			neighbors = new ArrayList<Node>();
		}
		
		Node() {
			
		}
		
		public void setAsMoveOnly() {
			move_only = 1;
		}
		
		public boolean isMoveOnly() {
			if (move_only == 1) {
				return true;
			}
			
			return false;
			
		}
		
		public void addNeighbor(Node neighbor) {
			neighbors.add(neighbor);
		}
		
		private void createBranch(int x, int y) {
			
			// x from [-1,1], y from [-1,1]
			Node cur = this;
			
			for (int k1 = 1; k1 < 8; k1++) {
				int[] new_coord = new int[2];
				new_coord[0] = x * a1[k1][0];
				new_coord[1] = y * a1[k1][1];
				
				Node next = new Node(new_coord);
				cur.addNeighbor(next);
				
				cur = next;
				
			}
			
		}
		
		public Square convertToSquare(Board b, Square root) {
			
			// converting node coordinates to square row/col numbers
			int row = (-1 * coord[1]) + root.getRow();
			int col = coord[0] + root.getCol();
			
			System.out.println("converting " + coord[0] 
					+ " " + coord[1] + " to row/col nums (centered at " 
					+ root.getRow() + " " + root.getCol() + ")");
			System.out.println("getting square at " + row + " " + col);
			
			if (b.inBounds(row, col)) {
				return b.getSquare(row, col);
			}
			
			System.out.println("row and col nums out of bounds");
			
			return null;
			
		}
		
	}
	
	public static Node createRookGraph(Node root) {
		
		root.createBranch(1, 0);
		root.createBranch(0, -1);
		root.createBranch(-1, 0);
		root.createBranch(0, 1);
		
		return root;
		
	}
	
	public static Node createBishopGraph(Node root) {
		
		root.createBranch(1, 1);
		root.createBranch(1, -1);
		root.createBranch(-1, -1);
		root.createBranch(-1, 1);
		
		return root;
	
	}
	
	public static Node createQueenGraph(Node root) {
		
		createRookGraph(root);
		createBishopGraph(root);
		
		return root;
		
	}
	
	public static Node createKnightGraph(Node root) {
		
		for (int k1 = 0; k1 < a2.length; k1++) {
			int[] new_coord = a2[k1];
			
			root.addNeighbor(new Node(new_coord));
		}
		
		return root;
		
	}
	
	public static Node createKingGraph(Node root) {
		
		for (int k1 = 0; k1 < a3.length; k1++) {
			int[] new_coord = a3[k1];
			
			root.addNeighbor(new Node(new_coord));
		}
		
		return root;
		
	}
	
	public static Node createPawnGraph(Board b, Piece p,
			Square central_sq) {
		
		int[] root_coord = {0, 0};
		
		Node root = new Node(root_coord);
		
		int direction = 0;
		
		if (p.getColor() == "black") {
			direction = -1;
		} else if (p.getColor() == "white") {
			direction = 1;
		}
		
		// direction = -1 (down) or 1 (up)
		
		int[][] directed_a4 = new int[4][2];
		
		for (int k1 = 0; k1 < 4; k1++) {
			directed_a4[k1][0] = a4[k1][0];
			directed_a4[k1][1] = direction * a4[k1][1];
		}
		
		Square s_left = b.getSquare(central_sq.getRow(), 
				central_sq.getCol() - 1);
		
		Square s_upper_left = b.getSquare(central_sq.getRow() - direction, 
				central_sq.getCol() - 1);
		
		Square s_right = b.getSquare(central_sq.getRow(), 
				central_sq.getCol() + 1); 
		
		Square s_upper_right = b.getSquare(central_sq.getRow() - direction, 
				central_sq.getCol() + 1); 
		

		if ((s_upper_left != null && s_upper_left.isOccupied())
				|| (s_left != null && 
				b.canEnPassant(p, central_sq, s_left, direction))) {
			root.addNeighbor(new Node(directed_a4[0]));
		}

		if ((s_upper_right != null && s_upper_right.isOccupied())
				|| (s_right != null && 
				b.canEnPassant(p, central_sq, s_right, direction))) {
			root.addNeighbor(new Node(directed_a4[1]));
		}

		// * the 2 squares above the pawn are move_only
		
		Node vertical_next = new Node(directed_a4[2]);
		vertical_next.setAsMoveOnly();
		
		root.addNeighbor(vertical_next);
		
		Node vertical_double_next = new Node(directed_a4[3]);
		vertical_double_next.setAsMoveOnly();
		
		if ((central_sq.getRow() == 1 && p.getColor() == "black") ||
				(central_sq.getRow() == 6 && p.getColor() == "white")) {
			vertical_next.addNeighbor(vertical_double_next);
		}
		
		return root;
		
	}
	
	public static void initGraphs() {
		
		int[] root_coord = {0, 0};
		
		RookRoot = createRookGraph(new Node(root_coord));
		KnightRoot = createKnightGraph(new Node(root_coord));
		BishopRoot = createBishopGraph(new Node(root_coord));
		QueenRoot = createQueenGraph(new Node(root_coord));
		KingRoot = createKingGraph(new Node(root_coord));
				
	}
	
	public static void constructGraph(Node root) {
		
		Node cur = root;
		
		grid[16][16] = '.';
		
		for (Node neighbor : cur.neighbors) {
			
			int x_coord = neighbor.coord[0];
			int y_coord = neighbor.coord[1];
			
			int shifted_x_coord = 16 + (2 * x_coord);
			int shifted_y_coord = 16 + (2 * y_coord);
			
			grid[32 - shifted_y_coord][shifted_x_coord] = 
					'.';
			
			int shifted_cur_x_coord = 16 + (2 * cur.coord[0]);
			int shifted_cur_y_coord = 16 + (2 * cur.coord[1]);
			
			grid[32 - ((shifted_y_coord + shifted_cur_y_coord) / 2)]
					[(shifted_x_coord + shifted_cur_x_coord) / 2] = 
					'-';
			
			constructGraph(neighbor);
		}
		
	}
	
	public static void printGrid() {
		for (int k1 = 0; k1 < 33; k1++) {
			System.out.println();
			for (int k2 = 0; k2 < 33; k2++) {
				System.out.print(grid[k1][k2]);
			}
		}
	}
	
	public static Node getIntrinsicGraph(Board b, Piece p, Square central_sq) {
		
		String name = p.getName();
		Node graph = new Node();
		
		System.out.println("getting graph for " + name);
		
		switch (name) {
		case "rook":
			graph = RookRoot;
			break;
		case "knight":
			graph = KnightRoot;
			break;
		case "bishop":
			graph = BishopRoot;
			break;
		case "queen":
			graph = QueenRoot;
			break;
		case "king":
			graph = KingRoot;
			break;
		case "pawn":
			graph = createPawnGraph(b, p, central_sq);
			break;
		}
		
		return graph;
		
	}

	public static void main(String[] args) {
		
		initGraphs();
		constructGraph(KnightRoot);
		printGrid();
	}

}
