import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Scanner;

/*
 * Converts algebraic notation to actual chess moves
 */
public class Reader {
	
	public static void interpretExpression(Board b, String e, String player) {
		if (e.charAt(e.length() - 1) == '+') {
			System.out.println("king in check");
			e = e.substring(0, e.length() - 1);
		}
		
		Square s1 = new Square(new Piece("", "white"), 0, 0);
		
		char first = e.charAt(0);
		
		String name = ""; // name of piece that is moving
		
		if (e.equals("0-0")) {
			b.castle(player, "king");
			return;
		}
		if (e.equals("0-0-0")) {
			b.castle(player, "queen");
			return;
		}
		
		if (first == 'K') {
			name = "king";
		} else if (first == 'Q') {
			name = "queen";
		} else if (first == 'R') {
			name = "rook";
		} else if (first == 'B') {
			name = "bishop";
		} else if (first == 'N') {
			name = "knight";
		} else {
			name = "pawn";
		}
		
		System.out.println("name : " + name);
		
		// pawn promotion
		if (name.equals("pawn")) {
			char last = e.charAt(e.length() - 1);
			String new_name = "";

			if (last == 'K') {
				new_name = "king";
			} else if (last == 'Q') {
				new_name = "queen";
			} else if (last == 'R') {
				new_name = "rook";
			} else if (last == 'B') {
				new_name = "bishop";
			} else if (last == 'N') {
				new_name = "knight";
			}

			if (!new_name.equals("")) { // confirms pawn promotion
				// do pawn promotion
				int[] pawn_coord = squareNameToCoordinate(e.substring(0, 2));
				s1 = b.board[pawn_coord[0] + 1][pawn_coord[1]];

				b.makeMovePawn(s1, 
						b.board[pawn_coord[0]][pawn_coord[1]], new_name);
				return;

			}
		}
		
		System.out.println(e.substring(
				e.length() - 2, e.length()));
		int[] coord = squareNameToCoordinate(e.substring(
				e.length() - 2, e.length()));
		System.out.println("coord1 : " + coord[0]);
		System.out.println("coord2 : " + coord[1]);
		
		Square s2 = b.board[coord[0]][coord[1]];

		
		HashSet<Square> possible_pieces = b.getSquaresWithNameAndColor(
				name, player);
		System.out.println(possible_pieces.size() + " possible pieces");
		HashSet<Square> final_pieces = new HashSet<Square>();
		
		for (Square s : possible_pieces) {
			System.out.println("possible : " + s.row + " " + s.col);
			for (Square a : b.listOfAvailableSquares(s.piece.name, s, 0)) {
				if (a.row == coord[0] && a.col == coord[1]) {
					final_pieces.add(s);
				}
			}
		}
		
		System.out.println(final_pieces.size() + " final pieces");
		if (final_pieces.size() == 1) {
			for (Square s : final_pieces) {
				s1 = s;
			}
		}

		
		if (final_pieces.size() > 1) {
			for (Square s : final_pieces) {
				if (name.equals("pawn")) {
					// indicates capture
					if (s.col == e.charAt(0) - 97) {
						// grab pawn in correct column
						s1 = s;
					}
				}
				
				if (e.charAt(1) >= 30 && e.charAt(1) <= 39) { // char is number
					if (s.row == 8 - (e.charAt(1) - 48)) {
						s1 = s;
					}
				} else { // char is letter
					if (s.col == e.charAt(1) - 97) {
						s1 = s;
					}
				}
			}
		}
		System.out.println("Make move from " + s1.row + ", " + s1.col +
				" to " + s2.row + ", " + s2.col);
		
		b.makeMove(s1, s2);
		
		
	}
	
	public static int[] squareNameToCoordinate(String n) {
		int[] coord = new int[2];
		
		coord[0] = 8 - (n.charAt(1) - 48);
		coord[1] = n.charAt(0) - 97;
		
		return coord;
	}
	
	public static void readFile(Board b, Scanner input) throws IOException {
		
		InputStreamReader in = new InputStreamReader(System.in);
		BufferedReader keyboard = new BufferedReader(in);
		
		int counter = 0;
		
		while (input.hasNextLine()) {
			keyboard.readLine(); // iterate through chess moves
			
			String player = "";
			
			if (counter == 0) {
				player = "white";
			} else if (counter == 1) {
				player = "black";
			}
			
			interpretExpression(b, input.nextLine().trim(), player);
			
			System.out.println(b.board[0][6].piece.name);
			
			Visualizer.visualizeBoard(b);
			
			if (b.isCheckmate(player)) {
				System.out.println(player + " wins!");
				Visualizer.visualizeBoard(b);
			}
			
			counter++;
			if (counter > 1) counter = 0;
			
		}
		
		input.close();
	}
	
	public static void main(String[] args) throws IOException {
		Visualizer.initGrid();
		
		Board test_board = new Board();
		
		Visualizer.visualizeBoard(test_board);
		
		try {
			Scanner input = new Scanner(new File("test"));
			readFile(test_board, input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		

	}

}
