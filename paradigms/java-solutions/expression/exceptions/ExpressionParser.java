package expression.exceptions;

import java.util.ArrayList;
import java.util.List;
import expression.exceptions.errors.*;
import expression.*;

public class ExpressionParser implements TripleParser {
    public CurrentExpression parse(final String source) throws CalculatingException {
        CurrentExpression a = null;
        try {
            a = parse(new StringCharSource(source));
        } catch (ParsingExceptions e) {
            throw new CalculatingException("Impossible to parse: " + e.getMessage());
        }
        return a;
    }

    public CurrentExpression parse(final CharSource source) throws ParsingExceptions {
        return new ExprParser(source).parse();
    }

    private class ExprParser extends BaseParser {
        private int bal;

        public ExprParser(CharSource source) {
            super(source);
            bal = 0;
        }

        public CurrentExpression parse() throws ParsingExceptions {
            List<Object> expr = new ArrayList<>();
            skipWhitespace();
            while (!eof()) {
                if (take('(')) {
                    bal++;
                    expr.add(parse());
                } else if (take(')')) {
                    bal--;
                    if (bal < 0) {
                        throw new MissingOpenBracketException();
                    }
                    break;
                } else if (take('+')) {
                    expr.add("+");
                } else if (take('*')) {
                    if (take('*')) {
                        expr.add("**");
                    } else {
                        expr.add("*");
                    }
                } else if (take('/')) {
                    if (take('/')) {
                        expr.add("//");
                    } else {
                        expr.add("/");
                    }
                } else if (take('>')) {
                    if (take('>')) {
                        if (take('>')) {
                            expr.add(">>>");
                        } else {
                            expr.add(">>");
                        }
                    } else {
                        throw new NotValidOperationException(">" + peek());
                    }
                } else if (take('<')) {
                    if (take('<')) {
                        expr.add("<<");
                    } else {
                        throw new NotValidOperationException("<" + peek());
                    }
                } else if (take('l')) {
                    if (take('0')) {
                        expr.add(new L0(parseUnaryOper()));
                    } else {
                        throw new NotValidOperationException("l" + peek());
                    }
                } else if (take('t')) {
                    if (take('0')) {
                        expr.add(new T0(parseUnaryOper()));
                    } else {
                        throw new NotValidOperationException("t" + peek());
                    }
                } else if (take('a')) {
                    char sec = take();
                    char third = take();
                    if (sec == 'b' && third == 's' && (peek() == '(' || Character.isWhitespace(peek()))) {
                        expr.add(new CheckedAbs(parseUnaryOper()));
                    } else {
                        throw new NotValidOperationException("a" + sec + third + " probably somthing after");
                    }
                } else if (take('-')) {
                    if (expr.size() == 0 || 
                       (expr.get(expr.size() - 1) instanceof String && 
                       get_prior((String) expr.get(expr.size() - 1)) != -100)
                       ) {
                        expr.add(parseUnaryMinus());
                    } else if (between('0', '9') && 
                              (expr.get(expr.size() - 1) instanceof String && 
                              get_prior((String) expr.get(expr.size() - 1)) != -100)
                              ) {
                                if (take('0')) {
                                    expr.add(new CheckedNegate(new CheckedConst(0)));
                                } else {
                                    return parseConst(true);
                                }
                    } else {
                        expr.add("-");
                    }
                } else if (between('0', '9')) {
                    expr.add(parseConst(false));
                } else if (between('x', 'z')) {
                    expr.add(parseVar());
                } else {
                    throw new NotValidOperationException(String.valueOf(peek()));
                }
                skipWhitespace();
            }
            if (eof() && bal > 0) {
                throw new MissingCloseBracketException();
            }
            return simplify(expr);
        }

        private int get_prior(String ch) {
            switch (ch) {
                case "l0":
                    return 3;
                case "t0":
                    return 3;
                case "abs":
                    return 3;
                case "**":
                    return 2;
                case "//":
                    return 2;
                case "+":
                    return 0;
                case "-":
                    return 0;
                case "*":
                    return 1;
                case "/":
                    return 1;
                case ">>":
                    return -1;
                case ">>>":
                    return -1;
                case "<<":
                    return -1;
                default : 
                    return -100;
            }
        }

