package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces:
 * - `.isEqualTo(emptyList())` with `.isEmpty()`
 * - `.isEqualTo(listOf())` with `.isEmpty()`
 * - `.isEqualTo(emptySet())` with `.isEmpty()`
 * - `.isEqualTo(setOf())` with `.isEmpty()`
 * - `.isEqualTo(emptyMap())` with `.isEmpty()`
 * - `.isEqualTo(mapOf())` with `.isEmpty()`
 */
class IsEqualToEmptyCollectionRule : StriktRule("is-equal-to-empty-collection") {

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
        if (argCallName !in EMPTY_COLLECTION_FUNCTIONS) return

        // Check that the inner call has no arguments (or only type arguments)
        val innerArgList = argExpr.valueArgumentList()
        if (innerArgList != null) {
            val innerArgs = innerArgList.getChildren(null).filter { it.elementType == ElementType.VALUE_ARGUMENT }
            if (innerArgs.isNotEmpty()) return
        }

        emit(
            callExpression.startOffset,
            "Use isEmpty() instead of isEqualTo($argCallName())",
            true,
        ).ifAutocorrectAllowed {
            val refExpr = callExpression.findChildByType(ElementType.REFERENCE_EXPRESSION)!!
            (refExpr.firstChildNode as LeafPsiElement).rawReplaceWithText("isEmpty")
            argList.removeChild(singleArg)
            ensureImport(node, "strikt.assertions.isEmpty")
        }
    }

    companion object {
        private val EMPTY_COLLECTION_FUNCTIONS = setOf(
            "emptyList", "listOf",
            "emptySet", "setOf",
            "emptyMap", "mapOf",
        )
    }
}
