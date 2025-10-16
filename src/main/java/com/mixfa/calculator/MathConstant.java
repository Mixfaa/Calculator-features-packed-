package com.mixfa.calculator;

import java.math.BigDecimal;

public record MathConstant(
        String name,
        MathComponent.Value value
) {
    public MathConstant(String name, BigDecimal v) {
        this(name, new MathComponent.Value(v));
    }
}
