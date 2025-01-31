import java.util.Scanner;
import java.util.Random;

public class Main {
    static final Scanner scanner = new Scanner(System.in);
    static final Random random = new Random();
    static final Rules rules = new Rules();
    static final String[] colors = { "Blue", "Red", "Green", "Yellow" };
    static final String reset = "\u001B[0m";
    static final String[] color = {
        "\u001B[94m",
        "\u001B[91m",
        "\u001B[92m",
        "\u001B[93m"
    };
    static Player[] players;
    static Algorithm algo;
    static Board board;
    static Game game;

    public static final void main(String[] args) {
        System.out.print("Enter players count: ");
        int count;
        try {
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
            System.out.print("Enter algorithm's depth (max: 5): ");
            String depthstr = scanner.nextLine();
            depth = Integer.valueOf(depthstr);
            if (depth % 2 == 0) {
                depth++;
            }
            if (depth > 5 || depth < 1) {
                throw new Exception();
            }
        } catch (Exception e) {
            System.out.println("Invalid, defaulting to 3.");
            depth = 3;
        }
        boolean stats = false;
        if (alone) {
            System.out.print("Show algorithm's stats? ");
            String boolstr = scanner.nextLine();
            for (int i = 0; i < boolstr.length(); i++) {
                if (boolstr.charAt(i) == 'y') {
                    stats = true;
                }
            }
        }
        players = new Player[count];
        for (int i = 0; i < count; i++) {
            players[i] = new Player(colors[i]);
        }
        game = new Game(players, rules);
        board = game.board;
        algo = new Algorithm(players, rules, board, game);
        game.printBoard();
        while (!game.isGameOver()) {
            Player player = players[game.currentPlayer];
            if (board.cells[player.pieces[0].homeEnd] == '4') {
                game.currentPlayer = (game.currentPlayer + 1) % players.length;
                continue;
            }
            int die = random.nextInt(6) + 1;
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
                if (die == 6) {
                    int reroll = random.nextInt(6) + 1;
                    System.out.println("rerolled " + reroll);
                    die += reroll;
                }
                if (die == 12) {
                    int reroll = random.nextInt(6) + 1;
                    System.out.println("rerolled " + reroll);
                    die += reroll;
                }
                long stime = System.nanoTime();
                int[] move = algo.expectiminimax(depth, false, die);
                long etime = System.nanoTime();
                if (die < 6) {
                    game.playMove((char) move[4], die);
                } else if (die < 12) {
                    game.playMove((char) move[4], 6);
                    game.playMove((char) move[5], die - 6);
                } else {
                    game.playMove((char) move[4], 6);
                    game.playMove((char) move[5], 6);
                    game.playMove((char) move[6], die - 12);
                }
                if (stats) {
                    System.out.println("execution time: " + ((etime - stime) / 1000000) + "ms");
                    System.out.println("nodes visited: " + algo.nodes);
                    System.out.println("leaf's player: " + color[algo.leaf] + (algo.leaf + 1) + reset);
                    System.out.println("leaf's evaluation: " + color[algo.leaf] + algo.leafeval + reset);
                    System.out.print("root's evaluation: [");
                    for (int j = 0; j < 4; j++) {
                        System.out.print(color[j] + move[j] + reset + (j == 3 ? "]\n" : ", "));
                    }
                }
                game.printBoard();
                algo.nodes = 0;
            } else {
                System.out.println("Enter which piece to move: ");
                while (true) {
                    String str = scanner.nextLine();
                    if (str.length() == 0) continue;
                    if (game.playMove(str.charAt(0), die)[0] != -2) break;
                }
                game.printBoard();
            }
        }
        scanner.close();
        System.out.println("gg");
    }
}