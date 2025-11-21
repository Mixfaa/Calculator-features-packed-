package com.mixfa.calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Tokenizer {
    private static final Pattern FUNC_PATTERN = Pattern.compile("([a-z]\\w*)");
    private static final String ALL_OPERATORS = "+-/*^()";

    static String findClosingBracket(Stack<String> cachedTokens, StringTokenizer tokenizer) {
        int bracketCount = 1;
        StringBuilder tokenBuilder = new StringBuilder();
        while (hasMoreTokens(cachedTokens, tokenizer)) {
            var token = nextToken(cachedTokens, tokenizer);
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

    private static boolean hasMoreTokens(Stack<String> cachedTokens, StringTokenizer tokenizer) {
        return !cachedTokens.empty() || tokenizer.hasMoreTokens();
    }

    private static String nextToken(Stack<String> cachedTokens, StringTokenizer tokenizer) {
        return cachedTokens.empty() ? tokenizer.nextToken() : cachedTokens.pop();
    }

    public static List<String> tokenize(String str) {
        var tokens = new ArrayList<String>();
        var tokenizer = new StringTokenizer(str, ALL_OPERATORS, true);
        var cachedTokens = new Stack<String>();

        var firstToken = true;
        while (hasMoreTokens(cachedTokens, tokenizer)) {
            String token = nextToken(cachedTokens, tokenizer);
            if (token.isBlank()) continue;
            if (firstToken) {
                firstToken = false;
                if ("+-".contains(token)) {
                    if (hasMoreTokens(cachedTokens, tokenizer))
                        token += nextToken(cachedTokens, tokenizer);
                    else
                        throw new RuntimeException("First token is a sign, but next token not found");
                }
            }
            if (hasMoreTokens(cachedTokens, tokenizer)) {
                var nextToken = nextToken(cachedTokens, tokenizer);

                if (nextToken.equals("(")) {
                    if (FUNC_PATTERN.matcher(token).matches()) {
                        token += "(" + findClosingBracket(cachedTokens, tokenizer);
                    } else
                        cachedTokens.push(nextToken);
                } else
                    cachedTokens.push(nextToken);
            }
            if (token.equals("(")) {
                token += findClosingBracket(cachedTokens, tokenizer);
            }
            tokens.add(token);
        }

        return tokens;
    }

    public static void tokenize(String str, Consumer<String> tokenConsumer) {
        var tokenizer = new StringTokenizer(str, ALL_OPERATORS, true);
        var cachedTokens = new Stack<String>();

        var firstToken = true;
        while (hasMoreTokens(cachedTokens, tokenizer)) {
            String token = nextToken(cachedTokens, tokenizer);
            if (token.isBlank()) continue;
            if (firstToken) {
                firstToken = false;
                if ("+-".contains(token)) {
                    if (hasMoreTokens(cachedTokens, tokenizer))
                        token += nextToken(cachedTokens, tokenizer);
                    else
                        throw new RuntimeException("First token is a sign, but next token not found");
                }
            }
            if (hasMoreTokens(cachedTokens, tokenizer)) {
                var nextToken = nextToken(cachedTokens, tokenizer);

                if (nextToken.equals("(")) {
                    if (FUNC_PATTERN.matcher(token).matches()) {
                        token += "(" + findClosingBracket(cachedTokens, tokenizer);
                    } else
                        cachedTokens.push(nextToken);
                } else
                    cachedTokens.push(nextToken);
            }
            if (token.equals("(")) {
                token += findClosingBracket(cachedTokens, tokenizer);
            }
            tokenConsumer.accept(token);
        }
    }
}