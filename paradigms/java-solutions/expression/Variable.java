package expression;

import expression.exceptions.errors.CalculatingException;

public class Variable implements CurrentExpression {
    private String variable;

    public Variable(String variable) {
        this.variable = variable;
    }

    public String getVar() {
        return variable;
    }
    
    @Override
    public int evaluate(int x) {
        return x;
    }

    @Override
    public int evaluate(int x, int y, int z) throws CalculatingException {
        switch (variable) {
            case "x" : 
                return x;
            case "y" :
                return y;
            case "z" :
                return z;
            default:
                throw new CalculatingException("not a valid variable: " + variable);
        }
    }

    @Override
    public String toString() {
        return variable;
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
    public boolean equals(Object toCheck) {
        if (toCheck == null || toCheck.getClass() != this.getClass()) {
            return false;
        }
        Variable check = (Variable) toCheck;
        return this.toString().equals(check.toString());
    }

    @Override
    public int hashCode() {
        int hsh = 0;
        for (int i = 0; i < variable.length(); i++) {
            hsh = hsh * 1367 + (variable.charAt(i) - '0');
        }
        return hsh;
    }
}
