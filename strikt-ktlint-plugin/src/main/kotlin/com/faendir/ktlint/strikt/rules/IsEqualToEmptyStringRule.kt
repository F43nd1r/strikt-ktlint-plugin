package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces `.isEqualTo("")` with `.isEmpty()`.
 */
class IsEqualToEmptyStringRule : StriktRule("is-equal-to-empty-string") {

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

        if (argExpr.elementType != ElementType.STRING_TEMPLATE) return
        // An empty string "" has text `""` (just two quotes, no content)
        if (argExpr.text != "\"\"") return

        emit(
            callExpression.startOffset,
            "Use isEmpty() instead of isEqualTo(\"\")",
            true,
        ).ifAutocorrectAllowed {
            val refExpr = callExpression.findChildByType(ElementType.REFERENCE_EXPRESSION)!!
            (refExpr.firstChildNode as LeafPsiElement).rawReplaceWithText("isEmpty")
            argList.removeChild(singleArg)
            ensureImport(node, "strikt.assertions.isEmpty")
        }
    }
}
