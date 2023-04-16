package expression;

import javax.swing.plaf.synth.SynthDesktopIconUI;

import expression.exceptions.*;
import expression.exceptions.errors.CalculatingException;

public class Main {
    public static void main(String[] args) {
        Parser cur = new ExpressionParser();
        try {
            // System.out.println((1 >> -1));
            System.out.println(cur.parse("4 ** 10").evaluate(0, 0, 0));
        } catch (CalculatingException e) {
            System.out.println(e.getMessage());
        } 
    }
}
