package expression;

import expression.exceptions.errors.CalculatingException;

public abstract class AnyExpression implements CurrentExpression {
    private CurrentExpression first, second;

    public AnyExpression(CurrentExpression first, CurrentExpression second) {
        this.first = first;
        this.second = second;
    }

    abstract protected String getOperation();
    abstract protected int calculate(int a, int b) throws CalculatingException;
    abstract public int priority();
    abstract public boolean surround();
    
    @Override
    public int evaluate(int x) throws CalculatingException {
        return calculate(first.evaluate(x), second.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) throws CalculatingException {
        return calculate(first.evaluate(x, y, z), second.evaluate(x, y, z));
    }

    @Override 
    public String toString() {
        return "(" + first.toString() + getOperation() + second.toString() + ")";
    }

    private String getString(CurrentExpression cur, boolean surr) {
        if (surr) {
            return "(" + cur.toMiniString() + ")";
        }
        return cur.toMiniString();
    }

    @Override 
    public String toMiniString() {
        return getString(first, first.priority() < this.priority()) + getOperation() + 
                        getString(second, second.priority() < this.priority() 
                        || second.priority() == this.priority() && (second.surround() && this.surround()
                        || this.priority() == 1 && second.surround() && !this.surround()
                        || this.priority() <= 1 && this.surround()));
    }

    public boolean equals(Object toCheck) {
        if (toCheck == null || toCheck.getClass() != this.getClass()) {
            return false;
        }
        AnyExpression check = (AnyExpression) toCheck;
        return this.first.equals(check.first)
                && this.second.equals(check.second);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + 1367 * (second.hashCode() + 1367 * first.hashCode());
    }
}