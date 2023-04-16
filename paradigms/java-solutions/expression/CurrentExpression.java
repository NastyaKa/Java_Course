package expression;

public interface CurrentExpression extends Expression, TripleExpression {
    int priority();
    boolean surround();
}

