public class OnePiece {
    public final char ui;
    public final int homeStart;
    public final int homeEnd;
    public final int start;
    public final int end;
    public final int base;
    public int i;

    public OnePiece(int homeStart, int start, int base, char ui) {
        this.homeStart = homeStart;
        this.homeEnd = homeStart + 5;
        this.start = start;
        this.end = start == 0 ? 50 : start - 2;
        this.base = base;
        this.i = base;
        this.ui = ui;
    }
}