package com.mixfa.calculator;

import ch.obermuhlner.math.big.BigDecimalMath;
import com.mixfa.calculator.functions.LowestCommonMultipleFunction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static com.mixfa.calculator.MathComponent.Value.*;
import static com.mixfa.calculator.ValueFactory.toValue;

public class MathUtils {
    private MathUtils() {
    }

    private static class RatioUtils {

        public static RatioValue valueAsRatio(Value value) {
            if (value instanceof RatioValue)
                return (RatioValue) value;

            return new RatioValue(value, ValueFactory.one());
        }

        public static Value add(RatioValue r1, RatioValue r2) {
            if (r1.denominator().compareTo(r2.denominator()) == 0)
                return ValueFactory.ratio(MathUtils.add(r1.numerator(), r2.numerator()), r1.denominator());

            var newDenominator = LowestCommonMultipleFunction.lowestCommonMultiple(r1.denominator(), r2.denominator());

            var mult1 = MathUtils.divide(r1.denominator(), newDenominator);
            var mult2 = MathUtils.divide(r2.denominator(), newDenominator);

            var numerator = MathUtils.add(
                    mult1.equalsConstant(OptimizationConstant.ONE) ? r1.numerator() : MathUtils.divide(r1.numerator(), mult1),
                    mult2.equalsConstant(OptimizationConstant.ONE) ? r2.numerator() : MathUtils.divide(r2.numerator(), mult2)
            );

            return ValueFactory.ratio(numerator, newDenominator);
        }

        public static Value subtract(RatioValue r1, RatioValue r2) {
            if (r1.denominator().compareTo(r2.denominator()) == 0)
                return ValueFactory.ratio(MathUtils.subtract(r1.numerator(), r2.numerator()), r1.denominator());

            var newDenominator = LowestCommonMultipleFunction.lowestCommonMultiple(r1.denominator(), r2.denominator());

            var mult1 = MathUtils.divide(r1.denominator(), newDenominator);
            var mult2 = MathUtils.divide(r2.denominator(), newDenominator);

            var numerator = MathUtils.subtract(
                    mult1.equalsConstant(OptimizationConstant.ONE) ? r1.numerator() : MathUtils.divide(r1.numerator(), mult1),
                    mult2.equalsConstant(OptimizationConstant.ONE) ? r2.numerator() : MathUtils.divide(r2.numerator(), mult2)
            );

            return ValueFactory.ratio(numerator, newDenominator);
        }

        public static Value multiply(Value n1, Value d1, Value n2, Value d2) {
            var numerator = MathUtils.multiply(n1, n2);
            var denominator = MathUtils.multiply(d1, d2);

            return ValueFactory.ratio(numerator, denominator);
        }

        public static Value multiply(RatioValue r1, RatioValue r2) {
            return multiply(r1.numerator(), r1.denominator(), r2.numerator(), r2.denominator());
        }

        public static Value divide(RatioValue r1, RatioValue r2) {
            return multiply(r1.numerator(), r1.denominator(), r2.denominator(), r2.numerator());
        }
    }

    public static Value add(MathComponent a, MathComponent b) {
        var optimizedValue = OptimizationUtils.add(a, b);
        if (optimizedValue != null) return optimizedValue.calculate();

        var aValue = a.calculate();
        var bValue = b.calculate();

        if (aValue instanceof RatioValue || bValue instanceof RatioValue)
            return RatioUtils.add(
                    RatioUtils.valueAsRatio(aValue),
                    RatioUtils.valueAsRatio(bValue)
            );

        if (aValue instanceof BigIntValue && bValue instanceof BigIntValue)
            return toValue(aValue.asBigInteger().add(bValue.asBigInteger()));

        return toValue(aValue.asBigDecimal().add(bValue.asBigDecimal()));
    }

    public static Value subtract(MathComponent a, MathComponent b) {
        var optimizedValue = OptimizationUtils.subtract(a, b);
        if (optimizedValue != null) return optimizedValue.calculate();

        var aValue = a.calculate();
        var bValue = b.calculate();

        if (aValue instanceof RatioValue || bValue instanceof RatioValue)
            return RatioUtils.subtract(
                    RatioUtils.valueAsRatio(aValue),
                    RatioUtils.valueAsRatio(bValue)
            );

        if (aValue instanceof BigIntValue && bValue instanceof BigIntValue)
            return toValue(aValue.asBigInteger().subtract(bValue.asBigInteger()));

        return toValue(aValue.asBigDecimal().subtract(bValue.asBigDecimal()));
    }

    public static Value divide(MathComponent a, MathComponent b) {
        var optimizedValue = OptimizationUtils.divide(a, b);
        if (optimizedValue != null) return optimizedValue.calculate();

        var aValue = a.calculate();
        var bValue = b.calculate();

        if (aValue instanceof RatioValue || bValue instanceof RatioValue)
            return RatioUtils.divide(
                    RatioUtils.valueAsRatio(aValue),
                    RatioUtils.valueAsRatio(bValue)
            );

        if (aValue instanceof BigIntValue && bValue instanceof BigIntValue) {
            var repeating = isRepeatingRemainder(aValue.asBigInteger(), bValue.asBigInteger());
            if (!repeating) {
                var remainderRemains = aValue.asBigInteger().remainder(bValue.asBigInteger()).compareTo(BigInteger.ZERO) != 0;

                if (remainderRemains)
                    return toValue(aValue.asBigDecimal().divide(bValue.asBigDecimal(), MathContext.DECIMAL128));
                return toValue(aValue.asBigInteger().divide(bValue.asBigInteger()));
            }
        } else {
            var repeating = isRepeatingRemainder(aValue.asBigDecimal(), bValue.asBigDecimal());
            if (!repeating)
                return toValue(aValue.asBigDecimal().divide(bValue.asBigDecimal(), MathContext.DECIMAL128));
        }

        return new RatioValue(aValue, bValue);
    }

    public static Value multiply(MathComponent a, MathComponent b) {
        var optimizedValue = OptimizationUtils.multiply(a, b);
        if (optimizedValue != null) return optimizedValue.calculate();

        var aValue = a.calculate();
        var bValue = b.calculate();

        if (aValue instanceof RatioValue || bValue instanceof RatioValue)
            return RatioUtils.multiply(
                    RatioUtils.valueAsRatio(aValue),
                    RatioUtils.valueAsRatio(bValue)
            );

        if (aValue instanceof BigIntValue && bValue instanceof BigIntValue)
            return toValue(aValue.asBigInteger().multiply(bValue.asBigInteger()));

        return toValue(aValue.asBigDecimal().multiply(bValue.asBigDecimal()));
    }

    public static Value power(MathComponent a, MathComponent b) {
        var optimizedValue = OptimizationUtils.power(a, b);
        if (optimizedValue != null) return optimizedValue.calculate();

        var aValue = a.calculate();
        var bValue = b.calculate();

        if (aValue instanceof BigIntValue && bValue instanceof BigIntValue)
            return toValue(aValue.asBigInteger().pow(bValue.asBigInteger().intValue()));

        return toValue(BigDecimalMath.pow(aValue.asBigDecimal(), bValue.asBigDecimal(), MathContext.DECIMAL128));
    }

    public static boolean isRepeatingRemainder(BigDecimal a, BigDecimal b) {
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

    public static boolean isRepeatingRemainder(BigInteger a, BigInteger b) {
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
