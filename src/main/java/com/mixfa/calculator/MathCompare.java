package com.mixfa.calculator;

import java.math.BigDecimal;
import java.math.BigInteger;

public class MathCompare {
    private MathCompare() {
    }

    public static int compareTo(MathComponent.Value.RatioValue value, MathComponent.Value other) {
        if (value.signum() != other.signum())
            return Integer.compare(value.signum(), other.signum());

        return switch (other) {
            case MathComponent.Value.BigIntValue(BigInteger v) -> value.asBigDecimal().compareTo(new BigDecimal(v));
            case MathComponent.Value.BigDecimalValue(BigDecimal v) -> value.asBigDecimal().compareTo(v);
            case MathComponent.Value.RatioValue ratioValue -> value.asBigDecimal().compareTo(ratioValue.asBigDecimal());
        };
    }

    public static int compareTo(BigDecimal value, MathComponent.Value other) {
        if (value.signum() != other.signum())
            return Integer.compare(value.signum(), other.signum());

        return switch (other) {
            case MathComponent.Value.BigIntValue(BigInteger v) -> value.compareTo(new BigDecimal(v));
            case MathComponent.Value.BigDecimalValue(BigDecimal v) -> value.compareTo(v);
            case MathComponent.Value.RatioValue ratioValue -> value.compareTo(ratioValue.asBigDecimal());
        };
    }

    public static int compareTo(BigInteger value, MathComponent.Value other) {
        if (value.signum() != other.signum())
            return Integer.compare(value.signum(), other.signum());

        return switch (other) {
            case MathComponent.Value.BigIntValue(BigInteger v) -> value.compareTo(v);
            case MathComponent.Value.BigDecimalValue(BigDecimal v) -> new BigDecimal(value).compareTo(v);
            case MathComponent.Value.RatioValue ratioValue -> new BigDecimal(value).compareTo(ratioValue.asBigDecimal());
        };
    }
}
