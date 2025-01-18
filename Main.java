import java.util.Scanner;
import java.util.Random;

public class Main {
	static final Scanner scanner = new Scanner(System.in);
	static final Random random = new Random();
	static final Rules rules = new Rules();
	static final String[] colors = { "Red", "Green", "Yellow", "Blue" };
	static final String reset = "\u001B[0m";
	static final String[] color = {
		"\u001B[31m",
		"\u001B[32m",
		"\u001B[33m",
		"\u001B[34m"
	};
	static Player[] players;
	static Board board;
	static Game game;
	static int nodes = 0;
	static int leafeval;
	static int leaf;

	public static final void main(String[] args) {
		int count;
		try {
			System.out.print("Enter players count: ");
			String countstr = scanner.nextLine();
			count = Integer.valueOf(countstr);
			if (count > 4 || count < 2) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("Invalid, defaulting to 4.");
			count = 4;
		}
		boolean alone = true;
		{
			System.out.print("Playing alone? ");
			String boolstr = scanner.nextLine();
			for (int i = 0; i < boolstr.length(); i++) {
				if (boolstr.charAt(i) == 'n') {
					alone = false;
				}
			}
		}
		int depth = 1;
		if (alone) try {
			System.out.print("Enter algorithm's depth: ");
			String depthstr = scanner.nextLine();
			depth = Integer.valueOf(depthstr);
			if (depth % 2 == 0) {
				depth++;
			}
			if (depth > 17 || depth < 1) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println("Too deep, defaulting to 17.");
			depth = 17;
		}
		boolean stats = true;
		if (alone) {
			System.out.print("Show algorithm's stats? ");
			String boolstr = scanner.nextLine();
			for (int i = 0; i < boolstr.length(); i++) {
				if (boolstr.charAt(i) == 'n') {
					stats = false;
				}
			}
		}
		players = new Player[count];
		for (int i = 0; i < count; i++) {
			players[i] = new Player(colors[i]);
		}
		game = new Game(players, rules);
		board = game.board;

		game.printBoard();
		while (!game.isGameOver()) {
			int die = random.nextInt(6) + 1;
			Player player = players[game.currentPlayer];
			System.out.print(player.name + "'s turn, " + "die rolled " + die + ". ");
			boolean play = false;
			for (int i = 0; i < 4; i++) {
				if (rules.isMoveValid(player.pieces[i], die, board)) {
					play = true;
				}
			}
			if (!play) {
				System.out.println("No moves available.");
				game.currentPlayer = (game.currentPlayer + 1) % players.length;
				continue;
			}
			if (alone && game.currentPlayer != 0) {
				System.out.println();
				int[] c = { 0, 0 };
				if (die != 6) {
					for (int i = 0; i < 4; i++) {
						if (player.pieces[i].i == player.pieces[i].base) {
							c[0]++;
						} else {
							c[1] = i;
						}
					}
				}
				if (c[0] != 3) {
					long stime = System.nanoTime();
					int[] move = expectiminimax(depth, false, die);
					long etime = System.nanoTime();
					game.playMove((char) move[4], die);
					if (stats) {
						System.out.println("execution time: " + ((etime - stime) / 1000000) + "ms");
						System.out.println("nodes visited: " + nodes);
						System.out.println("leaf's player: " + color[leaf] + leaf + reset);
						System.out.println("leaf's evaluation: " + color[leaf] + leafeval + reset);
						System.out.print("node's evaluation: [");
						for (int j = 0; j < 4; j++) {
							System.out.print(color[j] + move[j] + reset + (j == 3 ? "]\n" : ", "));
						}
					}
				} else {
					game.playMove(player.pieces[c[1]].ui, die);
				}
				game.printBoard();
				nodes = 0;
			} else {
				System.out.println("Enter which piece to move: ");
				while (true) {
					String str = scanner.nextLine();
					if (str.length() == 0) continue;
					if (game.playMove(str.charAt(0), die) != -1) break;
				}
				game.printBoard();
			}
		}
		scanner.close();
		System.out.println("Game over! Final scores:");
		for (Player player : players) {
			System.out.println(player.name + ": " + player.score());
		}
	}

