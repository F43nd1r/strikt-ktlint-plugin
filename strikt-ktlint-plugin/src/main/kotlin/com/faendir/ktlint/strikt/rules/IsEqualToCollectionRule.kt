package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces:
 * - `.isEqualTo(listOf(a, b, c))` with `.containsExactly(a, b, c)`
 * - `.isEqualTo(setOf(a, b, c))` with `.containsExactlyInAnyOrder(a, b, c)`
 *
 * Only applies when the collection factory call has at least one argument.
 * Empty collections are handled by [IsEqualToEmptyCollectionRule].
 */
class IsEqualToCollectionRule : StriktRule("is-equal-to-collection") {

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

        if (argExpr.elementType != ElementType.CALL_EXPRESSION) return

        val argCallName = argExpr.callExpressionName() ?: return

        val replacement = COLLECTION_TO_ASSERTION[argCallName] ?: return

        // Get the inner arguments - must have at least one
        val innerArgList = argExpr.valueArgumentList() ?: return
        val innerArgs = innerArgList.getChildren(null).filter { it.elementType == ElementType.VALUE_ARGUMENT }
        if (innerArgs.isEmpty()) return // empty collections are handled by IsEqualToEmptyCollectionRule

        // Replace: isEqualTo(listOf(a, b, c)) -> containsExactly(a, b, c)
        emit(
            callExpression.startOffset,
            "Use $replacement() instead of isEqualTo($argCallName(...))",
            true,
        ).ifAutocorrectAllowed {
            // Replace the function name
            val refExpr = callExpression.findChildByType(ElementType.REFERENCE_EXPRESSION)!!
            (refExpr.firstChildNode as LeafPsiElement).rawReplaceWithText(replacement)

            // Replace the outer VALUE_ARGUMENT_LIST (containing the single listOf/setOf arg)
            // with the inner VALUE_ARGUMENT_LIST (the actual elements)
            callExpression.replaceChild(argList, innerArgList.clone() as ASTNode)
            ensureImport(node, "strikt.assertions.$replacement")
        }
    }

    companion object {
        private val COLLECTION_TO_ASSERTION = mapOf(
            "listOf" to "containsExactly",
            "setOf" to "containsExactlyInAnyOrder",
        )
    }
}
