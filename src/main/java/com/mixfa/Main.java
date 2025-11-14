package com.mixfa;

import com.mixfa.calculator.MathParser;

public class Main {

    static void main() throws Exception {
        var parser = MathParser.defaultParser();
        var a = parser.parseInput("1-(-1)");
        IO.println(a.toString());
        IO.println(
                a.calculate()
        );
    }
}
