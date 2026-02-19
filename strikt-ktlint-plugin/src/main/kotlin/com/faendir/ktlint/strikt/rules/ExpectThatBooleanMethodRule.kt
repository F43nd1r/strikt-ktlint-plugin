package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces patterns where a boolean method result is passed to expectThat and checked with isTrue/isFalse:
 * - `expectThat(x.isEmpty()).isTrue()` → `expectThat(x).isEmpty()`
 * - `expectThat(x.isEmpty()).isFalse()` → `expectThat(x).isNotEmpty()`
 * - `expectThat(x.isNotEmpty()).isTrue()` → `expectThat(x).isNotEmpty()`
 * - `expectThat(x.isNotEmpty()).isFalse()` → `expectThat(x).isEmpty()`
 * - `expectThat(x.isBlank()).isTrue()` → `expectThat(x).isBlank()`
 * - `expectThat(x.isBlank()).isFalse()` → `expectThat(x).isNotBlank()`
 * - `expectThat(x.isNotBlank()).isTrue()` → `expectThat(x).isNotBlank()`
 * - `expectThat(x.isNotBlank()).isFalse()` → `expectThat(x).isBlank()`
 */
class ExpectThatBooleanMethodRule : StriktRule("expect-that-boolean-method") {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision,
    ) {
        if (node.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        // Match: expectThat(x.boolMethod()).isTrue/isFalse()
        val assertionCall = node.findLastCallExpression() ?: return
        val assertionName = assertionCall.callExpressionName() ?: return
        if (assertionName != "isTrue" && assertionName != "isFalse") return
        if (!assertionCall.hasNoValueArguments()) return

        val receiver = node.dotQualifiedReceiver() ?: return
        if (receiver.elementType != ElementType.CALL_EXPRESSION) return
        if (receiver.callExpressionName() != "expectThat") return

        // Get the single argument to expectThat
        val expectArgList = receiver.valueArgumentList() ?: return
        val expectSingleArg = expectArgList.singleValueArgument() ?: return
        val expectArgExpr = expectSingleArg.argumentExpression() ?: return

        // The argument must be a DOT_QUALIFIED_EXPRESSION ending in a known boolean method
        if (expectArgExpr.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return
        val innerCall = expectArgExpr.findLastCallExpression() ?: return
        val innerMethodName = innerCall.callExpressionName() ?: return
        if (innerMethodName !in BOOLEAN_METHODS) return
        if (!innerCall.hasNoValueArguments()) return

        val baseReceiver = expectArgExpr.dotQualifiedReceiver() ?: return

        val isFalse = assertionName == "isFalse"
        val replacement = if (isFalse) {
            BOOLEAN_METHODS_NEGATION[innerMethodName] ?: return
        } else {
            innerMethodName
        }

        emit(
            receiver.startOffset,
            "Use $replacement() instead of expectThat(x.$innerMethodName()).$assertionName()",
            true,
        ).ifAutocorrectAllowed {
            // Step 1: Replace the argument of expectThat from x.method() to just x
            expectSingleArg.replaceChild(expectArgExpr, baseReceiver.clone() as ASTNode)

            // Step 2: Rename isTrue/isFalse to the target assertion method
            val refExpr = assertionCall.findChildByType(ElementType.REFERENCE_EXPRESSION)!!
            (refExpr.firstChildNode as LeafPsiElement).rawReplaceWithText(replacement)
            ensureImport(node, "strikt.assertions.$replacement")
        }
    }

    companion object {
        private val BOOLEAN_METHODS = setOf(
            "isEmpty", "isNotEmpty",
            "isBlank", "isNotBlank",
        )

        private val BOOLEAN_METHODS_NEGATION = mapOf(
            "isEmpty" to "isNotEmpty",
            "isNotEmpty" to "isEmpty",
            "isBlank" to "isNotBlank",
            "isNotBlank" to "isBlank",
        )
    }
}
