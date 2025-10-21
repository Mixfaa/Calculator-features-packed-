package com.mixfa.calculator.functions;

import com.mixfa.calculator.FunctionComponent;
import com.mixfa.calculator.MathComponent;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

import static com.mixfa.calculator.ValueFactory.toValue;

public class LowestCommonMultipleFunction {
    private static final Supplier<FunctionComponent.FunctionComponent2> FUNCTION = StableValue.supplier(
            () -> new FunctionComponent.FunctionComponent2("lcm", LowestCommonMultipleFunction::lowestCommonMultiple)
    );

    public static FunctionComponent.FunctionComponent2 lowestCommonMultiple() {
        return FUNCTION.get();
    }

    public static MathComponent.Value findLCM(BigInteger a, BigInteger b) {
        if (a.compareTo(BigInteger.ZERO) == 0 || b.compareTo(BigInteger.ZERO) == 0) {
            return MathComponent.Value.BigIntValue.zero();
        }

        return toValue(a.multiply(b).abs().divide(GreatestCommonDivisorFunction.findGCDRecursive(a, b)));
    }

    public static MathComponent.Value findLCM(BigDecimal a, BigDecimal b) {
        if (a.compareTo(BigDecimal.ZERO) == 0 || b.compareTo(BigDecimal.ZERO) == 0) {
            return MathComponent.Value.BigIntValue.zero();
        }

        return toValue(a.multiply(b).abs().divide(GreatestCommonDivisorFunction.findGCDRecursive(a, b)));
    }

    public static MathComponent.Value lowestCommonMultiple(MathComponent.Value a, MathComponent.Value b) {
        if (a.isZero() || b.isZero()) {
            return MathComponent.Value.BigIntValue.zero();
        }

        if (a instanceof MathComponent.Value.BigIntValue && b instanceof MathComponent.Value.BigIntValue)
            return findLCM(a.asBigInteger(), b.asBigInteger());

        return findLCM(a.asBigDecimal(), b.asBigDecimal());
    }
}
