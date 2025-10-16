package com.mixfa.calculator;

import com.mixfa.calculator.exception.MathParsingException;

public interface TreeNodeTransformer {
    boolean transform(Tree.TreeNode node) throws MathParsingException;
}
