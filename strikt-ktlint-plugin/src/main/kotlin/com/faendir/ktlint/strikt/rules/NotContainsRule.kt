package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.AutocorrectDecision
import com.pinterest.ktlint.rule.engine.core.api.ElementType
import org.jetbrains.kotlin.com.intellij.lang.ASTNode

/**
 * Replaces `.not().contains(x)` with `.doesNotContain(x)`.
 *
 * Skips the replacement when `contains()` has a single string literal argument,
 * because `doesNotContain` does not exist for `CharSequence` subjects in Strikt.
 */
class NotContainsRule : NotRenameRule("not-contains", "contains", "doesNotContain") {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision,
    ) {
        val match = node.findNotReceiverChain() ?: return
        if (match.lastCall.callExpressionName() != "contains") return

        // If the single argument to contains() is a string literal, this is likely
        // a CharSequence.contains() call, and doesNotContain does not exist for strings.
        val argList = match.lastCall.valueArgumentList()
        val singleArg = argList?.singleValueArgument()
        if (singleArg != null) {
            val argExpr = singleArg.argumentExpression()
            if (argExpr?.elementType == ElementType.STRING_TEMPLATE) return
        }

        super.beforeVisitChildNodes(node, emit)
    }
}
