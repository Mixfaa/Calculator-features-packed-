package com.mixfa.calculator;

import org.apache.commons.lang3.function.TriFunction;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface FunctionComponent {
    String prefix();

    int argsCount();

    record FunctionComponent0(
            String prefix,
            Supplier<MathComponent.Value> function
    ) implements FunctionComponent {
        @Override
        public int argsCount() {
            return 0;
        }
    }

    record FunctionComponent1(
            String prefix,
            Function<MathComponent.Value, MathComponent.Value> function
    ) implements FunctionComponent {
        @Override
        public int argsCount() {
            return 1;
        }
    }

    record FunctionComponent2(
            String prefix,
            BiFunction<MathComponent.Value, MathComponent.Value, MathComponent.Value> function
    ) implements FunctionComponent {
        @Override
        public int argsCount() {
            return 2;
        }
    }

    record FunctionComponent3(
            String prefix,
            TriFunction<MathComponent.Value, MathComponent.Value, MathComponent.Value, MathComponent.Value> function
    ) implements FunctionComponent {
        @Override
        public int argsCount() {
            return 3;
        }
    }

    record FunctionComponentMulti(
            String prefix,
            Function<MathComponent.Value[], MathComponent.Value> function
    ) implements FunctionComponent {
        @Override
        public int argsCount() {
            return -1;
        }
    }
}
