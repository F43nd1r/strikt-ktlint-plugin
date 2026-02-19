package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces `.isEqualTo(null)` with `.isNull()`.
 */
class IsEqualToNullRule : StriktRule("is-equal-to-null") {

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

        if (argExpr.elementType != ElementType.NULL) return

        emit(callExpression.startOffset, "Use isNull() instead of isEqualTo(null)", true)
            .ifAutocorrectAllowed {
                val refExpr = callExpression.findChildByType(ElementType.REFERENCE_EXPRESSION)!!
                (refExpr.firstChildNode as LeafPsiElement).rawReplaceWithText("isNull")
                argList.removeChild(singleArg)
                ensureImport(node, "strikt.assertions.isNull")
            }
    }
}
