package com.mixfa.calculator;


import com.mixfa.calculator.exception.MathParsingException;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.function.BiFunction;

class ShuntingYardConverter {
    private static boolean isOperator(String token) {
        return "+-/*^".contains(token);
    }

    private static boolean isOperand(String token) {
        return !isOperator(token);
    }

    private static int precedence(String op) {
        return switch (op) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            case "^" -> 3;
            default -> 0; // Для дужок та інших
        };
    }

    private static boolean isLeftAssociative(String op) {
        // Усі оператори, крім степеня (^), зазвичай лівоасоціативні.
        return !op.equals("^");
    }

    private static MathComponent _parse(String token, MathParser mathParser) throws MathParsingException {
        if (token.startsWith("(") && token.endsWith(")"))
            return mathParser.parseInput(token.substring(1, token.length() - 1));

        return mathParser.parseSimpleComponent(token);
    }

    public static MathComponent convertToMathComponent(List<String> tokens, MathParser parser) throws MathParsingException {
        if (tokens.size() == 1)
            return _parse(tokens.getFirst(), parser);

        Queue<String> outputQueue = new ArrayDeque<>();
        Stack<String> operatorStack = new Stack<>();

        for (String token : tokens) {
            if (isOperator(token)) {
                // Токен є оператором
                while (!operatorStack.isEmpty() && isOperator(operatorStack.peek())) {
                    String topOp = operatorStack.peek();

                    if (precedence(topOp) > precedence(token) ||
                            (precedence(topOp) == precedence(token) && isLeftAssociative(topOp))) {

                        outputQueue.add(operatorStack.pop());
                    } else {
                        break;
                    }
                }
                operatorStack.push(token);
            } else if (token.equals("(")) {
                // Ліва дужка завжди поміщається у стек
                operatorStack.push(token);
            } else if (token.equals(")")) {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    outputQueue.add(operatorStack.pop());
                }

                if (operatorStack.isEmpty() || !operatorStack.peek().equals("(")) {
                    throw new IllegalArgumentException("Незбалансовані дужки: пропущена ліва дужка.");
                }

                operatorStack.pop(); // Викидаємо ліву дужку зі стека
            } else {
                // Токен є операндом (числом або змінною)
                outputQueue.add(token);
            }
        }

        while (!operatorStack.isEmpty()) {
            String op = operatorStack.pop();
            if (op.equals("(") || op.equals(")")) {
                throw new IllegalArgumentException("Незбалансовані дужки: пропущена права дужка.");
            }
            outputQueue.add(op);
        }
        var stack = new Stack<MathComponent>();
        for (String token : outputQueue) {

            if (isOperand(token)) {
                stack.push(_parse(token, parser));
            } else {
                var compA = stack.pop();
                var compB = stack.pop();

                stack.push(selectFunction(token).apply(compB, compA));
            }
        }
        if (stack.size() != 1)
            throw new RuntimeException("Unbalanced input");

        return stack.pop();
    }

    private static BiFunction<MathComponent, MathComponent, MathComponent> selectFunction(String operator) throws MathParsingException {
        return switch (operator) {
            case "+" -> MathComponent.AnyOperation::add;
            case "-" -> MathComponent.AnyOperation::subtract;
            case "*" -> MathComponent.AnyOperation::multiply;
            case "/" -> MathComponent.AnyOperation::divide;
            case "^" -> MathComponent.AnyOperation::power;
            default -> throw new MathParsingException("Unsupported operator: " + operator);
        };
    }
}