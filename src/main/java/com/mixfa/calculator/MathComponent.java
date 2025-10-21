package com.mixfa.calculator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public sealed interface MathComponent {
    Value calculate();

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

        int signum();

        Value abs();

        int compareTo(Value other);

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
            public int signum() {
                return value.signum();
            }

            @Override
            public Value abs() {
                return new BigIntValue(value.abs());
            }

            @Override
            public int compareTo(Value other) {
                return MathCompare.compareTo(value, other);
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
                return value.compareTo(constant.intValue()) == 0;
            }
        }

        record BigDecimalValue(BigDecimal value) implements Value {
            @Override
            public int signum() {
                return value.signum();
            }

            @Override
            public Value abs() {
                return new BigDecimalValue(value.abs());
            }

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
                return value.compareTo(constant.decimalValue()) == 0;
            }

            @Override
            public int compareTo(Value other) {
                return MathCompare.compareTo(value, other);
            }
        }

        // prefers negative numerator
        record RatioValue(Value numerator, Value denominator) implements Value {
            public RatioValue {
                if ((numerator.signum() == -1 && denominator.signum() == -1) ||
                        numerator.signum() == 1 && denominator.signum() == -1) {
                    numerator = numerator.negate();
                    denominator = denominator.negate();
                }
            }

            @Override
            public int signum() {
                return numerator.signum() * denominator.signum();
            }

            @Override
            public Value abs() {
                return new RatioValue(numerator.signum() == -1 ? numerator.abs() : numerator,
                        denominator.signum() == -1 ? denominator.abs() : denominator);
            }

            @Override
            public int compareTo(Value other) {
                return MathCompare.compareTo(this, other);
            }

            @Override
            public BigInteger asBigInteger() {
                return asBigDecimal().toBigInteger();
            }

            public BigDecimal asBigDecimal() {
                return numerator.asBigDecimal().divide(denominator.asBigDecimal(), MathContext.DECIMAL128);
            }

            @Override
            public Value negate() {
                if (numerator.signum() == -1)
                    return new RatioValue(numerator.negate(), denominator.negate());
                else
                    return new RatioValue(numerator, denominator.negate());
            }

            @Override
            public boolean equalsConstant(OptimizationConstant constant) {
                return switch (constant) {
                    case ZERO -> numerator.isZero();
                    case ONE -> numerator.equals(denominator);
                    case MINUS_ONE -> numerator.abs().equals(denominator.abs()) && (
                            (numerator.signum() == -1 && denominator.signum() == 1) || (numerator.signum() == 1 && denominator.signum() == -1)
                    );
                };
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
