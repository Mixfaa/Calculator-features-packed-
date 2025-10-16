package com.mixfa.calculator;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface FunctionComponent {
    String prefix();

    int argsCount();

    record FunctionComponent0(
            String prefix,
            Supplier<MathComponent> function
    ) implements FunctionComponent {
        @Override
        public int argsCount() {
            return 0;
        }
    }

    record FunctionComponent1(
            String prefix,
            Function<MathComponent, MathComponent> function
    ) implements FunctionComponent {
        @Override
        public int argsCount() {
            return 1;
        }
    }

    record FunctionComponent2(
            String prefix,
            BiFunction<MathComponent, MathComponent, MathComponent> function
    ) implements FunctionComponent {
        @Override
        public int argsCount() {
            return 2;
        }
    }
}
