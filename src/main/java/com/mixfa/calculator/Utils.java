package com.mixfa.calculator;

import com.mixfa.calculator.exception.MathParsingException;

public class Utils {
    private Utils() {
    }

    public static boolean startsWithFunctionName(String comp, FunctionComponent functionComponent) {
        var prefix = functionComponent.prefix();
        var prefixLength = prefix.length();

        if (comp.length() < prefixLength) return false;
        if (comp.charAt(prefixLength) != '(') return false;

        return comp.startsWith(prefix);
    }

    public static int findClosingBracketPos(String str, int startPos) throws MathParsingException {
        var openingBrackets = 0;
        var bytes = str.getBytes();
        for (int i = startPos; i < bytes.length; i++) {
            var c = bytes[i];

            if (c == '(')
                ++openingBrackets;
            else if (c == ')') {
                --openingBrackets;
                if (openingBrackets == 0)
                    return i;
            }
        }

        throw new MathParsingException("Cannot find closing bracket");
    }

    public static String getArgs(String comp) throws MathParsingException {
        var index1 = comp.indexOf('(');
        if (index1 == -1)
            throw new MathParsingException("Cannot find argument in component " + comp);
        var index2 = findClosingBracketPos(comp, index1);

        return comp.substring(index1 + 1, index2);
    }
}
