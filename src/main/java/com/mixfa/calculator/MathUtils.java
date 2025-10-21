package com.mixfa.calculator;

import ch.obermuhlner.math.big.BigDecimalMath;
import com.mixfa.calculator.functions.GreatestCommonDivisorFunction;
import com.mixfa.calculator.functions.LowestCommonMultipleFunction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static com.mixfa.calculator.MathComponent.Value.*;

public class MathUtils {
    private MathUtils() {
    }

    private static class RatioUtils {

        public static Value optimizeRatio(Value newNumerator, Value newDenominator) {
            if (newNumerator.isZero())
                return BigIntValue.zero();
            if (newNumerator.compareTo(newDenominator) == 0)
                return BigIntValue.one();
            if (newDenominator.equalsConstant(OptimizationConstant.ONE))
                return newNumerator;

            var divisor = GreatestCommonDivisorFunction.greatestCommonDivisor(newNumerator, newDenominator);
            if (!divisor.equalsConstant(OptimizationConstant.ONE))
                return new RatioValue(
                        MathUtils.divide(newNumerator, divisor),
                        MathUtils.divide(newDenominator, divisor)
                );

            return new RatioValue(
                    newNumerator,
                    newDenominator
            );
        }

        public static RatioValue valueAsRatio(Value value) {
            if (value instanceof RatioValue)
                return (RatioValue) value;

            return new RatioValue(value, BigIntValue.one());
        }

        public static Value add(RatioValue r1, RatioValue r2) {
            if (r1.denominator().compareTo(r2.denominator()) == 0)
                return optimizeRatio(MathUtils.add(r1.numerator(), r2.numerator()), r1.denominator());

            var newDenominator = LowestCommonMultipleFunction.lowestCommonMultiple(r1.denominator(), r2.denominator());

            var mult1 = MathUtils.divide(r1.denominator(), newDenominator);
            var mult2 = MathUtils.divide(r2.denominator(), newDenominator);

            var numerator = MathUtils.add(
                    mult1.equalsConstant(OptimizationConstant.ONE) ? r1.numerator() : MathUtils.divide(r1.numerator(), mult1),
                    mult2.equalsConstant(OptimizationConstant.ONE) ? r2.numerator() : MathUtils.divide(r2.numerator(), mult2)
            );

            return optimizeRatio(numerator, newDenominator);
            /*
            if (r1.denominator().compareTo(r2.denominator()) == 0)
                return optimizeRatio(MathUtils.add(r1.numerator(), r2.numerator()), r1.denominator());

            IO.println(r1.denominator());
            IO.println(r2.denominator());
            var newDenominator = GreatestCommonDivisorFunction.greatestCommonDivisor(r1.denominator(), r2.denominator());

            var mult1 = MathUtils.divide(r1.denominator(), newDenominator);
            var mult2 = MathUtils.divide(r2.denominator(), newDenominator);

            var numerator = MathUtils.add(
                    mult1.equalsConstant(OptimizationConstant.ONE) ? r1.numerator() : MathUtils.divide(r1.numerator(), mult1),
                    mult2.equalsConstant(OptimizationConstant.ONE) ? r2.numerator() : MathUtils.divide(r2.numerator(), mult2)
            );

            return optimizeRatio(numerator, newDenominator);
             */
        }

        public static Value subtract(RatioValue r1, RatioValue r2) {
            if (r1.denominator().compareTo(r2.denominator()) == 0)
                return optimizeRatio(MathUtils.subtract(r1.numerator(), r2.numerator()), r1.denominator());

            var newDenominator = GreatestCommonDivisorFunction.greatestCommonDivisor(r1.denominator(), r2.denominator());

            var mult1 = MathUtils.divide(r1.denominator(), newDenominator);
            var mult2 = MathUtils.divide(r2.denominator(), newDenominator);

            var numerator = MathUtils.subtract(
                    MathUtils.divide(r1.numerator(), mult1),
                    MathUtils.divide(r2.numerator(), mult2)
            );

            return optimizeRatio(numerator, newDenominator);
        }

