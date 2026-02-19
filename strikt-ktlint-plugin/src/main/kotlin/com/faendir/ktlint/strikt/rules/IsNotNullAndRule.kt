package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces `.isNotNull().and { block }` with `.withNotNull { block }`.
 */
class IsNotNullAndRule : StriktRule("is-not-null-and") {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision,
    ) {
        if (node.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        // Match: <receiver>.isNotNull().and { block }
        val andCall = node.findLastCallExpression() ?: return
        if (andCall.callExpressionName() != "and") return
        if (!andCall.hasLambdaArgument()) return
        // and() should have no value arguments, only a lambda
        if (!andCall.hasNoValueArguments()) return

        val receiver = node.dotQualifiedReceiver() ?: return
        if (receiver.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        val isNotNullCall = receiver.findLastCallExpression() ?: return
        if (isNotNullCall.callExpressionName() != "isNotNull") return
        if (!isNotNullCall.hasNoValueArguments()) return

        val baseReceiver = receiver.dotQualifiedReceiver() ?: return

        emit(
            isNotNullCall.startOffset,
            "Use withNotNull { } instead of isNotNull().and { }",
            true,
        ).ifAutocorrectAllowed {
            // Rename and -> withNotNull
            val refExpr = andCall.findChildByType(ElementType.REFERENCE_EXPRESSION)!!
            (refExpr.firstChildNode as LeafPsiElement).rawReplaceWithText("withNotNull")

            // Remove .isNotNull() by replacing receiver DOT_QUALIFIED_EXPRESSION with base
            node.replaceChild(receiver, baseReceiver.clone() as ASTNode)
            ensureImport(node, "strikt.assertions.withNotNull")
        }
    }
}
