package com.mixfa.calculator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

public sealed interface MathComponent {
    MathComponent.Value calculate();

    default boolean isEmpty() {
        return false;
    }

    record Unparsed(String comp) implements MathComponent {
        @Override
        public Value calculate() {
            throw new UnsupportedOperationException("Not supported. Comp: " + comp);
        }
    }


    static public sealed interface Value extends MathComponent {
        BigInteger asBigInteger();

        BigDecimal asBigDecimal();

        Value negate();

        boolean equalsConstant(OptimizationConstant constant);

        default boolean isZero() {
            return equalsConstant(OptimizationConstant.ZERO);
        }

        record BigIntValue(BigInteger value) implements Value {
            private static final BigIntValue ONE = new BigIntValue(BigInteger.ONE);
            private static final BigIntValue ZERO = new BigIntValue(BigInteger.ZERO);

            public static BigIntValue one() {
                return ONE;
            }

            public static BigIntValue zero() {
                return ZERO;
            }

            @Override
            public Value calculate() {
                return this;
            }

            @Override
            public BigInteger asBigInteger() {
                return value;
            }

            @Override
            public BigDecimal asBigDecimal() {
                return new BigDecimal(value);
            }

            @Override
            public Value negate() {
                return new BigIntValue(value.negate());
            }

            @Override
            public boolean equalsConstant(OptimizationConstant constant) {
                return value.equals(constant.intValue());
            }

        }

        record BigDecimalValue(BigDecimal value) implements Value {

            @Override
            public Value calculate() {
                return this;
            }

            @Override
            public BigInteger asBigInteger() {
                return value.toBigInteger();
            }

            @Override
            public BigDecimal asBigDecimal() {
                return value;
            }

            @Override
            public Value negate() {
                return new BigDecimalValue(value.negate());
            }

            @Override
            public boolean equalsConstant(OptimizationConstant constant) {
                return value.equals(constant.decimalValue());
            }
        }

        record RatioValue(Value value, Value divisor) implements Value {

            @Override
            public BigInteger asBigInteger() {
                return asBigDecimal().toBigInteger();
            }

            public BigDecimal asBigDecimal() {
                return value.asBigDecimal().divide(divisor.asBigDecimal());
            }

            @Override
            public Value negate() {
                return new RatioValue(value, divisor.negate());
            }

            @Override
            public boolean equalsConstant(OptimizationConstant constant) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Value calculate() {
                return this;
            }
        }
    }

    record Add(MathComponent compA,
               MathComponent compB
    ) implements MathComponent {
        @Override
        public Value calculate() {
            return MathUtils.add(compA.calculate(), compB.calculate());
        }
    }

    record Subtract(MathComponent compA,
                    MathComponent compB
    ) implements MathComponent {
        @Override
        public Value calculate() {
            return MathUtils.subtract(compA.calculate(), compB.calculate());
        }
    }

    record Multiply(MathComponent compA,
                    MathComponent compB
    ) implements MathComponent {
        @Override
        public Value calculate() {
            return MathUtils.multiply(compA.calculate(), compB.calculate());
        }
    }

    record Divide(MathComponent compA,
                  MathComponent compB
    ) implements MathComponent {
        @Override
        public Value calculate() {
            return MathUtils.divide(compA.calculate(), compB.calculate());
        }
    }

    record Power(MathComponent compA,
                 MathComponent compB) implements MathComponent {
        @Override
        public Value calculate() {
            return MathUtils.power(compA.calculate(), compB.calculate());
        }
    }
}
