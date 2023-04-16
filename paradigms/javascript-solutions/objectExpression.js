"use strict";

const Exception = function (exception, Message) {
    const result = function (...args) {
        this.message = Message(...args);
        this.exception = exception;
    };
    result.prototype = new Error;
    return result;
};
const MissedOperationException = Exception(
    "MissedOperationException",
    index => "Missed operation at index " + index
);
const InvalidOperandNumber = Exception(
    "InvalidOperandNumber",
    (operand, index) => "Invalid operands number for " + operand + " at index " + index
);
const EmptyExpressionException = Exception(
    "EmptyExpressionException",
    () => "Empty expression, nothing to parse"
);
const MissedCloseBracketException = Exception(
    "MissedCloseBracketException",
    index => "Missed close bracket at index " + index
);
const MissedOpenBracketException = Exception(
    "MissedOpenBracketException",
    index => "Missed open bracket at index " + index
);
const IrregularSymbolException = Exception(
    "IrregularSymbolException",
    (symbol, index) => "Unexpected symbol <" + symbol + "> at index " + index
);

function Const(value) {
    this.value = value;
}

Const.prototype.evaluate = function () {
    return this.value;
};
Const.prototype.toString = function () {
    return this.value.toString();
};
Const.prototype.diff = function () {
    return ZERO;
};
Const.prototype.prefix = function () {
    return this.toString();
};
Const.prototype.postfix = function () {
    return this.toString();
};

const ZERO = new Const(0);
const ONE = new Const(1);
const TWO = new Const(2);
const E = new Const(Math.E);

function Variable(variable) {
    this.variable = variable;
    this.pos = VARS[variable];
}

Variable.prototype.evaluate = function (...args) {
    return args[this.pos];
};
Variable.prototype.toString = function () {
    return this.variable;
};
Variable.prototype.diff = function (dx) {
    return dx === this.variable ? ONE : ZERO;
};
Variable.prototype.prefix = function () {
    return this.toString();
};
Variable.prototype.postfix = function () {
    return this.toString();
};

function Operation(...args) {
    this.args = args;
}

Operation.prototype.toString = function () {
    return this.args.join(" ") + " " + this.getSign;
};
Operation.prototype.evaluate = function (...arg) {
    return this.oper(...this.args.map(cur => cur.evaluate(...arg)));
};
Operation.prototype.diff = function (dx) {
    return this.deter(dx, ...this.args);
};
Operation.prototype.prefix = function () {
    return "(" + this.getSign + " " + this.args.map(cur => cur.prefix()).join(" ") + ")";
};
Operation.prototype.postfix = function () {
    return "(" + this.args.map(cur => cur.postfix()).join(" ") + " " + this.getSign + ")";
};

const creator = function (sign, operation, deter) {
    const creature = function (...args) {
        Operation.call(this, ...args);
    };
    creature.prototype = new Operation;
    creature.prototype.getSign = sign;
    creature.prototype.oper = operation;
    creature.prototype.deter = deter;
    creature.getLen = operation.length;
    return creature;
};

const Add = creator(
    "+",
    (a, b) => a + b,
    (dx, a, b) => new Add(a.diff(dx), b.diff(dx))
);
const Subtract = creator(
    "-",
    (a, b) => a - b,
    (dx, a, b) => new Subtract(a.diff(dx), b.diff(dx))
);
const Multiply = creator(
    "*",
    (a, b) => a * b,
    (dx, a, b) => new Add(new Multiply(a.diff(dx), b), new Multiply(a, b.diff(dx)))
);
const Divide = creator(
    "/",
    (a, b) => a / b,
    (dx, a, b) => new Divide(new Subtract(new Multiply(a.diff(dx), b),
            new Multiply(a, b.diff(dx))),
        new Multiply(b, b))
);
const Negate = creator(
    "negate",
    a => -a,
    (dx, a) => new Negate(a.diff(dx))
);
const Gauss = creator(
    "gauss",
    (a, b, c, x) => a * Math.pow(E, -((x - b) * (x - b) / (TWO * c * c))),
    (dx, a, b, c, x) =>
        new Multiply(new Add(new Multiply(a,
                    new Divide(new Multiply(new Subtract(x, b),
                            new Subtract(b, x)),
                        new Multiply(new Multiply(c, c),
                            TWO)).diff(dx)),
                a.diff(dx)),
            new Gauss(ONE, b, c, x)
        )
);
const PowE = creator(
    "powE",
    a => Math.pow(E, a),
    (dx, a) => new Multiply(new PowE(a), a.diff(dx))
);
const Sumexp = creator(
    "sumexp",
    (...opers) => opers.reduce((sum, oper) => sum + Math.pow(E, oper), 0),
    (dx, ...opers) => sumExpExpr(...opers).diff(dx)
);
const Softmax = creator(
    "softmax",
    (...args) => {
        if (args.length !== 0) {
            return Math.pow(E, args[0]) / Sumexp.prototype.oper(...args);
        } else {
            return 1;
        }
    },
    (dx, ...args) => new Divide(new PowE(args[0]), sumExpExpr(...args)).diff(dx)
);
const sumExpExpr = (...args) => {
    if (args.length !== 0) {
        return args.reduce((sum, operation) => new Add(sum, new PowE(operation)), ZERO);
    } else {
        return ZERO;
    }
};

