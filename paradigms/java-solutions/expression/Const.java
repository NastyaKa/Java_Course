package expression;

public class Const implements CurrentExpression {
    private Number cnst;

    public Const(Number cnst) {
        this.cnst = cnst;
    }

    public Const(int cnst) {
        this.cnst = cnst;
    }

    public int getConst() {
        return cnst.intValue();
    }

    public Number getAnyConst() {
        return cnst;
    }

    @Override
    public int evaluate(int x) {
        return getConst();
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return getConst();
    }

    @Override 
    public int priority() {
        return 10;
    }

    @Override
    public boolean surround() {
        return false;
    }

    @Override
    public String toString() {
        return cnst.toString();
    }

    @Override
    public boolean equals(Object toCheck) {
        if (toCheck == null || toCheck.getClass() != this.getClass()) {
            return false;
        }
        Const check = (Const) toCheck;
        return this.getAnyConst().equals(check.getAnyConst());
    }

    @Override
    public int hashCode() {
        String hsh = getAnyConst().toString();
        int ans = 0;
        for (int i = 0; i < hsh.length(); i++) {
            ans = ans * 1367 + hsh.charAt(i);
        }
        return ans;
    }
}
