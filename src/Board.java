import java.util.HashMap;

public class Board {
    public final HashMap<Character, String> blocks = new HashMap<>();
    public final OnePiece[] pieces = new OnePiece[16];
    public final char[] cells = new char[92];
    public final String blocksUI = "STUVWXYZ";

    public Board() {
        String uis = "abcdefghijklmnop";
        int base = 76;
        int home = 52;
        int j = 0;
        for (int start = 0; start < 13 * 4; start += 13) {
            for (int i = 0; i < 4; i++) {
                pieces[j] = new OnePiece(home, start, base++, uis.charAt(j++));
            }
            home += 6;
        }

        for (int i = 0; i < cells.length; i++) {
            this.cells[i] = ' ';
        }
        for (int i = 0; i < 4 * 4; i += 4) {
            this.cells[this.pieces[i].homeEnd] = '0';
        }
        for (int i = 0; i < pieces.length; i++) {
            this.cells[this.pieces[i].i] = this.pieces[i].ui;
        }
    }

    public int newIndex(OnePiece piece, int die) {
        int newIndex = piece.i + die;
        if (piece.start != 0) {
            if (newIndex > 51 && newIndex < 58) {
                return newIndex - 52;
            }
        }
        if (piece.i <= piece.end) {
            if (newIndex > piece.end) {
                return piece.homeStart + newIndex - piece.end - 1;
            }
        } else if (piece.i == piece.base) {
            return piece.start;
        }
        return newIndex;
    }

    public int[] move(OnePiece piece, int die) {
        int newIndex = this.newIndex(piece, die);
        char cellUI = cells[piece.i];
        if (cellUI == piece.ui) {
            cells[piece.i] = ' ';
        } else {
            String block = blocks.get(cellUI);
            String newBlock = "";
            blocks.remove(cellUI);
            for (int i = 0; i < block.length(); i++) {
                if (block.charAt(i) != piece.ui) {
                    newBlock += block.charAt(i);
                }
            }
            if (newBlock.length() == 1) {
                cells[piece.i] = newBlock.charAt(0);
            } else {
                blocks.put(cellUI, newBlock);
            }
        }

        piece.i = newIndex;
        cellUI = cells[newIndex];
        if (cellUI == ' ') {
            cells[newIndex] = piece.ui;
            return new int[]{ -1 };
        } else if (newIndex == piece.homeEnd) {
            cells[newIndex] = (char) (cells[newIndex] + 1);
            return new int[]{ -1 };
        }
        String block = blocks.get(cellUI);
        if (block == null) {
            OnePiece piece2 = this.pieces[cellUI - 'a'];
            if (newIndex % 13 == 0 || piece2.end == piece.end) {
                for (int i = 0; i < this.blocksUI.length(); i++) {
                   char c = this.blocksUI.charAt(i);
                   if (!this.blocks.containsKey(c)) {
                       this.blocks.put(c, "" + piece.ui + cellUI);
                       cells[newIndex] = c;
                       break;
                   }
               }
            } else {
                piece2.i = piece2.base;
                cells[piece2.i] = piece2.ui;
                cells[newIndex] = piece.ui;
                return new int[]{ piece2.i };
            }
        } else {
            if (newIndex % 13 == 0 || this.pieces[block.charAt(0) - 'a'].end == piece.end) {
                blocks.put(cellUI, block + piece.ui);
            } else {
                int[] piecesindices = new int[block.length()];
                for (int i = 0; i < piecesindices.length; i++) {
                    OnePiece piece2 = this.pieces[block.charAt(i) - 'a'];
                    piece2.i = piece2.base;
                    cells[piece2.i] = piece2.ui;
                    piecesindices[i] = piece2.i;
                }
                cells[newIndex] = piece.ui;
                blocks.remove(cellUI);
                return piecesindices;
            }
        }
        return new int[]{ -1 };
    }
}

