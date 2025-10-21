package com.mixfa.calculator;

import com.mixfa.calculator.functions.GreatestCommonDivisorFunction;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface ValueFactory {
    public static MathComponent.Value zero() {
        return MathComponent.Value.BigIntValue.zero();
    }

    public static MathComponent.Value one() {
        return MathComponent.Value.BigIntValue.one();
    }

    public static MathComponent.Value ratio(MathComponent.Value newNumerator, MathComponent.Value newDenominator) {
        if (newNumerator.isZero())
            return MathComponent.Value.BigIntValue.zero();
        if (newNumerator.compareTo(newDenominator) == 0)
            return MathComponent.Value.BigIntValue.one();
        if (newDenominator.equalsConstant(OptimizationConstant.ONE))
            return newNumerator;

        var divisor = GreatestCommonDivisorFunction.greatestCommonDivisor(newNumerator, newDenominator);
        if (!divisor.equalsConstant(OptimizationConstant.ONE))
            return new MathComponent.Value.RatioValue(
                    MathUtils.divide(newNumerator, divisor),
                    MathUtils.divide(newDenominator, divisor)
            );

        return new MathComponent.Value.RatioValue(
                newNumerator,
                newDenominator
        );
    }

    public static MathComponent.Value toValue(BigDecimal value) {
        if (value.scale() == 0)
            return new MathComponent.Value.BigIntValue(value.toBigInteger());

        return new MathComponent.Value.BigDecimalValue(value);
    }

    public static MathComponent.Value toValue(double value) {
        return toValue(new BigDecimal(String.valueOf(value)));
    }

    public static MathComponent.Value toValue(long value) {
        return new MathComponent.Value.BigIntValue(new BigInteger(String.valueOf(value)));
    }

    public static MathComponent.Value toValue(String value) {
        return toValue(new BigDecimal(value));
    }

    public static MathComponent.Value toValue(BigInteger value) {
        return new MathComponent.Value.BigIntValue(value);
    }
}
