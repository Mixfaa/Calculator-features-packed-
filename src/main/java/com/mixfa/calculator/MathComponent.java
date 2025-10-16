package com.mixfa.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleFunction;

public sealed interface MathComponent {
    BigDecimal calculate();
    default boolean isEmpty() {
        return false;
    }

    final class Empty implements MathComponent {
        private static Empty INSTANCE = new Empty();

        public static Empty instance() {
            return INSTANCE;
        }

        private Empty() {
        }

        @Override
        public BigDecimal calculate() {
            return BigDecimal.ZERO;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    record Unparsed(String comp) implements MathComponent {
        @Override
        public BigDecimal calculate() {
            throw new UnsupportedOperationException("Not supported. Comp: " + comp);
        }
    }

    record Value(BigDecimal value) implements MathComponent {
        @Override
        public BigDecimal calculate() {
            return value;
        }
    }

    record Add(MathComponent compA,
               MathComponent compB
    ) implements MathComponent {
        @Override
        public BigDecimal calculate() {
            return compA.calculate().add(compB.calculate());
        }
    }

    record Subtract(MathComponent compA,
                    MathComponent compB
    ) implements MathComponent {
        @Override
        public BigDecimal calculate() {
            return compA.calculate().subtract(compB.calculate());
        }
    }

    record Multiply(MathComponent compA,
                    MathComponent compB
    ) implements MathComponent {
        @Override
        public BigDecimal calculate() {
            return compA.calculate().multiply(compB.calculate());
        }
    }

    record Divide(MathComponent compA,
                  MathComponent compB
    ) implements MathComponent {
        @Override
        public BigDecimal calculate() {
            return compA.calculate().divide(compB.calculate(), 15, RoundingMode.HALF_EVEN);
        }
    }

    record Power(MathComponent compA,
                 MathComponent compB) implements MathComponent {
        @Override
        public BigDecimal calculate() {
            return new BigDecimal(Math.pow(compA.calculate().doubleValue(), compB.calculate().doubleValue()));
        }
    }

    record MathFunc1(MathComponent comp, DoubleFunction<Double> function) implements MathComponent {
        @Override
        public BigDecimal calculate() {
            return BigDecimal.valueOf(function.apply(comp.calculate().doubleValue()));
        }
    }

    record MathFunc2(MathComponent comp1, MathComponent comp2,
                     DoubleBinaryOperator function) implements MathComponent {
        @Override
        public BigDecimal calculate() {
            return BigDecimal.valueOf(function.applyAsDouble(comp1.calculate().doubleValue(), comp2.calculate().doubleValue()));
        }
    }
}
