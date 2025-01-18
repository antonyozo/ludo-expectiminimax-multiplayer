import java.util.HashMap;

public class Board {
	public final HashMap<Character, String> blocks = new HashMap<>();
	public final OnePiece[] pieces = new OnePiece[16];
	public final char[] cells = new char[92];
	public final String blocksui = "STUVWXYZ";

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
			this.cells[this.pieces[i].homeend] = '0';
		}
		for (int i = 0; i < pieces.length; i++) {
			this.cells[this.pieces[i].i] = this.pieces[i].ui;
		}
	}

	public int newIndex(OnePiece piece, int die) {
		int index = piece.i + die;
		if (piece.start != 0) {
			int diff = index - 51;
			if (diff > 0 && diff < 7) {
				return diff - 1;
			}
		}
		if (piece.i <= piece.end) {
			int diff = index - piece.end;
			if (diff > 0) {
				return piece.homestart + diff - 1;
			}
		} else if (piece.i == piece.base) {
			return piece.start;
		}
		return index;
	}

	public int move(OnePiece piece, int die) {
		int index = this.newIndex(piece, die);
		char cellui = cells[piece.i];
		if (cellui == piece.ui) {
			cells[piece.i] = ' ';
		} else {
			String block = blocks.get(cellui);
			String newblock = "";
			blocks.remove(cellui);
			for (int i = 0; i < block.length(); i++) {
				if (block.charAt(i) != piece.ui) {
					newblock += block.charAt(i);
				}
			}
			if (newblock.length() == 1) {
				cells[piece.i] = newblock.charAt(0);
			} else {
				blocks.put(cellui, newblock);
			}
		}

		piece.i = index;
		cellui = cells[index];
		if (cellui == ' ') {
			cells[index] = piece.ui;
			return 0;
		} else if (index == piece.homeend) {
			cells[index] = (char) (cells[index] + 1);
			return 0;
		}
		String block = blocks.get(cellui);
		if (block == null) {
			OnePiece piece2 = this.pieces[cellui - 'a'];
			if (index % 13 == 0 || piece2.end == piece.end) {
				for (int i = 0; i < blocksui.length(); i++) {
					char c = blocksui.charAt(i);
					if (!blocks.containsKey(c)) {
						blocks.put(c, piece.ui + "" + cellui);
						cells[index] = c;
						break;
					}
				}
			} else {
				piece2.i = piece2.base;
				cells[piece2.i] = piece2.ui;
				cells[index] = piece.ui;
				return piece2.i;
			}
		} else {
			blocks.put(cellui, block + piece.ui);
		}
		return 0;
	}
}

