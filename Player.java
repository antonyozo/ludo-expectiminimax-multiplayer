public class Player {
	public final OnePiece[] pieces = new OnePiece[4];
	public final String name;
	public int sixes;

	public Player(String name) {
		this.name = name;
	}

	public int score() {
		int score = 0;
		for (OnePiece piece : pieces) {
			if (piece.i == piece.homeend) {
				score++;
			}
		}
		return score;
	}
}
