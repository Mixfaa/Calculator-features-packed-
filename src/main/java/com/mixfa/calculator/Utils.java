package com.mixfa.calculator;

import com.mixfa.calculator.exception.MathParsingException;

public class Utils {
    private Utils() {
    }

    public static MathConstant findMathConstant(String comp, MathConstant[] constants) {
        int l = 0;
        int r = constants.length - 1;
        while (l <= r) {
            int m = l + (r - l) / 2;
            if (constants[m].name().equals(comp))
                return constants[m];
            if (constants[m].name().compareTo(comp) < 0)
                l = m + 1;
            else
                r = m - 1;
        }
        return null;
    }

//    public static FunctionComponent findMathFunction(String comp, FunctionComponent[] functionComponents) {
//        int l = 0;
//        int r = functionComponents.length - 1;
//        while (l <= r) {
//            int m = l + (r - l) / 2;
//            if (startsWithFunctionName(comp, functionComponents[m]))
//                return functionComponents[m];
//            if (functionComponents[m].prefix().compareTo(comp) < 0)
//                l = m + 1;
//            else
//                r = m - 1;
//        }
//        return null;
//    }

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
