package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces traversal + `.and { }` patterns with their `with*` shorthands:
 * - `.single().and { block }` → `.withSingle { block }`
 * - `.first().and { block }` → `.withFirst { block }`
 * - `.last().and { block }` → `.withLast { block }`
 * - `.elementAt(i).and { block }` → `.withElementAt(i) { block }`
 */
class TraversalAndToWithRule : StriktRule("traversal-and-to-with") {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision,
    ) {
        if (node.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        // Match: <receiver>.traversal().and { block }
        val andCall = node.findLastCallExpression() ?: return
        if (andCall.callExpressionName() != "and") return
        if (!andCall.hasLambdaArgument()) return
        if (!andCall.hasNoValueArguments()) return

        val receiver = node.dotQualifiedReceiver() ?: return
        if (receiver.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        val traversalCall = receiver.findLastCallExpression() ?: return
        val traversalName = traversalCall.callExpressionName() ?: return
        val replacement = TRAVERSAL_TO_WITH[traversalName] ?: return

        // For single/first/last: must have no value arguments
        // For elementAt: must have exactly one value argument (the index)
        if (traversalName == "elementAt") {
            val argList = traversalCall.valueArgumentList() ?: return
            if (argList.singleValueArgument() == null) return
        } else {
            if (!traversalCall.hasNoValueArguments()) return
        }

        val baseReceiver = receiver.dotQualifiedReceiver() ?: return

        emit(
            traversalCall.startOffset,
            "Use $replacement { } instead of $traversalName().and { }",
            true,
        ).ifAutocorrectAllowed {
            // Ensure import before any node replacement (node may be detached)
            ensureImport(node, "strikt.assertions.$replacement")
            if (traversalName == "elementAt") {
                // For elementAt(i).and { block } -> withElementAt(i) { block }
                // Use text replacement to avoid whitespace issues with AST splicing
                val argList = traversalCall.valueArgumentList()!!
                val lambdaArg = andCall.lambdaArgument()!!
                val newCallText = "$replacement${argList.text} ${lambdaArg.text}"
                val newText = "${baseReceiver.text}.$newCallText"
                node.treeParent.replaceChild(
                    node,
                    LeafPsiElement(ElementType.DOT_QUALIFIED_EXPRESSION, newText),
                )
            } else {
                // For single/first/last: rename and -> withSingle/withFirst/withLast, remove traversal
                val refExpr = andCall.findChildByType(ElementType.REFERENCE_EXPRESSION)!!
                (refExpr.firstChildNode as LeafPsiElement).rawReplaceWithText(replacement)

                // Remove .traversal() by replacing receiver DOT_QUALIFIED_EXPRESSION with base
                node.replaceChild(receiver, baseReceiver.clone() as ASTNode)
            }
        }
    }

    companion object {
        private val TRAVERSAL_TO_WITH = mapOf(
            "single" to "withSingle",
            "first" to "withFirst",
            "last" to "withLast",
            "elementAt" to "withElementAt",
        )
    }
}
