package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces `.$functionName(0)` with `.isEmpty()`.
 *
 * @param functionName the function to match (`hasSize` or `hasLength`)
 */
open class HasZeroRule(
    ruleName: String,
    private val functionName: String,
) : StriktRule(ruleName) {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision,
    ) {
        if (node.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        val callExpression = node.findLastCallExpression() ?: return
        if (callExpression.callExpressionName() != functionName) return

        val argList = callExpression.valueArgumentList() ?: return
        val singleArg = argList.singleValueArgument() ?: return
        val argExpr = singleArg.argumentExpression() ?: return

        if (argExpr.elementType != ElementType.INTEGER_CONSTANT || argExpr.text != "0") return

        emit(
            callExpression.startOffset,
            "Use isEmpty() instead of $functionName(0)",
            true,
        ).ifAutocorrectAllowed {
            val refExpr = callExpression.findChildByType(ElementType.REFERENCE_EXPRESSION)!!
            (refExpr.firstChildNode as LeafPsiElement).rawReplaceWithText("isEmpty")
            argList.removeChild(singleArg)
            ensureImport(node, "strikt.assertions.isEmpty")
        }
    }
}
