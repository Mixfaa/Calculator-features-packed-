package com.mixfa.calculator;

import lombok.Getter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public sealed interface MathComponent {
    Value calculate();

    default int chainLength() {
        return 1;
    }

    default boolean isEmpty() {
        return false;
    }

    record Unparsed(String comp) implements MathComponent {
        @Override
        public Value calculate() {
            throw new UnsupportedOperationException("Unparsed component: " + comp);
        }
    }

    static public sealed interface Operation extends MathComponent {
        MathComponent compA();

        MathComponent compB();

        @Override
        default int chainLength() {
            return compA().chainLength() + compB().chainLength();
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

            @Override
            public String toString() {
                return value.toString();
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

            @Override
            public String toString() {
                return value.toString();
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
            public int chainLength() {
                return 2;
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

            @Override
            public String toString() {
                return numerator.toString() + "/" + denominator.toString();
            }
        }
    }

    @Accessors(fluent = true)
    @Getter
    final class AnyOperation implements Operation {
        private final MathComponent compA;
        private final MathComponent compB;
        private final BiFunction<MathComponent, MathComponent, Value> operation;

        private final Supplier<Value> calculatedValue;

        public AnyOperation(MathComponent compA, MathComponent compB, BiFunction<MathComponent, MathComponent, Value> operation) {
            this.compA = compA;
            this.compB = compB;
            this.operation = operation;

            this.calculatedValue = StableValue.supplier(() -> operation.apply(compA, compB));
        }

        @Override
        public Value calculate() {
            return calculatedValue.get();
        }

        public static AnyOperation add(MathComponent compA, MathComponent compB) {
            return new AnyOperation(compA, compB, MathUtils::add);
        }

        public static AnyOperation subtract(MathComponent compA, MathComponent compB) {
            return new AnyOperation(compA, compB, MathUtils::subtract);
        }

        public static AnyOperation multiply(MathComponent compA, MathComponent compB) {
            return new AnyOperation(compA, compB, MathUtils::multiply);
        }

        public static AnyOperation divide(MathComponent compA, MathComponent compB) {
            return new AnyOperation(compA, compB, MathUtils::divide);
        }

        public static AnyOperation power(MathComponent compA, MathComponent compB) {
            return new AnyOperation(compA, compB, MathUtils::power);
        }
    }
}
