package com.mixfa.calculator;

import com.mixfa.calculator.exception.MathParsingException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Gatherers;

import static com.mixfa.calculator.Utils.getArgs;

@RequiredArgsConstructor
public class MathParser {
    private static final Supplier<Pattern> REAL_NUMBER_PATTERN = StableValue.supplier(() -> Pattern.compile("^[-+]?([0-9]*\\.)?[0-9]+([eE][-+]?[0-9]+)?$"));
    private static final Supplier<Pattern> INT_NUMBER_PATTERN = StableValue.supplier(() -> Pattern.compile("^-?\\d+$"));
    private final Pattern realNumberPattern = REAL_NUMBER_PATTERN.get();
    private final Pattern intNumberPattern = INT_NUMBER_PATTERN.get();
    private final FunctionComponent[] functionComponents;
    private final MathConstant[] constants;


    public MathComponent parseNode(Tree.TreeNode node) throws MathParsingException {
        if (!(node.comp() instanceof MathComponent.Unparsed(String comp)))
            return node.comp();

        if (comp.equals("0") || comp.equals("0.0")) {
            node.comp(ValueFactory.zero());
            return node.comp();
        }

        if (intNumberPattern.matcher(comp).find()) {
            node.comp(ValueFactory.toValue(new BigInteger(comp)));
            return node.comp();
        }

        if (realNumberPattern.matcher(comp).find()) {
            node.comp(ValueFactory.toValue(new BigDecimal(comp)));
            return node.comp();
        }

        for (MathConstant constant : constants) {
            if (!comp.equals(constant.name()))
                continue;
            node.comp(constant.value());
            return node.comp();
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
                                return this.parse(it);
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

            switch (functionComponent) {
                case FunctionComponent.FunctionComponent0 fc0 -> node.comp(fc0.function().get());
                case FunctionComponent.FunctionComponent1 fc1 -> node.comp(fc1.function().apply(args[0]));
                case FunctionComponent.FunctionComponent2 fc2 -> node.comp(fc2.function().apply(args[0], args[1]));
                case FunctionComponent.FunctionComponent3 fc3 ->
                        node.comp(fc3.function().apply(args[0], args[1], args[2]));
                case FunctionComponent.FunctionComponentMulti fcMulti -> node.comp(fcMulti.function().apply(args));
            }

            return node.comp();
        }

        throw new MathParsingException("Not parsed component " + comp);
    }


    public MathComponent parse(String input) throws MathParsingException {
        if (input.isBlank()) return ValueFactory.zero();

        var tree = new Tree(this);

        Parser2.tokenize(input).stream().gather(
                Gatherers.windowFixed(2)
        ).forEach(tokensList -> {
            var token = tokensList.getFirst();
            var token2 = tokensList.size() > 1 ? tokensList.get(1) : null;

            try {
                tree.add(token, token2 == null ? ' ' : token2.charAt(0));
            } catch (MathParsingException e) {
                throw new RuntimeException(e);
            }
        });
        return tree.parse();
    }

    private static final Supplier<MathParser> DEFAULT_PARSER = StableValue.supplier(() -> new MathParser(
            MathParserBuilder.DEFAULT_FUNCTIONS.toArray(FunctionComponent[]::new),
            MathParserBuilder.DEFAULT_CONSTANTS.toArray(MathConstant[]::new)
    ));

    public static MathParser defaultParser() {
        return DEFAULT_PARSER.get();
    }
}
