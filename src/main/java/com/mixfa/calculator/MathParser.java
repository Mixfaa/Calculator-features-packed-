package com.mixfa.calculator;

import com.mixfa.calculator.exception.MathParsingException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static com.mixfa.calculator.Utils.getArgs;

public class MathParser {
    private static final Supplier<Pattern> REAL_NUMBER_PATTERN = StableValue.supplier(() -> Pattern.compile("^[-+]?([0-9]*\\.)?[0-9]+([eE][-+]?[0-9]+)?$"));
    private static final Supplier<Pattern> INT_NUMBER_PATTERN = StableValue.supplier(() -> Pattern.compile("^-?\\d+$"));
    private final Pattern realNumberPattern = REAL_NUMBER_PATTERN.get();
    private final Pattern intNumberPattern = INT_NUMBER_PATTERN.get();
    private final FunctionComponent[] functionComponents;
    private final MathConstant[] constants;

    public MathParser(FunctionComponent[] functionComponents, MathConstant[] constants) {
        this.functionComponents = Arrays.stream(functionComponents)
                .sorted(Comparator.comparing(FunctionComponent::prefix))
                .toArray(FunctionComponent[]::new);

        this.constants = Arrays.stream(constants)
                .sorted(Comparator.comparing(MathConstant::name))
                .toArray(MathConstant[]::new);
    }

    public MathComponent parseSimpleComponent(String comp) throws MathParsingException {
        if (comp.equals("0") || comp.equals("0.0")) {
            return ValueFactory.zero();
        }

        if (intNumberPattern.matcher(comp).find()) {
            return ValueFactory.toValue(new BigInteger(comp));
        }

        if (realNumberPattern.matcher(comp).find()) {
            return ValueFactory.toValue(new BigDecimal(comp));
        }

        var constant = Utils.findMathConstant(comp, constants);
        if (constant != null) {
            return constant.value();
        }

        for (FunctionComponent functionComponent : functionComponents) {
            if (!Utils.startsWithFunctionName(comp, functionComponent))
                continue;

            MathComponent.Value[] args;
            try {
                args = Arrays.stream(getArgs(comp).split(","))
                        .map(String::trim)
                        .filter(arg -> !arg.isBlank())
                        .map(it -> {
                            try {
                                return this.parseInput(it);
                            } catch (MathParsingException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(mc -> !mc.isEmpty())
                        .map(MathComponent::calculate)
                        .toArray(MathComponent.Value[]::new);
            } catch (RuntimeException re) {
                throw new MathParsingException(re.getMessage());
            }

            if (functionComponent.argsCount() != args.length && functionComponent.argsCount() == -1)
                throw new MathParsingException("Args count mismatch " + functionComponent + " comp: " + comp);

            return switch (functionComponent) {
                case FunctionComponent.FunctionComponent0 fc0 -> fc0.function().get();
                case FunctionComponent.FunctionComponent1 fc1 -> fc1.function().apply(args[0]);
                case FunctionComponent.FunctionComponent2 fc2 -> fc2.function().apply(args[0], args[1]);
                case FunctionComponent.FunctionComponent3 fc3 ->
                        fc3.function().apply(args[0], args[1], args[2]);
                case FunctionComponent.FunctionComponentMulti fcMulti -> fcMulti.function().apply(args);
            };

        }

        throw new MathParsingException("Not parsed component " + comp);
    }

    public MathComponent parseInput(String input) throws MathParsingException {
        if (input.isBlank()) return ValueFactory.zero();

        return ShuntingYardConverter.convertToMathComponent(Tokenizer.tokenize(input), this);
    }

    private static final Supplier<MathParser> DEFAULT_PARSER = StableValue.supplier(() -> new MathParser(
            MathParserBuilder.DEFAULT_FUNCTIONS.toArray(FunctionComponent[]::new),
            MathParserBuilder.DEFAULT_CONSTANTS.toArray(MathConstant[]::new)
    ));

    public static MathParser defaultParser() {
        return DEFAULT_PARSER.get();
    }
}
