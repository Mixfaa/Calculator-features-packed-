package com.mixfa.calculator.functions;

import com.mixfa.calculator.FunctionComponent;
import com.mixfa.calculator.MathComponent;

import java.math.BigInteger;
import java.util.function.Supplier;

import static com.mixfa.calculator.MathUtils.toValue;

public class LowestCommonMultipleFunction {
    private static final Supplier<FunctionComponent.FunctionComponent2> FUNCTION = StableValue.supplier(
            () -> new FunctionComponent.FunctionComponent2("lcm", LowestCommonMultipleFunction::lowestCommonMultiple)
    );

    public static FunctionComponent.FunctionComponent2 lowestCommonMultiple() {
        return FUNCTION.get();
    }

    public static BigInteger findLCM(BigInteger a, BigInteger b) {
        if (a.equals(BigInteger.ZERO) || b.equals(BigInteger.ZERO)) {
            return BigInteger.ZERO;
        }

        return a.multiply(b).abs().divide(GreatestCommonDivisorFunction.findGCDRecursive(a, b));
    }

    public static MathComponent.Value lowestCommonMultiple(MathComponent.Value a, MathComponent.Value b) {
        return toValue(findLCM(a.calculate().asBigInteger(), b.calculate().asBigInteger()));
    }
}