const OPERATIONS = {
    "+": Add,
    "-": Subtract,
    "*": Multiply,
    "/": Divide,
    "negate": Negate,
    "gauss": Gauss,
    "sumexp": Sumexp,
    "softmax": Softmax
};
const MULTI = {
    "sumexp": Sumexp,
    "softmax": Softmax
};
const VARS = {
    "x": 0,
    "y": 1,
    "z": 2
};

const WHITESPACE = new Set([' ', '\f', '\n', '\r', '\t', '\v', '\u00A0', '\u2028', '\u2029']);
const SEPR = new Set(['(', ')']);
const PREF = "prefix";
const POST = "postfix";

function parse(expr) {
    const stack = [];
    expr.split(" ").forEach(token => {
        if (token in OPERATIONS) {
            const curOper = OPERATIONS[token];
            stack.push(new curOper(...stack.splice(-curOper.getLen)));
        } else if (token in VARS) {
            stack.push(new Variable(token));
        } else if (token !== "") {
            stack.push(new Const(+token));
        }
    });
    return stack.pop();
}

class exprparser {
    constructor(str, typ) {
        this.str = str;
        this.typ = typ;
        this.ind = 0;
    }
    isEmpty() {
        return this.ind === this.str.length;
    }
    skipWhitespace() {
        while (!this.isEmpty() && WHITESPACE.has(this.str[this.ind])) {
            this.ind++;
        }
        return true;
    }
    take(ch) {
        if (!this.isEmpty() && ch === this.str[this.ind]) {
            this.ind++;
            return true;
        }
        return false;
    }
    getNext() {
        this.skipWhitespace();
        let saved = "";
        while (!this.isEmpty() && !WHITESPACE.has(this.str[this.ind]) && !SEPR.has(this.str[this.ind])) {
            saved += this.str[this.ind++];
        }
        return saved;
    }
}
function anyParser(expr, typ) {
    const pars = new exprparser(expr, typ);
    const ans = myParse(pars);
    if (pars.skipWhitespace() && !pars.isEmpty()) {
        throw new IrregularSymbolException(pars.getNext(), pars.ind);
    }
    return ans;
}
function parsePrefix(expr) {
    return anyParser(expr, PREF);
}
function parsePostfix(expr) {
    return anyParser(expr, POST);
}

function isNum(str) {
    for (let i = 1; i < str.length; i++) {
        if ('0' > str[i] || str[i] > '9') {
            return false;
        }
    }
    return (str !== "" && (str[0] !== '-' || str.length > 1) && (str[0] === '-' || '0' <= str[0] && str[0] <= '9'));
}

function myParse(pars) {
    if (pars.skipWhitespace() && !pars.isEmpty()) {
        if (pars.take(")")) {
            throw new MissedOpenBracketException(pars.ind);
        } else if (pars.take("(")) {
            const stack = [];
            let operation;
            if (pars.typ === PREF) {
                operation = pars.getNext();
            }
            let cur = "";
            while (pars.skipWhitespace() && pars.str[pars.ind] !== ")") {
                cur = parseNextOperand(pars);
                if (cur in OPERATIONS) {
                    break;
                }
                stack.push(cur);
            }
            if (pars.typ === POST) {
                operation = cur;
            }
            if (!(operation in OPERATIONS)) {
                throw new MissedOperationException(pars.ind);
            }
            const curOper = OPERATIONS[operation];
            if (pars.skipWhitespace() && !pars.take(')')) {
                throw new MissedCloseBracketException(pars.ind);
            } else if (!(operation in MULTI) && stack.length !== curOper.getLen) {
                throw new InvalidOperandNumber(operation, pars.ind);
            }
            return new curOper(...stack.splice(-curOper.getLen));
        }
        return parseNextOperand(pars);
    }
    throw new EmptyExpressionException();
}

function parseNextOperand(pars) {
    const token = pars.getNext();
    if (token in VARS) {
        return new Variable(token);
    } else if (isNum(token)) {
        return new Const(+token);
    } else if (token === "" && !pars.isEmpty() && pars.str[pars.ind] === "(") {
        return myParse(pars);
    } else if (pars.typ === POST && token in OPERATIONS) {
        return token;
    }
    throw (pars.isEmpty() ? new MissedCloseBracketException(pars.ind) : new IrregularSymbolException(token, pars.ind));
}

