package com.mixfa.calculator;

import ch.obermuhlner.math.big.BigDecimalMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static com.mixfa.calculator.MathComponent.Value;
import static com.mixfa.calculator.MathComponent.Value.*;

public class MathUtils {
    private MathUtils() {
    }

    public static Value add(Value a, Value b) {
        if (a.isZero())
            return b;
        if (b.isZero())
            return a;

        if (a.equals(b.negate()))
            return BigIntValue.zero();

        if (a instanceof BigIntValue && b instanceof BigIntValue)
            return toValue(a.asBigInteger().add(b.asBigInteger()));

        return toValue(a.asBigDecimal().add(b.asBigDecimal()));
    }

    public static Value subtract(Value a, Value b) {
        if (a.isZero())
            return b.negate();
        if (b.isZero())
            return a;

        if (a.equals(b))
            return BigIntValue.zero();

        if (a instanceof BigIntValue && b instanceof BigIntValue)
            return toValue(a.asBigInteger().subtract(b.asBigInteger()));

        return toValue(a.asBigDecimal().subtract(b.asBigDecimal()));
    }

    public static Value divide(Value a, Value b) {
        if (a.isZero()) return BigIntValue.zero();
        if (b.isZero()) throw new ArithmeticException("Division by zero");

        if (a.equals(b))
            return BigIntValue.one();

        return new RatioValue(a, b);
//
//        if (a instanceof BigIntValue && b instanceof BigIntValue) {
//            if (a.asBigInteger().remainder(b.asBigInteger()).equals(BigInteger.ZERO))
//                return toValue(a.asBigInteger().divide(b.asBigInteger()));
//
//            return toValue(a.asBigDecimal().divide(b.asBigDecimal(), 15, RoundingMode.HALF_EVEN));
//        }
//
//        return toValue(a.asBigDecimal().divide(b.asBigDecimal()));
    }

    public static Value multiply(Value a, Value b) {
        if (a.isZero() || b.isZero()) return BigIntValue.zero();

        if (a instanceof BigIntValue && b instanceof BigIntValue)
            return toValue(a.asBigInteger().multiply(b.asBigInteger()));

        return toValue(a.asBigDecimal().multiply(b.asBigDecimal()));
    }

    public static Value power(Value a, Value b) {
        if (a.isZero()) return BigIntValue.zero();
        if (b.isZero()) return BigIntValue.one();

        if (a instanceof BigIntValue && b instanceof BigIntValue)
            return toValue(a.asBigInteger().pow(b.asBigInteger().intValue()));

        return toValue(BigDecimalMath.pow(a.asBigDecimal(), b.asBigDecimal(), MathContext.DECIMAL128));
    }

    public static Value toValue(BigDecimal value) {
        return new BigDecimalValue(value);
    }

    public static Value toValue(BigInteger value) {
        return new BigIntValue(value);
    }
}
