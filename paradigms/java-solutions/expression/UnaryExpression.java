package expression;

import expression.exceptions.errors.CalculatingException;

public abstract class UnaryExpression implements CurrentExpression {
    private CurrentExpression cur;

    public UnaryExpression(CurrentExpression cur) {
        this.cur = cur;
    }

    abstract protected String getSign();
    abstract protected int calculate(int a) throws CalculatingException;
    abstract public int priority();
    abstract public boolean surround();
    
    private CurrentExpression getExpr() {
        return cur;
    }
    
    @Override
    public int evaluate(int x) throws CalculatingException {
        return calculate(cur.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) throws CalculatingException {
        return calculate(cur.evaluate(x, y, z));
    }

    @Override
    public String toString() {
        return this.getSign() + "(" + cur.toString() + ")";
    }

    @Override
    public String toMiniString() {
        if (cur.priority() >= 3 || cur.getClass() == this.getClass()) {
            return this.getSign() + " " + cur.toMiniString();
        } else {
            return this.getSign() + "(" + cur.toMiniString() + ")";
        }
    }

    @Override
    public boolean equals(Object toCheck) {
        if (toCheck == null || toCheck.getClass() != this.getClass()) {
            return false;
        }
        UnaryExpression check = (UnaryExpression) toCheck;
        return this.getExpr().equals(check.getExpr());
    }

    @Override
    public int hashCode() {
        return getSign().hashCode() + 71 * getExpr().hashCode();
    }
}
