package com.mixfa.calculator;


import com.mixfa.calculator.exception.MathParsingException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
class Tree {
    private TreeNode head;
    private final MathParser mathParser;

    public void add(String strComp, char operator) throws MathParsingException {
        MathComponent component = null;
        if (strComp.startsWith("(") && strComp.endsWith(")"))
            component = mathParser.parse(strComp.substring(1, strComp.length() - 1));

        component = component == null ? new MathComponent.Unparsed(strComp) : component;
        if (head == null) {
            head = new TreeNode(component, operator, null);
            return;
        }

        var currentNode = head;
        while (currentNode.next != null)
            currentNode = currentNode.next;

        currentNode.next = new TreeNode(
                component,
                operator,
                null
        );
    }

    private void transform(TreeNodeTransformer transformer) throws MathParsingException {
        var currentNode = head;
        while (currentNode != null) {
            var patched = transformer.transform(currentNode);
            if (patched) {
                currentNode.operator = currentNode.next.operator;
                currentNode.next = currentNode.next.next;
                mathParser.parseNode(currentNode);
                continue;
            }

            mathParser.parseNode(currentNode);
            currentNode = currentNode.next;
        }

    }

    public MathComponent parse() throws MathParsingException {
        transform(currentNode -> {
            var patched = true;
            switch (currentNode.operator()) {
                case '^' ->
                        currentNode.comp = new MathComponent.Power(mathParser.parseNode(currentNode), mathParser.parseNode(currentNode.next));
                case '*' ->
                        currentNode.comp = new MathComponent.Multiply(mathParser.parseNode(currentNode), mathParser.parseNode(currentNode.next));
                case '/' ->
                        currentNode.comp = new MathComponent.Divide(mathParser.parseNode(currentNode), mathParser.parseNode(currentNode.next));
                default -> patched = false;
            }
            return patched;
        });

        transform(currentNode -> {
            var patched = true;
            switch (currentNode.operator()) {
                case '+' ->
                        currentNode.comp = new MathComponent.Add(mathParser.parseNode(currentNode), mathParser.parseNode(currentNode.next));
                case '-' ->
                        currentNode.comp = new MathComponent.Subtract(mathParser.parseNode(currentNode), mathParser.parseNode(currentNode.next));
                default -> patched = false;
            }
            return patched;
        });

        return head.comp;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class TreeNode {
        private MathComponent comp;
        private char operator;
        private TreeNode next;
    }
}