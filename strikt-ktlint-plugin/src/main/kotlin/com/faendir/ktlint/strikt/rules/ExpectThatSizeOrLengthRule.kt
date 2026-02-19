package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces:
 * - `expectThat(x.size).isEqualTo(n)` with `expectThat(x).hasSize(n)`
 * - `expectThat(x.length).isEqualTo(n)` with `expectThat(x).hasLength(n)`
 */
class ExpectThatSizeOrLengthRule : StriktRule("expect-that-size-or-length") {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision,
    ) {
        if (node.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        // Pattern: expectThat(x.size).isEqualTo(n)
        // AST:
        //   DOT_QUALIFIED_EXPRESSION
        //     CALL_EXPRESSION (expectThat(...))
        //     DOT
        //     CALL_EXPRESSION (isEqualTo(n))

        val isEqualToCall = node.findLastCallExpression() ?: return
        if (isEqualToCall.callExpressionName() != "isEqualTo") return

        val receiver = node.dotQualifiedReceiver() ?: return
        if (receiver.elementType != ElementType.CALL_EXPRESSION) return
        if (receiver.callExpressionName() != "expectThat") return

        // Get the single argument to expectThat
        val expectArgList = receiver.valueArgumentList() ?: return
        val expectSingleArg = expectArgList.singleValueArgument() ?: return
        val expectArgExpr = expectSingleArg.argumentExpression() ?: return

        // The argument must be a DOT_QUALIFIED_EXPRESSION ending in .size or .length
        if (expectArgExpr.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        val children = expectArgExpr.getChildren(null)
        val lastChild = children.lastOrNull { it.elementType == ElementType.REFERENCE_EXPRESSION } ?: return
        val propertyName = lastChild.text
        val replacement = PROPERTY_TO_ASSERTION[propertyName] ?: return

        // Get the base receiver (everything before .size/.length)
        val baseReceiver = expectArgExpr.dotQualifiedReceiver() ?: return

        emit(
            receiver.startOffset,
            "Use $replacement() instead of expectThat(x.$propertyName).isEqualTo()",
            true,
        ).ifAutocorrectAllowed {
            // Step 1: Replace the argument of expectThat from x.size to just x
            expectSingleArg.replaceChild(expectArgExpr, baseReceiver.clone() as ASTNode)

            // Step 2: Rename isEqualTo to hasSize/hasLength
            val isEqualToRef = isEqualToCall.findChildByType(ElementType.REFERENCE_EXPRESSION)!!
            (isEqualToRef.firstChildNode as LeafPsiElement).rawReplaceWithText(replacement)
            ensureImport(node, "strikt.assertions.$replacement")
        }
    }

    companion object {
        private val PROPERTY_TO_ASSERTION = mapOf(
            "size" to "hasSize",
            "length" to "hasLength",
        )
    }
}
