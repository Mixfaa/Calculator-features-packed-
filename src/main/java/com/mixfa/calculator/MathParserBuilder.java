package com.mixfa.calculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MathParserBuilder {
    private final List<FunctionComponent> functions = new ArrayList<>();
    private final List<MathConstant> constants = new ArrayList<>();
    protected static final List<FunctionComponent> DEFAULT_FUNCTIONS = List.of(
            new FunctionComponent.FunctionComponent1("sin", val -> new MathComponent.MathFunc1(val, Math::sin)),
            new FunctionComponent.FunctionComponent1("cos", val -> new MathComponent.MathFunc1(val, Math::cos)),
            new FunctionComponent.FunctionComponent1("tan", val -> new MathComponent.MathFunc1(val, Math::tan)),
            new FunctionComponent.FunctionComponent1("sqrt", val -> new MathComponent.MathFunc1(val, Math::sqrt)),
            new FunctionComponent.FunctionComponent2("pow", (arg1, arg2) -> new MathComponent.MathFunc2(arg1, arg2, Math::pow))
    );

    protected static final List<MathConstant> DEFAULT_CONSTANTS = List.of(
            new MathConstant("e", BigDecimal.valueOf(Math.E)),
            new MathConstant("pi", BigDecimal.valueOf(Math.PI))
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
