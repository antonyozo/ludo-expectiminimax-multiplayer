public class Game {
    public final Board board;
    public final Player[] players;
    public final Rules rules;
    public int currentPlayer;

    public Game(Player[] players, Rules rules) {
        this.board = new Board();
        this.players = players;
        for (int i = 0; i < players.length * 4; i++) {
            players[i / 4].pieces[i % 4] = board.pieces[i];
        }
        this.rules = rules;
        this.currentPlayer = 0;
    }

    public int[] playMove(char pieceui, int die) {
        int index = pieceui - 'a';
        OnePiece piece = null;
        if (index >= 0 && index < 16) {
            piece = board.pieces[index];
        }
        if (piece == null
            || piece.start / 13 != this.currentPlayer
            || !rules.isMoveValid(piece, die, this.board)
            ) {
            System.out.println("Invalid move. Try again.");
            return new int[]{ -2 };
        }
        int[] m = this.board.move(piece, die);
        Player player = this.players[this.currentPlayer];
        if (die != 6 || player.sixes == 2) {
            player.sixes = 0;
            if (m[0] == -1) {
                this.currentPlayer = (this.currentPlayer + 1) % this.players.length;
            }
        } else {
            player.sixes++;
        }
        return m;
    }

    public int[] botPlayMove(OnePiece piece, int die) {
        return this.board.move(piece, die);
    }

    public boolean isGameOver() {
        return this.rules.isGameOver(this.board);
    }

    private String char2color(char c, String[] colors, String reset) {
        String block = this.board.blocks.get(c);
        if (block == null) {
            return "" + c;
        }
        int[] color = new int[block.length()];
        for (int j = 0; j < color.length; j++) {
            color[j] = this.board.pieces[block.charAt(j) - 'a'].start;
        }
        for (int j = 1; j < color.length; j++) {
            if (color[j - 1] != color[j]) {
                return colors[0] + c + reset;
            } 
        }
        for (int j = 1; j < 5; j++) {
            if (block.charAt(0) - 'a' < j * 4) {
                return colors[j] + c + reset;
            }
        }
        return "";
    }

    public void printBoard() {
        String reset = "\u001B[0m";
        String[] colors = {
            "\u001B[1m",
            "\u001B[94m",
            "\u001B[91m",
            "\u001B[92m",
            "\u001B[93m"
        };
        char[] cells = this.board.cells;
        String[] c = new String[cells.length];
        for (int i = 0; i < c.length; i++) {
            if (cells[i] - 'a' < 0) {
                c[i] = this.char2color(cells[i], colors, reset);
            } else for (int j = 1; j < 5; j++) {
                if (cells[i] - 'a' < j * 4) {
                    c[i] = colors[j] + cells[i] + reset;
                    break;
                }
            }
        }
        System.out.println("+ ~ ~ ~ ~ ~ ~ + ~ ~ ~ + ~ ~ ~ ~ ~ ~ +");
        System.out.println("|             | " + c[10] + " " + c[11] + " " + c[12] + " |             |");
        System.out.println("|   " + c[76] + "     " + c[77] + "   | " + c[9] + " " + c[58] + " " + c[13] + " " + colors[2] + "|" + reset + "   " + c[80] + "     " + c[81] + "   |");
        System.out.println("|             | " + c[8] + " " + c[59] + " " + c[14] + " |             |");
        System.out.println("|             | " + c[7] + " " + c[60] + " " + c[15] + " |             |");
        System.out.println("|   " + c[79] + "     " + c[78] + "   | " + c[6] + " " + c[61] + " " + c[16] + " |   " + c[83] + "     " + c[82] + "   |");
        System.out.println("|             | " + c[5] + " " + c[62] + " " + c[17] + " |             |");
        System.out.println("+ ~ " + colors[1] + "~" + reset + " ~ ~ ~ ~ + ~ " + colors[2] + "~" + reset + " ~ + ~ ~ ~ ~ ~ ~ +");
        System.out.println("| " + c[51] + " " + c[0] + " " + c[1] + " " + c[2] + " " + c[3] + " " + c[4] + " |   " + colors[2] + c[63] + reset + "   | " + c[18] + " " + c[19] + " " + c[20] + " " + c[21] + " " + c[22] + " " + c[23] + " |");
        System.out.println("| " + c[50] + " " + c[52] + " " + c[53] + " " + c[54] + " " + c[55] + " " + c[56] + " " + colors[1] + "| " + c[57] + reset + "   " + colors[3] + c[69] + " |" + reset + " " + c[68] + " " + c[67] + " " + c[66] + " " + c[65] + " " + c[64] + " " + c[24] + " |");
        System.out.println("| " + c[49] + " " + c[48] + " " + c[47] + " " + c[46] + " " + c[45] + " " + c[44] + " |   " + colors[4] + c[75] + reset + "   | " + c[30] + " " + c[29] + " " + c[28] + " " + c[27] + " " + c[26] + " " + c[25] + " |");
        System.out.println("+ ~ ~ ~ ~ ~ ~ + ~ " + colors[4] + "~" + reset + " ~ + ~ ~ ~ ~ " + colors[3] + "~" + reset + " ~ +");
        System.out.println("|             | " + c[43] + " " + c[74] + " " + c[31] + " |             |");
        System.out.println("|   " + c[88] + "     " + c[89] + "   | " + c[42] + " " + c[73] + " " + c[32] + " |   " + c[84] + "     " + c[85] + "   |");
        System.out.println("|             | " + c[41] + " " + c[72] + " " + c[33] + " |             |");
        System.out.println("|             | " + c[40] + " " + c[71] + " " + c[34] + " |             |");
        System.out.println("|   " + c[91] + "     " + c[90] + "   " + colors[4] + "|" + reset + " " + c[39] + " " + c[70] + " " + c[35] + " |   " + c[87] + "     " + c[86] + "   |");
        System.out.println("|             | " + c[38] + " " + c[37] + " " + c[36] + " |             |");
        System.out.println("+ ~ ~ ~ ~ ~ ~ + ~ ~ ~ + ~ ~ ~ ~ ~ ~ +");
        boolean print = true;
        for (int i = 0; i < this.board.blocksUI.length(); i++) {
            char key = this.board.blocksUI.charAt(i);
            String block = this.board.blocks.get(key);
            if (block == null) {
                continue;
            }
            String blockUI = this.char2color(key, colors, reset);
            String content = "";
            for (int k = 0; k < block.length(); k++) {
                for (int j = 1; j < 5; j++) {
                    if (block.charAt(k) - 'a' < j * 4) {
                        content += colors[j] + block.charAt(k) + reset;
                        content += k == block.length() - 1 ? "." : ", ";
                        break;
                    }
                }
            }
            if (print) {
                System.out.println("blocks:");
                print = false;
            }
            System.out.println(blockUI + " => " + content);
        }
    }
}