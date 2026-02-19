package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.*
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.LeafPsiElement

/**
 * Replaces:
 * - `.get(X::size).isEqualTo(n)` with `.hasSize(n)`
 * - `.get { size }.isEqualTo(n)` with `.hasSize(n)`
 * - `.get(X::length).isEqualTo(n)` with `.hasLength(n)`
 * - `.get { length }.isEqualTo(n)` with `.hasLength(n)`
 */
class GetSizeOrLengthRule : StriktRule("get-size-or-length") {

    override fun beforeVisitChildNodes(
        node: ASTNode,
        emit: (offset: Int, errorMessage: String, canBeAutoCorrected: Boolean) -> AutocorrectDecision,
    ) {
        if (node.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        // We need a chain like: <receiver>.get(<size/length>).isEqualTo(n)
        // The AST for a.b().c() is:
        //   DOT_QUALIFIED_EXPRESSION
        //     DOT_QUALIFIED_EXPRESSION  (a.b())
        //       <receiver a>
        //       DOT
        //       CALL_EXPRESSION (b())
        //     DOT
        //     CALL_EXPRESSION (c())

        val isEqualToCall = node.findLastCallExpression() ?: return
        if (isEqualToCall.callExpressionName() != "isEqualTo") return

        val receiver = node.dotQualifiedReceiver() ?: return
        if (receiver.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return

        val getCall = receiver.findLastCallExpression() ?: return
        if (getCall.callExpressionName() != "get") return

        val propertyName = extractPropertyName(getCall) ?: return
        val replacement = PROPERTY_TO_ASSERTION[propertyName] ?: return

        emit(
            getCall.startOffset,
            "Use $replacement() instead of get($propertyName).isEqualTo()",
            true,
        ).ifAutocorrectAllowed {
            // We want to transform: <base>.get(X::size).isEqualTo(n)
            // into:                  <base>.hasSize(n)

            // Step 1: Replace the isEqualTo name with hasSize/hasLength
            val isEqualToRef = isEqualToCall.findChildByType(ElementType.REFERENCE_EXPRESSION)!!
            (isEqualToRef.firstChildNode as LeafPsiElement).rawReplaceWithText(replacement)

            // Step 2: Replace the receiver DOT_QUALIFIED_EXPRESSION (which is <base>.get(...))
            // with just <base>
            val baseReceiver = receiver.dotQualifiedReceiver() ?: return@ifAutocorrectAllowed
            node.replaceChild(receiver, baseReceiver.clone() as ASTNode)
            ensureImport(node, "strikt.assertions.$replacement")
        }
    }

    /**
     * Extracts the property name from a `get()` call.
     * Supports:
     * - `get(X::size)` -> "size" (callable reference)
     * - `get { size }` -> "size" (lambda with single reference expression)
     */
    private fun extractPropertyName(getCall: ASTNode): String? {
        // Try callable reference: get(X::size)
        val argList = getCall.valueArgumentList()
        if (argList != null) {
            val singleArg = argList.singleValueArgument()
            if (singleArg != null) {
                val argExpr = singleArg.argumentExpression()
                if (argExpr != null && argExpr.elementType == ElementType.CALLABLE_REFERENCE_EXPRESSION) {
                    // CALLABLE_REFERENCE_EXPRESSION children vary:
                    //   With type args:    [TYPE_REFERENCE, COLONCOLON, REFERENCE_EXPRESSION]
                    //   Without type args: [REFERENCE_EXPRESSION, COLONCOLON, REFERENCE_EXPRESSION]
                    // We always want the LAST REFERENCE_EXPRESSION (the property name after ::)
                    val refExpr = argExpr.getChildren(null)
                        .lastOrNull { it.elementType == ElementType.REFERENCE_EXPRESSION }
                    return refExpr?.text
                }
            }
        }

        // Try lambda: get { size }
        val lambdaArg = getCall.getChildren(null)
            .firstOrNull { it.elementType == ElementType.LAMBDA_ARGUMENT }
        if (lambdaArg != null) {
            val lambdaExpr = lambdaArg.findChildByType(ElementType.LAMBDA_EXPRESSION)
            val body = lambdaExpr?.findChildByType(ElementType.FUNCTION_LITERAL)
                ?.findChildByType(ElementType.BLOCK)
            if (body != null) {
                // The block should contain a single REFERENCE_EXPRESSION
                val expressions = body.getChildren(null)
                    .filter { it.elementType != ElementType.WHITE_SPACE }
                if (expressions.size == 1 && expressions[0].elementType == ElementType.REFERENCE_EXPRESSION) {
                    return expressions[0].text
                }
            }
        }

        return null
    }

    companion object {
        private val PROPERTY_TO_ASSERTION = mapOf(
            "size" to "hasSize",
            "length" to "hasLength",
        )
    }
}
