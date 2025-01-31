public class Rules {
    public boolean isMoveValid(OnePiece piece, int die, Board board) {
        if (piece.i == piece.base) {
            if (die == 6) {
                return true;
            }
            return false;
        }
        if (piece.i + die > piece.homeEnd) {
            return false;
        }
        for (int i = 1; i < die; i++) {
            int index = board.newIndex(piece, i);
            String block = board.blocks.get(board.cells[index]);
            if (block == null) {
                continue;
            }
            if (index % 13 == 0) {
                int[] color = new int[block.length()];
                for (int j = 0; j < color.length; j++) {
                    color[j] = board.pieces[block.charAt(j) - 'a'].start;
                }
                for (int j = 0; j < color.length; j++) {
                    for (int k = j + 1; k < color.length; k++) {
                        if (color[j] == color[k] && color[j] != piece.start) {
                            return false;
                        }
                    }
                }
            } else if (board.pieces[block.charAt(0) - 'a'].start != piece.start) {
                return false;
            }
        }
        return true;
    }

    public boolean isGameOver(Board board) {
        int fours = 0;
        for (int i = 0; i < 4 * 4; i += 4) {
            if (board.cells[board.pieces[i].homeEnd] == '4') {
                fours++;
            }
        }
        return fours == 4;
    }
}