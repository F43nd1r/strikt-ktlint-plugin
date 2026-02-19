package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces `.isEqualTo(true)` with `.isTrue()` and `.isEqualTo(false)` with `.isFalse()`.
 */
class IsEqualToBooleanRule : StriktRule("is-equal-to-boolean") {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision,
    ) {
        if (node.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        val callExpression = node.findLastCallExpression() ?: return
        if (callExpression.callExpressionName() != "isEqualTo") return

        val argList = callExpression.valueArgumentList() ?: return
        val singleArg = argList.singleValueArgument() ?: return
        val argExpr = singleArg.argumentExpression() ?: return

        if (argExpr.elementType != ElementType.BOOLEAN_CONSTANT) return

        val isTrue = argExpr.text == "true"
        val replacement = if (isTrue) "isTrue" else "isFalse"

        emit(
            callExpression.startOffset,
            "Use $replacement() instead of isEqualTo(${argExpr.text})",
            true,
        ).ifAutocorrectAllowed {
            val refExpr = callExpression.findChildByType(ElementType.REFERENCE_EXPRESSION)!!
            (refExpr.firstChildNode as LeafPsiElement).rawReplaceWithText(replacement)
            argList.removeChild(singleArg)
            ensureImport(node, "strikt.assertions.$replacement")
        }
    }
}
