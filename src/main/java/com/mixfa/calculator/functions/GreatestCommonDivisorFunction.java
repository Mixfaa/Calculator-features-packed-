package com.mixfa.calculator.functions;

import com.mixfa.calculator.FunctionComponent;
import com.mixfa.calculator.MathComponent;

import java.math.BigInteger;
import java.util.function.Supplier;

import static com.mixfa.calculator.MathUtils.toValue;

public class GreatestCommonDivisorFunction {
    private static final Supplier<FunctionComponent.FunctionComponent2> FUNCTION = StableValue.supplier(
            () -> new FunctionComponent.FunctionComponent2("hcf", GreatestCommonDivisorFunction::greatestCommonDivisor)
    );

    public static FunctionComponent.FunctionComponent2 greatestCommonDivisor() {
        return FUNCTION.get();
    }

    public static BigInteger findGCDRecursive(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) return a;

        return findGCDRecursive(b, a.remainder(b));
    }

    public static MathComponent.Value greatestCommonDivisor(MathComponent.Value a, MathComponent.Value b) {
        return toValue(findGCDRecursive(a.calculate().asBigInteger(), b.calculate().asBigInteger()));
    }
}
