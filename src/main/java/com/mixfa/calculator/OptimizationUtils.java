package com.mixfa.calculator;

import com.mixfa.calculator.MathComponent.Value;

public class OptimizationUtils {
    private OptimizationUtils() {
    }

    private static boolean isAny(MathComponent a, MathComponent b, OptimizationConstant constant) {
        if (a.chainLength() < b.chainLength())
            return a.calculate().equalsConstant(constant) || b.calculate().equalsConstant(constant);
        else
            return b.calculate().equalsConstant(constant) || a.calculate().equalsConstant(constant);
    }

    private static int find(MathComponent a, MathComponent b, OptimizationConstant constant) {
        if (a.chainLength() < b.chainLength()) {
            if (a.calculate().equalsConstant(constant))
                return -1;
        } else {
            if (b.calculate().equalsConstant(constant))
                return 1;
        }

        if (a.calculate().equalsConstant(constant))
            return -1;
        if (b.calculate().equalsConstant(constant))
            return 1;


        return 0;
    }

    private static boolean isAnyZero(MathComponent a, MathComponent b) {
        return isAny(a, b, OptimizationConstant.ZERO);
    }

    private static int findZero(MathComponent a, MathComponent b) {
        return find(a, b, OptimizationConstant.ZERO);
    }

    public static Value add(MathComponent a, MathComponent b) {
        var zeroPos = findZero(a, b);

        var aValue = a.calculate();
        var bValue = b.calculate();

        if (zeroPos == -1)
            return aValue;
        if (zeroPos == 1)
            return bValue;

        if (aValue.compareTo(bValue.negate()) == 0)
            return Value.BigIntValue.zero();

        return null;
    }

    public static Value subtract(MathComponent a, MathComponent b) {
        var zeroPos = findZero(a, b);

        var aValue = a.calculate();
        var bValue = b.calculate();

        if (zeroPos == -1)
            return bValue.negate();
        if (zeroPos == 1)
            return aValue;

        if (aValue.compareTo(bValue) == 0)
            return Value.BigIntValue.zero();

        return null;
    }

    public static Value divide(MathComponent a, MathComponent b) {
        var zeroPos = findZero(a, b);

        var aValue = a.calculate();
        var bValue = b.calculate();

        if (zeroPos == -1)
            return Value.BigIntValue.zero();
        if (zeroPos == 1)
            throw new ArithmeticException("Division by zero");

        if (bValue.equalsConstant(OptimizationConstant.ONE))
            return aValue;
        if (bValue.equalsConstant(OptimizationConstant.MINUS_ONE))
            return aValue.negate();

        if (aValue.compareTo(bValue) == 0)
            return Value.BigIntValue.one();

        return null;
    }

    public static Value multiply(MathComponent a, MathComponent b) {
        if (isAnyZero(a, b)) return ValueFactory.zero();

        var onePos = find(a, b, OptimizationConstant.ONE);
        var aValue = a.calculate();
        var bValue = b.calculate();

        if (onePos == -1)
            return bValue;
        if (onePos == 1)
            return aValue;

        var minusOnePos = find(a, b, OptimizationConstant.MINUS_ONE);

        if (minusOnePos == -1)
            return bValue.negate();
        if (minusOnePos == 1)
            return aValue.negate();

        return null;
    }

    public static Value power(MathComponent a, MathComponent b) {
        var zeroPos = findZero(a, b);

        if (zeroPos == -1) return ValueFactory.zero();
        if (zeroPos == 1) return ValueFactory.one();

        var aValue = a.calculate();
        var bValue = b.calculate();

        if (bValue.equalsConstant(OptimizationConstant.ONE))
            return aValue;

        return null;
    }

}
