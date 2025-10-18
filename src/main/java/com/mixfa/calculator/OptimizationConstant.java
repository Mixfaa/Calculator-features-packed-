package com.mixfa.calculator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.BigInteger;

@RequiredArgsConstructor
@Accessors(fluent = true)
@Getter
public enum OptimizationConstant {
    ZERO(BigInteger.ZERO, BigDecimal.ZERO),
    ONE(BigInteger.ONE, BigDecimal.ONE),
    MINUS_ONE(BigInteger.ONE.negate(), BigDecimal.ONE.negate()),;

    private final BigInteger intValue;
    private final BigDecimal decimalValue;
}
