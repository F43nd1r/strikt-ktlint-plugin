package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces `.not().$matchName(x)` with `.$replacementName(x)`.
 *
 * @param matchName the function name to match after `.not()` (e.g. `isEqualTo`, `contains`)
 * @param replacementName the function name to replace it with (e.g. `isNotEqualTo`, `doesNotContain`)
 */
open class NotRenameRule(
    ruleName: String,
    private val matchName: String,
    private val replacementName: String,
) : StriktRule(ruleName) {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision,
    ) {
        val match = node.findNotReceiverChain() ?: return
        if (match.lastCall.callExpressionName() != matchName) return

        emit(
            match.notCall.startOffset,
            "Use $replacementName() instead of not().$matchName()",
            true,
        ).ifAutocorrectAllowed {
            val refExpr = match.lastCall.findChildByType(ElementType.REFERENCE_EXPRESSION)!!
            (refExpr.firstChildNode as LeafPsiElement).rawReplaceWithText(replacementName)

            node.replaceChild(match.notDotExpr, match.baseReceiver.clone() as ASTNode)
            ensureImport(node, "strikt.assertions.$replacementName")
        }
    }
}
