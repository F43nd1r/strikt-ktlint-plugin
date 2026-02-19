package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces `expectCatching { block }.isFailure().isA<E>()` with `expectThrows<E> { block }`.
 */
class ExpectCatchingToExpectThrowsRule : StriktRule("expect-catching-to-expect-throws") {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision,
    ) {
        if (node.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        // Match: expectCatching { block }.isFailure().isA<E>()
        val isACall = node.findLastCallExpression() ?: return
        if (isACall.callExpressionName() != "isA") return
        if (!isACall.hasNoValueArguments()) return

        // isA must have a type argument
        val typeArgList = isACall.getChildren(null)
            .firstOrNull { it.elementType == ElementType.TYPE_ARGUMENT_LIST }
            ?: return

        val middleReceiver = node.dotQualifiedReceiver() ?: return
        if (middleReceiver.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        val isFailureCall = middleReceiver.findLastCallExpression() ?: return
        if (isFailureCall.callExpressionName() != "isFailure") return
        if (!isFailureCall.hasNoValueArguments()) return

        val expectCatchingCall = middleReceiver.dotQualifiedReceiver() ?: return
        if (expectCatchingCall.elementType != ElementType.CALL_EXPRESSION) return
        if (expectCatchingCall.callExpressionName() != "expectCatching") return
        if (!expectCatchingCall.hasLambdaArgument()) return

        val lambdaArg = expectCatchingCall.lambdaArgument()!!
        val typeArgText = typeArgList.text
        val lambdaText = lambdaArg.text

        emit(
            expectCatchingCall.startOffset,
            "Use expectThrows${typeArgText} { } instead of expectCatching { }.isFailure().isA${typeArgText}()",
            true,
        ).ifAutocorrectAllowed {
            // Replace the entire expression with reconstructed text
            val replacement = "expectThrows$typeArgText $lambdaText"
            // Ensure import before replacing the node (node will be detached after replacement)
            ensureImport(node, "strikt.api.expectThrows")
            node.treeParent.replaceChild(
                node,
                LeafPsiElement(ElementType.DOT_QUALIFIED_EXPRESSION, replacement),
            )
        }
    }
}
