package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces `.not().X()` with `.Y()` for assertion pairs that have direct negation counterparts:
 * - `.not().isNull()` → `.isNotNull()`
 * - `.not().isNotNull()` → `.isNull()`
 * - `.not().isEmpty()` → `.isNotEmpty()`
 * - `.not().isNotEmpty()` → `.isEmpty()`
 * - `.not().isBlank()` → `.isNotBlank()`
 * - `.not().isNotBlank()` → `.isBlank()`
 */
class NotNegationRule : StriktRule("not-negation") {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision,
    ) {
        val match = node.findNotReceiverChain() ?: return
        val callName = match.lastCall.callExpressionName() ?: return
        val replacement = NEGATION_PAIRS[callName] ?: return

        // The negated call must have no value arguments
        if (!match.lastCall.hasNoValueArguments()) return

        emit(
            match.notCall.startOffset,
            "Use $replacement() instead of not().$callName()",
            true,
        ).ifAutocorrectAllowed {
            // Rename the assertion
            val refExpr = match.lastCall.findChildByType(ElementType.REFERENCE_EXPRESSION)!!
            (refExpr.firstChildNode as LeafPsiElement).rawReplaceWithText(replacement)

            // Remove .not() by replacing the not-DOT_QUALIFIED_EXPRESSION with its base receiver
            node.replaceChild(match.notDotExpr, match.baseReceiver.clone() as ASTNode)
            ensureImport(node, "strikt.assertions.$replacement")
        }
    }

    companion object {
        private val NEGATION_PAIRS = mapOf(
            "isNull" to "isNotNull",
            "isNotNull" to "isNull",
            "isEmpty" to "isNotEmpty",
            "isNotEmpty" to "isEmpty",
            "isBlank" to "isNotBlank",
            "isNotBlank" to "isBlank",
        )
    }
}
