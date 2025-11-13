package com.mixfa;

import com.mixfa.calculator.MathParser;
import com.mixfa.calculator.MathParserBuilder;
import com.mixfa.calculator.exception.MathParsingException;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;

@Fork(value = 1)
@Warmup(iterations = 2, time = 5)
@Measurement(iterations = 2, time = 5)
public class Main {

    private static final MathParser parser = new MathParserBuilder().addDefaults().build();

    @Benchmark
    public static void test1() throws MathParsingException {
//
        parser.parse("1/2").calculate();
        parser.parse("2^2^2").calculate();
        parser.parse("lcm(25,15)").calculate();
        parser.parse("1-(-1)").calculate();
        parser.parse("(sin(1)*cos(1))*0").calculate();
        parser.parse("(sin(1)^cos(cos(1)^cos(1)))*0").calculate();
        parser.parse("(1/3)+2").calculate();
        parser.parse("2.5+3.5").calculate();
        parser.parse("(2.5+3.5+cos(1)^3)*0").calculate();
        parser.parse("(1/3)+2").calculate();
        parser.parse("(1/3)+(2/6)").calculate();
        parser.parse("(1/3)*(1/3)").calculate();
        parser.parse("(1/3)").calculate();
        parser.parse("gcd(3,6)").calculate();
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}
