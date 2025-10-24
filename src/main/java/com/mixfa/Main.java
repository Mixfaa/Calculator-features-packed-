package com.mixfa;

import com.mixfa.calculator.MathParserBuilder;
import com.mixfa.calculator.exception.MathParsingException;


public class Main {

    public static void main() throws MathParsingException {

//        Parser2.tokenize2("1+32+sin(25)+(-12)+rand()").forEach(System.out::println);

        var parser = new MathParserBuilder().addDefaults().build();
//
        IO.println(parser.parse("1/2").calculate());
        IO.println(parser.parse("2^2^2").calculate());
        IO.println(parser.parse("lcm(25,15)").calculate());
        IO.println(parser.parse("1-(-1)").calculate());

        IO.println(parser.parse("(1/3)+2").calculate());
        IO.println(parser.parse("2.5+3.5").calculate());
        IO.println(parser.parse("(1/3)+2").calculate());
        IO.println(parser.parse("(1/3)+(2/6)").calculate());
        IO.println(parser.parse("(1/3)*(1/3)").calculate());
        IO.println(parser.parse("(1/3)").calculate());
        IO.println(parser.parse("gcd(3,6)").calculate());


    }
}
