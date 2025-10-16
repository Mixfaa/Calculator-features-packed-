package com.mixfa;

import com.mixfa.calculator.FunctionComponent;
import com.mixfa.calculator.MathComponent;
import com.mixfa.calculator.MathConstant;
import com.mixfa.calculator.MathParserBuilder;

import java.math.BigDecimal;
import java.util.Random;

public class Main {
    static void main() throws Exception {
        var parser = new MathParserBuilder()
                .addDefaults()
                .addConstant(
                        new MathConstant("x", BigDecimal.valueOf(500))
                )
                .addFunctions(
                        new FunctionComponent.FunctionComponent1("negate", arg -> new MathComponent.Value(
                                arg.calculate().negate()
                        )),
                        new FunctionComponent.FunctionComponent0("rand", () -> new MathComponent.Value(
                                BigDecimal.valueOf(new Random().nextDouble())
                        ))
                )
                .build();
        IO.println(
                parser.parse("negate(rand())+negate(x)").calculate()
        );
    }
}
