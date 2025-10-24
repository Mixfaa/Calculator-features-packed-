package com.mixfa.calculator;

import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Parser2 {
    private static final Pattern FUNC_PATTERN = Pattern.compile("([a-z]\\w*)");

    static String findClosingBracket(Supplier<String> nextToken, Supplier<Boolean> hasNextToken) {
        int bracketCount = 1;
        StringBuilder tokenBuilder = new StringBuilder();
        while (hasNextToken.get()) {
            var token = nextToken.get();
            tokenBuilder.append(token);

            if (token.equals("("))
                ++bracketCount;
            else if (token.equals(")"))
                --bracketCount;

            if (bracketCount == 0)
                return tokenBuilder.toString();
        }

        throw new RuntimeException("Closing bracket not found");
    }

    public static List<String> tokenize(String str) {
        var allOperators = "+-/*^()";

        var tokens = new ArrayList<String>();

        var tokenizer = new StringTokenizer(str, allOperators, true);
        Stack<String> cachedTokens = new Stack<>();
        Supplier<String> nextTokenFunc = () -> cachedTokens.empty() ? tokenizer.nextToken() : cachedTokens.pop();
        Supplier<Boolean> hasMoreTokensFunc = () -> !cachedTokens.empty() || tokenizer.hasMoreTokens();

        while (hasMoreTokensFunc.get()) {
            String token = nextTokenFunc.get();
            if (token.isBlank()) continue;

            if (hasMoreTokensFunc.get()) {
                var nextToken = nextTokenFunc.get();

                if (nextToken.equals("(")) {
                    if (FUNC_PATTERN.matcher(token).matches()) {
                        token += "(" + findClosingBracket(nextTokenFunc, hasMoreTokensFunc);
                    } else
                        cachedTokens.push(nextToken);
                } else
                    cachedTokens.push(nextToken);
            }

            if (token.equals("(")) {
                token += findClosingBracket(nextTokenFunc, hasMoreTokensFunc);
            }
            tokens.add(token);
        }

        return tokens;
    }
}