        public static Value multiply(Value n1, Value d1, Value n2, Value d2) {
            var numerator = MathUtils.multiply(n1, n2);
            var denominator = MathUtils.multiply(d1, d2);

            var gcd = GreatestCommonDivisorFunction.greatestCommonDivisor(numerator, denominator);

            numerator = MathUtils.divide(numerator, gcd);
            denominator = MathUtils.divide(denominator, gcd);

            return optimizeRatio(numerator, denominator);
        }

        public static Value multiply(RatioValue r1, RatioValue r2) {
            return multiply(r1.numerator(), r1.denominator(), r2.numerator(), r2.denominator());
        }

        public static Value divide(RatioValue r1, RatioValue r2) {
            return multiply(r1.numerator(), r1.denominator(), r2.denominator(), r2.numerator());
        }
    }

    public static Value add(Value a, Value b) {
        if (a.isZero())
            return b;
        if (b.isZero())
            return a;

        if (a.compareTo(b.negate()) == 0)
            return BigIntValue.zero();

        if (a instanceof RatioValue || b instanceof RatioValue)
            return RatioUtils.add(
                    RatioUtils.valueAsRatio(a),
                    RatioUtils.valueAsRatio(b)
            );

        if (a instanceof BigIntValue && b instanceof BigIntValue)
            return toValue(a.asBigInteger().add(b.asBigInteger()));

        return toValue(a.asBigDecimal().add(b.asBigDecimal()));
    }

    public static Value subtract(Value a, Value b) {
        if (a.isZero())
            return b.negate();
        if (b.isZero())
            return a;

        if (a.compareTo(b) == 0)
            return BigIntValue.zero();

        if (a instanceof RatioValue || b instanceof RatioValue)
            return RatioUtils.subtract(
                    RatioUtils.valueAsRatio(a),
                    RatioUtils.valueAsRatio(b)
            );

        if (a instanceof BigIntValue && b instanceof BigIntValue)
            return toValue(a.asBigInteger().subtract(b.asBigInteger()));

        return toValue(a.asBigDecimal().subtract(b.asBigDecimal()));
    }

    public static Value divide(Value a, Value b) {
        if (a.isZero()) return BigIntValue.zero();
        if (b.isZero()) throw new ArithmeticException("Division by zero");

        if (b.equalsConstant(OptimizationConstant.ONE))
            return a;
        if (b.equalsConstant(OptimizationConstant.MINUS_ONE))
            return a.negate();

        if (a.compareTo(b) == 0)
            return BigIntValue.one();

        if (a instanceof RatioValue || b instanceof RatioValue)
            return RatioUtils.divide(
                    RatioUtils.valueAsRatio(a),
                    RatioUtils.valueAsRatio(b)
            );

        if (a instanceof BigIntValue && b instanceof BigIntValue) {
            var repeating = isRepeatingDecimal(a.asBigInteger(), b.asBigInteger());
            if (!repeating) {
                var remainderRemains = a.asBigInteger().remainder(b.asBigInteger()).compareTo(BigInteger.ZERO) != 0;

                if (remainderRemains)
                    return toValue(a.asBigDecimal().divide(b.asBigDecimal(), MathContext.DECIMAL128));
                return toValue(a.asBigInteger().divide(b.asBigInteger()));
            }
        } else {
            var repeating = isRepeatingDecimal(a.asBigDecimal(), b.asBigDecimal());
            if (!repeating)
                return toValue(a.asBigDecimal().divide(b.asBigDecimal(), MathContext.DECIMAL128));
        }

        return new RatioValue(a, b);
    }

