package md2html;

public class MyPair<L, R> {
    private L x;
    private R y;

    public MyPair(L x, R y) {
        this.x = x;
        this.y = y;
    }

    public L getX() {
        return x;
    }

    public R getY() {
        return y;
    }
}
