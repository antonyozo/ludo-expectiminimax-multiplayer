public class OnePiece {
	public final char ui;
	public final int homestart;
	public final int homeend;
	public final int start;
	public final int end;
	public final int base;
	public int i;

	public OnePiece(int homestart, int start, int base, char ui) {
		this.homestart = homestart;
		this.homeend = homestart + 5;
		this.start = start;
		this.end = start == 0 ? 50 : start - 2;
		this.base = base;
		this.i = base;
		this.ui = ui;
	}
}