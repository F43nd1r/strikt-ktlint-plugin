package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode

/**
 * Warns about `.equals(x)` usage on Strikt assertion builders.
 *
 * `.equals()` on a Builder is `Any.equals()` which returns a Boolean that is silently discarded.
 * The user almost certainly intended `.isEqualTo(x)`.
 *
 * This rule emits a warning only (no autofix) because the intent may vary.
 */
class EqualsMisuseRule : StriktRule("equals-misuse") {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision,
    ) {
        if (node.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        val callExpression = node.findLastCallExpression() ?: return
        if (callExpression.callExpressionName() != "equals") return

        // Check that the receiver chain contains an assertion-starting call
        val receiver = node.dotQualifiedReceiver() ?: return
        if (!containsAssertionCall(receiver)) return

        emit(
            callExpression.startOffset,
            ".equals() does not assert. Use isEqualTo() instead",
            false,
        )
    }

    private fun containsAssertionCall(node: ASTNode): Boolean {
        return when (node.elementType) {
            ElementType.CALL_EXPRESSION -> {
                node.callExpressionName() in ASSERTION_STARTING_CALLS
            }
            ElementType.DOT_QUALIFIED_EXPRESSION -> {
                val lastCall = node.findLastCallExpression()
                if (lastCall?.callExpressionName() in ASSERTION_STARTING_CALLS) return true
                val receiver = node.dotQualifiedReceiver() ?: return false
                containsAssertionCall(receiver)
            }
            else -> false
        }
    }

    companion object {
        private val ASSERTION_STARTING_CALLS = setOf(
            "expectThat", "expect", "expectCatching", "expectThrows",
            "get", "isA", "isNotNull", "and", "not",
            "first", "last", "single", "elementAt",
            "with", "withNotNull", "withSingle", "withFirst", "withLast", "withElementAt",
            "map", "flatMap", "filter", "filterNot", "filterIsInstance",
            "all", "any", "none", "one",
        )
    }
}
