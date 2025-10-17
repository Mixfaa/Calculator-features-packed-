package com.mixfa.calculator;

import com.mixfa.calculator.exception.MathParsingException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.regex.Pattern;

import static com.mixfa.calculator.Utils.findClosingBracketPos;
import static com.mixfa.calculator.Utils.getArgs;

@RequiredArgsConstructor
public class MathParser {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[-+]?([0-9]*\\.)?[0-9]+([eE][-+]?[0-9]+)?$");
    private final FunctionComponent[] functionComponents;
    private final MathConstant[] constants;

    public static final MathParser defaultParser = new MathParser(
            MathParserBuilder.DEFAULT_FUNCTIONS.toArray(FunctionComponent[]::new),
            MathParserBuilder.DEFAULT_CONSTANTS.toArray(MathConstant[]::new)
    );

    public MathComponent parseNode(Tree.TreeNode node) throws MathParsingException {
        if (!(node.comp() instanceof MathComponent.Unparsed(String comp)))
            return node.comp();

        if (NUMBER_PATTERN.matcher(comp).find()) {
            node.comp(new MathComponent.Value(new BigDecimal(comp)));
            return node.comp();
        }

        for (MathConstant constant : constants)
            if (comp.equals(constant.name())) {
                node.comp(constant.value());
                return node.comp();
            }

        for (FunctionComponent functionComponent : functionComponents)
            if (Utils.startsWithFunctionName(comp, functionComponent)) {
                MathComponent[] args;
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
                            .toArray(MathComponent[]::new);
                } catch (RuntimeException re) {
                    throw new MathParsingException(re.getMessage());
                }

                if (functionComponent.argsCount() != args.length)
                    throw new MathParsingException("Args count mismatch " + functionComponent + " comp: " + comp);

                switch (functionComponent) {
                    case FunctionComponent.FunctionComponent0 fc0 -> node.comp(fc0.function().get());
                    case FunctionComponent.FunctionComponent1 fc1 -> node.comp(fc1.function().apply(args[0]));
                    case FunctionComponent.FunctionComponent2 fc2 -> node.comp(fc2.function().apply(args[0], args[1]));
                }

                return node.comp();
            }

        return node.comp();
    }

    public MathComponent parse(String input) throws MathParsingException {
        if (input.isBlank()) return MathComponent.Empty.instance();
        final var operatorSymbols = "+-/*^";

        var tree = new Tree(this);
        var str = new StringBuilder();
        var chars = input.toCharArray();
        String bracketsContent = null;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            if (c == '(') {
                var bracketEnd = findClosingBracketPos(input, i);
                bracketsContent = input.substring(i, bracketEnd + 1);
                i = bracketEnd;
                continue;
            }
            if (operatorSymbols.contains(String.valueOf(c))) {
                if (bracketsContent != null) {
                    tree.add(bracketsContent, c);
                    bracketsContent = null;
                    continue;
                }
                tree.add(str.toString(), c);
                str = new StringBuilder();
            } else {
                str.append(c);

                if (isFunctionName(str)) {
                    var endOfFunction = findClosingBracketPos(input, i + 1);
                    str.append(input, i + 1, endOfFunction + 1);
                    i = endOfFunction;
                }
            }
        }
        if (!str.isEmpty())
            tree.add(str.toString(), ' ');
        if (bracketsContent != null)
            tree.add(bracketsContent, ' ');
        return tree.parse();
    }

    private boolean isFunctionName(StringBuilder str) {
        for (FunctionComponent functionComponent : functionComponents)
            if (Utils.equals(functionComponent.prefix(), str)) return true;
        return false;
    }
}
