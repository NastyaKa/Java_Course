"use strict";

const cnst = value => () => value;

const variable = variable => (...args) => args[variable.charCodeAt(0) - 120];

const oper = operation => (...funcs) => (...params) => operation(...funcs.map(func => func(...params)));

const add = (a, b) => oper((x, y) => x + y)(a, b);
const subtract = (a, b) => oper((x, y) => x - y)(a, b);
const multiply = (a, b) => oper((x, y) => x * y)(a, b);
const divide = (a, b) => oper((x, y) => x / y)(a, b);
const negate = (a) => oper((x) => -x)(a);
const avg3 = (a, b, c) => oper((x, y, z) => (x + y + z) / 3)(a, b, c);
const med5 = oper((...a) => a.sort((x, y) => x - y)[2]);
const pi = () => Math.PI;
const e = () => Math.E;

const OPERATIONS = {
    "+" : [add],
    "-" : [subtract],
    "*" : [multiply], 
    "/" : [divide],
    "negate": [negate],
    "avg3" : [avg3], 
    "med5" : [med5, 5]
};

const VASR = {
    "x" : 0, 
    "y" : 1, 
    "z" : 2
};

const consts = {
    "pi" : pi,
    "e" : e
};

let parse = (expr) => {
    let stack = [];
    
    expr.split(" ").forEach(token => {
        if (token in OPERATIONS) {
            let curOper = OPERATIONS[token];
            stack.push(curOper[0](...stack.splice(-curOper[0].length === 0 ? -curOper[1] : -curOper[0].length)));
        } else if (token in VASR) {
            stack.push(variable(token));
        } else if (token in consts) {
            stack.push(consts[token]);
        } else if (token !== "") {
            stack.push(cnst(+token));
        }
    });

    return stack.pop();
};

println(parse("x x 2 - * x * 1 +")(5));
let primer = parse("x x * 2 x * - 1 +");
for (let i = 0; i < 11; i++) {
    println(primer(i));
}