        private int get_expr(String ch) throws ParsingExceptions {
            switch (ch) {
                case "+":
                    return 0;
                case "-":
                    return 1;
                case "*":
                    return 2;
                case "/":
                    return 3;
                case ">>":
                    return 4;
                case ">>>":
                    return 5;
                case "<<":
                    return 6;
                case "l0":
                    return 7;
                case "t0":
                    return 8;
                case "**":
                    return 9;
                case "//": 
                    return 10;
                case "abs":
                    return 11;
                default : 
                    throw new UnknownOperandException(ch);
            }
        }

        private boolean isBinary(Object expr) {
            return (expr instanceof String && get_prior((String) expr) < 3 && get_prior((String) expr) != -100);
        }
        private boolean isUnary(Object expr) {
            return (expr instanceof String && get_prior((String) expr) >= 3);
        }

        private boolean isOperation(Object expr) {
            return (expr instanceof String && get_prior((String) expr) != -100);
        }
        
        private boolean isOperand(Object expr) {
            return !isOperation(expr);
        }

        private CurrentExpression simplify(List<Object> list) throws ParsingExceptions {
            for (int i = 0; i < list.size(); i++) {
                if (isBinary(list.get(i))) {
                    if (i == 0) {
                        throw new MissedOperandException("no first operand was found");
                    } else if (i == list.size() - 1) {
                        throw new MissedOperandException("no last operand was found");
                    } else if (isOperation(list.get(i - 1)) || isOperation(list.get(i + 1))) {
                        throw new MissedOperandException(list.get(i - 1).toString() + " " + list.get(i).toString() + " " + list.get(i + 1));
                    }
                } else if (isUnary(list.get(i))) {
                    if (i == list.size() - 1) {
                        throw new MissedOperandException("no last operand was found");
                    } else if (i + 1 < list.size() && isBinary(list.get(i + 1))) {
                        throw new MissedOperandException(list.get(i).toString() + " " + list.get(i + 1));
                    }
                } else if (isOperand(list.get(i)) && i + 1 < list.size() && isOperand(list.get(i + 1))) {
                    throw new MissedOperationException(list.get(i).toString() + " " + list.get(i + 1).toString());
                }
            }
            for (int prior = 3; prior >= -1; prior--) {
                boolean isNeg = false;
                List<Object> nw = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) instanceof String) {
                        if (get_prior((String) list.get(i)) == prior) {
                            int cur_expr = get_expr((String) list.get(i));
                            if (isNeg && cur_expr == 1 && (nw.size() == 0 || nw.get(nw.size() - 1) instanceof String)) {
                                isNeg = false;
                                if (i + 1 == list.size()) {
                                    throw new MissedOperandException("no operand after " + list.get(i).toString() + " was found");
                                }
                                nw.add(new CheckedNegate((CurrentExpression) list.get(i + 1)));
                            } else {
                                CurrentExpression first = null;
                                if (i + 1 == list.size()) {
                                    throw new MissedOperandException("no operand after " + list.get(i).toString() + " was found");
                                }
                                CurrentExpression second = (CurrentExpression) list.get(i + 1);
                                if (nw.size() > 0) {
                                    first = (CurrentExpression) nw.get(nw.size() - 1);
                                    nw.remove(nw.size() - 1);
                                }
                                if (cur_expr == 0) {
                                    nw.add(new CheckedAdd(first, second));
                                } else if (cur_expr == 1) {
                                    isNeg = true;
                                    nw.add(new CheckedSubtract(first, second));
                                } else if (cur_expr == 2) {
                                    nw.add(new CheckedMultiply(first, second));
                                } else if (cur_expr == 3) {
                                    nw.add(new CheckedDivide(first, second));
                                } else if (cur_expr == 4) {
                                    nw.add(new ShiftRight(first, second));
                                } else if (cur_expr == 5) {
                                    nw.add(new Shift(first, second));
                                } else if (cur_expr == 6) {
                                    nw.add(new ShiftLeft(first, second));
                                } else if (cur_expr == 7) {
                                    nw.add(new L0(second));
                                } else if (cur_expr == 8) {
                                    nw.add(new T0(second));
                                } else if (cur_expr == 9) {
                                    nw.add(new CheckedPow(first, second));
                                } else if (cur_expr == 10) {
                                    nw.add(new CheckedLog(first, second));
                                } else if (cur_expr == 11) {
                                    nw.add(new CheckedAbs(second));
                                }
                            }
                            i++;
                        } else {
                            nw.add(list.get(i));
                        } 
                    } else {
                        nw.add(list.get(i));
                    }
                }
                list = nw;
            }
            if (list.size() != 1) {
                throw new MissedOperationException("is not possible to simplify");
            }
            return (CurrentExpression) list.get(0);
        }

        private CurrentExpression parseConst(boolean neg) {
            String ans = "";
            while (!eof() && between('0', '9')) {
                ans += take();
            }
            try {
                if (neg) {
                    if (ans.equals("2147483648")) {
                        return new CheckedConst(Integer.parseInt("-" + ans));
                    } else {
                        return new CheckedConst(Integer.parseInt(ans) * -1);
                    }
                } else {
                    return new CheckedConst(Integer.parseInt(ans));
                }
            } catch (NumberFormatException e) {
                throw new OverflowException("Not a valid const: " + ans);
            }
        }

        private Variable parseVar() throws UnknownVariableException {
            String ans = "";
            boolean correct = true;
            while (!eof() && between('a', 'z')) {
                if (!between('x', 'z')) {
                    correct = false;
                }
                ans += take();
            }
            if (ans.length() != 1 || !correct) {
                throw new UnknownVariableException(ans);
            }
            return new Variable(ans);
        }

        private CurrentExpression parseUnaryMinus() throws ParsingExceptions {
            if (between('0', '9')) {
                if (take('0')) {
                    return new CheckedNegate(new CheckedConst(0));
                } else {
                    return parseConst(true);
                }
            } else if (between('x', 'z')) {
                return new CheckedNegate(parseVar());
            } else if (take('l')) {
                if (take('0')) {
                    return new CheckedNegate(new L0(parseUnaryOper()));
                } else {
                    throw new NotValidOperationException("l" + peek());
                }
            } else if (take('t')) {
                if (take('0')) {
                    return new CheckedNegate(new T0(parseUnaryOper()));
                } else {
                    throw new NotValidOperationException("t" + peek());
                }
            } else if (take('a')) {
                char sec = take(), third = take();
                if (sec == 'b' && third == 's') {
                    return new CheckedNegate(new CheckedAbs(parseUnaryOper()));
                } else {
                    throw new NotValidOperationException("a" + sec + third);
                }
            } else if (take('(')) {
                bal++;
                return new CheckedNegate(parse());
            } else if (eof() || (peek() != '-' && !Character.isWhitespace(peek()))) {
                throw new MissedOperandException("not a valid expression after < - >");
            } 
            skipWhitespace();
            if (!eof() && take('-')) {
                return new CheckedNegate(parseUnaryMinus());
            } else if (between('0', '9')) {
                // return new CheckedNegate(parseConst(true));
                return new CheckedNegate(parseConst(false));
            } else {
                return parseUnaryMinus();
            }
        }

        private CurrentExpression parseUnaryOper() throws ParsingExceptions {
            skipWhitespace();
            if (!eof() && take('l')) {
                if (take('0')) {
                    return new L0(parseUnaryOper());
                } else {
                    throw new NotValidOperationException("l" + peek());
                }
            } else if (take('t')) {
                if (take('0')) {
                    return new T0(parseUnaryOper());
                } else {
                    throw new NotValidOperationException("t" + peek());
                }
            } else if (take('a')) {
                char sec = take(), third = take();
                if (sec == 'b' && third == 's') {
                    return new CheckedAbs(parseUnaryOper());
                } else {
                    throw new NotValidOperationException("a" + sec + third);
                }
            } else if (between('0', '9')) {
                return parseConst(false);
            } else if (between('x', 'z')) {
                return parseVar();
            } else if (take('-')) {
                return parseUnaryMinus();
            } 
            if (take('(')) {
                bal++;
            }
            return parse();
        }

        private void skipWhitespace() {
            while (!eof() && Character.isWhitespace(peek())) {
                take();
            }
        }
    }
}

