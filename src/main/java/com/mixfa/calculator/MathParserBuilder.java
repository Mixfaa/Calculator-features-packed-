package com.mixfa.calculator;

import ch.obermuhlner.math.big.BigDecimalMath;
import com.mixfa.calculator.functions.GreatestCommonDivisorFunction;
import com.mixfa.calculator.functions.LowestCommonMultipleFunction;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class MathParserBuilder {
    private final List<FunctionComponent> functions = new ArrayList<>();
    private final List<MathConstant> constants = new ArrayList<>();
    protected static final List<FunctionComponent> DEFAULT_FUNCTIONS = List.of(
            new FunctionComponent.FunctionComponent1("sin", val -> ValueFactory.toValue(BigDecimalMath.sin(val.asBigDecimal(), MathContext.DECIMAL128))),
            new FunctionComponent.FunctionComponent1("cos", val -> ValueFactory.toValue(BigDecimalMath.cos(val.asBigDecimal(), MathContext.DECIMAL128))),
            new FunctionComponent.FunctionComponent1("tan", val -> ValueFactory.toValue(BigDecimalMath.tan(val.asBigDecimal(), MathContext.DECIMAL128))),
            new FunctionComponent.FunctionComponent1("sqrt", val -> ValueFactory.toValue(BigDecimalMath.sqrt(val.asBigDecimal(), MathContext.DECIMAL128))),
            new FunctionComponent.FunctionComponent2("pow", (arg1, arg2) -> ValueFactory.toValue(BigDecimalMath.pow(arg1.asBigDecimal(), arg2.asBigDecimal(), MathContext.DECIMAL128))),
            GreatestCommonDivisorFunction.greatestCommonDivisor(),
            LowestCommonMultipleFunction.lowestCommonMultiple()
    );

    protected static final List<MathConstant> DEFAULT_CONSTANTS = List.of(
            new MathConstant("e", new MathComponent.Value.BigDecimalValue(new BigDecimal(String.valueOf(Math.E)))),
            new MathConstant("pi", new MathComponent.Value.BigDecimalValue(new BigDecimal(String.valueOf(Math.PI))))
    );

    public MathParserBuilder addDefaults() {
        addDefaultFunctions();
        return addDefaultConstants();
    }

    public MathParserBuilder addFunctions(FunctionComponent... functions) {
        this.functions.addAll(List.of(functions));
        return this;
    }

    public MathParserBuilder addConstants(MathConstant... constants) {
        this.constants.addAll(List.of(constants));
        return this;
    }

    public MathParserBuilder addDefaultFunctions() {
        functions.addAll(DEFAULT_FUNCTIONS);
        return this;
    }

    public MathParserBuilder addDefaultConstants() {
        constants.addAll(DEFAULT_CONSTANTS);
        return this;
    }

    public MathParserBuilder addFunction(FunctionComponent function) {
        functions.add(function);
        return this;
    }

    public MathParserBuilder addConstant(MathConstant constant) {
        constants.add(constant);
        return this;
    }

    public MathParser build() {
        return new MathParser(functions.toArray(FunctionComponent[]::new), constants.toArray(MathConstant[]::new));
    }
}