    public static Value multiply(Value a, Value b) {
        if (a.isZero() || b.isZero()) return BigIntValue.zero();

        if (a.equalsConstant(OptimizationConstant.ONE))
            return b;
        if (b.equalsConstant(OptimizationConstant.ONE))
            return a;

        if (a.equalsConstant(OptimizationConstant.MINUS_ONE))
            return b.negate();
        if (b.equalsConstant(OptimizationConstant.MINUS_ONE))
            return a.negate();

        if (a instanceof RatioValue || b instanceof RatioValue)
            return RatioUtils.multiply(
                    RatioUtils.valueAsRatio(a),
                    RatioUtils.valueAsRatio(b)
            );

        if (a instanceof BigIntValue && b instanceof BigIntValue)
            return toValue(a.asBigInteger().multiply(b.asBigInteger()));

        return toValue(a.asBigDecimal().multiply(b.asBigDecimal()));
    }

    public static Value power(Value a, Value b) {
        if (a.isZero()) return BigIntValue.zero();
        if (b.isZero()) return BigIntValue.one();

        if (b.equalsConstant(OptimizationConstant.ONE))
            return a;

        if (a instanceof BigIntValue && b instanceof BigIntValue)
            return toValue(a.asBigInteger().pow(b.asBigInteger().intValue()));

        return toValue(BigDecimalMath.pow(a.asBigDecimal(), b.asBigDecimal(), MathContext.DECIMAL128));
    }

    public static Value toValue(BigDecimal value) {
        if (value.scale() == 0)
            return new BigIntValue(value.toBigInteger());

        return new BigDecimalValue(value);
    }

    public static Value toValue(double value) {
        return toValue(new BigDecimal(String.valueOf(value)));
    }

    public static Value toValue(long value) {
        return new BigIntValue(new BigInteger(String.valueOf(value)));
    }

    public static Value toValue(String value) {
        return toValue(new BigDecimal(value));
    }

    public static Value toValue(BigInteger value) {
        return new BigIntValue(value);
    }


    public static boolean isRepeatingDecimal(BigDecimal a, BigDecimal b) {
        if (b.signum() == 0) {
            throw new ArithmeticException("Division by zero");
        }
        if (a.signum() == 0) {
            return false; // 0 / b = 0 (конечная)
        }

        // 1. Получаем немасштабированные значения (Unscaled Values)
        BigInteger m = a.unscaledValue().abs();
        BigInteger n = b.unscaledValue().abs();

        // 2. Сокращаем знаменатель
        BigInteger common = m.gcd(n);
        BigInteger reducedN = n.divide(common);

        // 3. Удаляем множители 2 и 5

        // Удалить все 2 (проверка на четность)
        BigInteger two = BigInteger.valueOf(2);
        while (!reducedN.testBit(0)) { // testBit(0) == false означает четное
            reducedN = reducedN.divide(two);
        }

        // Удалить все 5
        BigInteger five = BigInteger.valueOf(5);
        while (reducedN.remainder(five).compareTo(BigInteger.ZERO) == 0) {
            reducedN = reducedN.divide(five);
        }

        // 4. Проверяем остаток
        // Если оставшийся знаменатель > 1, значит, есть другие простые множители
        return reducedN.compareTo(BigInteger.ONE) > 0;
    }

    public static boolean isRepeatingDecimal(BigInteger a, BigInteger b) {
        if (b.signum() == 0) {
            throw new ArithmeticException("Division by zero");
        }
        if (a.signum() == 0) {
            return false; // 0 / N = 0 (конечная)
        }

        // 1. Сокращение дроби
        BigInteger gcd = a.gcd(b);
        BigInteger reducedN = b.divide(gcd).abs(); // Берем абсолютное значение

        // 2. Удаление множителей 2 и 5
        BigInteger two = BigInteger.valueOf(2);
        BigInteger five = BigInteger.valueOf(5);

        // Удалить все 2
        // Используем testBit(0) для быстрой проверки на четность (testBit(0) == false)
        while (reducedN.compareTo(BigInteger.ONE) != 0 && !reducedN.testBit(0)) {
            reducedN = reducedN.divide(two);
        }

        // Удалить все 5
        while (reducedN.remainder(five).compareTo(BigInteger.ZERO) == 0) {
            reducedN = reducedN.divide(five);
        }

        // 3. Проверка результата
        // Если остался множитель > 1, то это периодическая дробь.
        return reducedN.compareTo(BigInteger.ONE) > 0;
    }
}
