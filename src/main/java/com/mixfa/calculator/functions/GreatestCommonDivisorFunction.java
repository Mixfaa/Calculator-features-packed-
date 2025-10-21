package com.mixfa.calculator.functions;

import com.mixfa.calculator.FunctionComponent;
import com.mixfa.calculator.MathComponent;
import com.mixfa.calculator.MathComponent.Value.BigIntValue;
import com.mixfa.calculator.ValueFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

public class GreatestCommonDivisorFunction {
    private static final Supplier<FunctionComponent.FunctionComponent2> FUNCTION = StableValue.supplier(
            () -> new FunctionComponent.FunctionComponent2("gcd", GreatestCommonDivisorFunction::greatestCommonDivisor)
    );

    public static FunctionComponent.FunctionComponent2 greatestCommonDivisor() {
        return FUNCTION.get();
    }

    public static BigInteger findGCDRecursive(BigInteger a, BigInteger b) {
        if (b.compareTo(BigInteger.ZERO) == 0) return a;

        return findGCDRecursive(b, a.remainder(b));
    }

    public static BigDecimal findGCDRecursive(BigDecimal a, BigDecimal b) {
        if (b.compareTo(BigDecimal.ZERO) == 0) return a;

        return findGCDRecursive(b, a.remainder(b));
    }

    public static MathComponent.Value greatestCommonDivisor(MathComponent.Value a, MathComponent.Value b) {
        if (b.isZero()) return a;

        if (a instanceof BigIntValue && b instanceof BigIntValue)
            return ValueFactory.toValue(findGCDRecursive(b.asBigInteger(), a.asBigInteger()));

        return ValueFactory.toValue(findGCDRecursive(a.asBigDecimal(), b.asBigDecimal()));
    }
}