	static int[] expectiminimax(int depth, boolean chance, int die) {
		int[] val = { 0, 0, 0, 0, 0 };
		if (depth == 0 || game.isGameOver()) {
			for (int l = 0; l < players.length; l++) {
				for (int i = 0; i < 4; i++) {
					OnePiece piece = players[l].pieces[i];
					if (piece.i != piece.base) {
						val[l] += 99;
					} else {
						continue;
					}
					if (piece.i >= piece.homestart) {
						val[l] += 73;
						continue;
					}
					if (piece.i % 13 == 0 || board.blocks.containsKey(board.cells[piece.i])) {
						val[l] += 37;
					}

					for (int j = 1; j < 7; j++) {
						int back = piece.i - j;
						if (back < 0) {
							back += 52;
						}
						char cellui = board.cells[back];
						if (cellui == ' ') {
							val[l] += 3;
							continue;
						}
						String block = board.blocks.get(cellui);
						if (block == null) {
							OnePiece piece2 = board.pieces[cellui - 'a'];
							if (piece2.end == piece.end) {
								val[l] += 7;
							} else {
								val[l] -= 30; 
							}
						} else for (int k = 0; k < block.length(); k++) {
							OnePiece piece2 = board.pieces[block.charAt(k) - 'a'];
							if (piece2.end == piece.end) {
								val[l] += 13;
							} else {
								val[l] -= 17; 
							}
						}
					}
					for (int j = 1; j < 7; j++) {
						int front = piece.i + j;
						if (front > 51) {
							front -= 52;
						}
						if (board.cells[front] != ' ') {
							val[l] += 70;
						}
					}
				}
			}
			for (int i = 0; i < players.length; i++) {
				for (int j = 0; j < 4; j++) {
					if (players[i].pieces[j].i == players[i].pieces[j].base) {
						for (int k = 0; k < players.length; k++) {
							if (k != i) {
								val[k] += 99;
							}
						}
					}
				}
			}
			leaf = game.currentPlayer;
			leafeval = val[leaf];
			return val;
		}
		if (chance) {
			for (int i = 1; i < 7; i++) {
				int[] emm = expectiminimax(depth - 1, false, i);
				for (int j = 0; j < 4; j++) {
					val[j] += emm[j] / 6;
				}
			}
			return val;
		}
		int player = game.currentPlayer;
		int[][] vals = new int[4][5];
		for (int i = 0; i < 4; i++) {
			OnePiece piece = game.players[player].pieces[i];
			if (!rules.isMoveValid(piece, die, board)) {
				continue;
			}
			nodes++;
			int oldindex = piece.i;
			char oldcell = board.cells[oldindex];
			String oldblock = board.blocks.get(oldcell);
			int newIndex = board.newIndex(piece, die);
			char newcell = board.cells[newIndex];
			String newblock = board.blocks.get(newcell);

			int g = game.botPlayMove(piece, die);
			int[] emm = expectiminimax(depth - 1, true, die);

			for (int j = 0; j < 4; j++) {
				vals[i][j] = emm[j];
			}
			vals[i][4] = (int) piece.ui;

			piece.i = oldindex;
			game.currentPlayer = player;
			char newcell2 = board.cells[newIndex];
			board.cells[oldindex] = oldcell;
			board.cells[newIndex] = newcell;

			if (newblock != null) {
				board.blocks.put(newcell, newblock);
			} else {
				board.blocks.remove(newcell2);
			}
			if (oldblock != null) {
				board.blocks.put(oldcell, oldblock);
			}
			if (g != 0) {
				OnePiece piece2 = board.pieces[board.cells[g] - 'a'];
				board.cells[g] = ' ';
				piece2.i = newIndex;
			}
		}
		for (int i = 0; i < 4; i++) {
			if (vals[i][player] >= val[player]) {
				val = vals[i];
			}
		}
		return val;
	}
}