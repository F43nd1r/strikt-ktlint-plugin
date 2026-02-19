package com.faendir.ktlint.strikt.rules

import com.faendir.ktlint.strikt.RULESET_ID
import com.pinterest.ktlint.rule.engine.core.api.ElementType
import com.pinterest.ktlint.rule.engine.core.api.RuleId
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.ImportPath

internal fun ruleId(name: String) = RuleId("$RULESET_ID:$name")

/**
 * Ensures that the given fully qualified import exists in the file containing [node].
 * If a wildcard import already covers the package, no import is added.
 * If the exact import already exists, no import is added.
 * If there are no existing imports in the file, no import is added (the file
 * likely uses star imports or doesn't use explicit imports).
 */
internal fun ensureImport(node: ASTNode, fqImport: String) {
    val fileNode = node.findFileNode() ?: return
    val importList = fileNode.findChildByType(ElementType.IMPORT_LIST) ?: return

    val existingDirectives = importList.getChildren(null)
        .filter { it.elementType == ElementType.IMPORT_DIRECTIVE }

    // If there are no existing imports, don't add one
    if (existingDirectives.isEmpty()) return

    val packageName = fqImport.substringBeforeLast('.')
    val wildcardImport = "$packageName.*"

    val existingImportTexts = existingDirectives
        .map { it.text.removePrefix("import ").trim() }

    if (fqImport in existingImportTexts || wildcardImport in existingImportTexts) return

    // Create a new import directive using KtPsiFactory
    val psiFactory = KtPsiFactory(node.psi.project)
    val importDirective = psiFactory.createImportDirective(
        ImportPath(FqName(fqImport), isAllUnder = false),
    )

    // Add a newline before the new import and append it to the import list
    importList.addChild(PsiWhiteSpaceImpl("\n"), null)
    importList.addChild(importDirective.node, null)
}

/**
 * Walks up the tree to find the FILE node.
 */
private fun ASTNode.findFileNode(): ASTNode? {
    var current: ASTNode? = this
    while (current != null && current.elementType != ElementType.FILE) {
        current = current.treeParent
    }
    return current
}

/**
 * Finds the CALL_EXPRESSION that is the last segment of a DOT_QUALIFIED_EXPRESSION.
 * For `a.b.c()`, this returns the node for `c()`.
 */
internal fun ASTNode.findLastCallExpression(): ASTNode? {
    if (elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return null
    val children = getChildren(null)
    val lastChild = children.lastOrNull() ?: return null
    return if (lastChild.elementType == ElementType.CALL_EXPRESSION) lastChild else null
}

/**
 * Gets the function name from a CALL_EXPRESSION node.
 */
internal fun ASTNode.callExpressionName(): String? {
    if (elementType != ElementType.CALL_EXPRESSION) return null
    return findChildByType(ElementType.REFERENCE_EXPRESSION)?.text
}

/**
 * Gets the VALUE_ARGUMENT_LIST from a CALL_EXPRESSION node.
 */
internal fun ASTNode.valueArgumentList(): ASTNode? {
    if (elementType != ElementType.CALL_EXPRESSION) return null
    return findChildByType(ElementType.VALUE_ARGUMENT_LIST)
}

/**
 * Gets the single VALUE_ARGUMENT from a VALUE_ARGUMENT_LIST, or null if there's not exactly one.
 */
internal fun ASTNode.singleValueArgument(): ASTNode? {
    if (elementType != ElementType.VALUE_ARGUMENT_LIST) return null
    val args = getChildren(null).filter { it.elementType == ElementType.VALUE_ARGUMENT }
    return if (args.size == 1) args[0] else null
}

/**
 * Gets the expression inside a VALUE_ARGUMENT node (skips whitespace, etc.)
 */
internal fun ASTNode.argumentExpression(): ASTNode? {
    if (elementType != ElementType.VALUE_ARGUMENT) return null
    return getChildren(null).lastOrNull { it.elementType != ElementType.WHITE_SPACE }
}

/**
 * Gets the receiver part of a DOT_QUALIFIED_EXPRESSION (everything before the last dot).
 */
internal fun ASTNode.dotQualifiedReceiver(): ASTNode? {
    if (elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return null
    return getChildren(null).firstOrNull {
        it.elementType != ElementType.WHITE_SPACE && it.elementType != ElementType.DOT
    }
}

/**
 * Checks whether a CALL_EXPRESSION has no value arguments (may still have type arguments).
 */
internal fun ASTNode.hasNoValueArguments(): Boolean {
    if (elementType != ElementType.CALL_EXPRESSION) return false
    val argList = findChildByType(ElementType.VALUE_ARGUMENT_LIST) ?: return true
    return argList.getChildren(null).none { it.elementType == ElementType.VALUE_ARGUMENT }
}

/**
 * Checks whether a CALL_EXPRESSION has a trailing lambda argument.
 */
internal fun ASTNode.hasLambdaArgument(): Boolean {
    if (elementType != ElementType.CALL_EXPRESSION) return false
    return getChildren(null).any { it.elementType == ElementType.LAMBDA_ARGUMENT }
}

/**
 * Gets the LAMBDA_ARGUMENT from a CALL_EXPRESSION node.
 */
internal fun ASTNode.lambdaArgument(): ASTNode? {
    if (elementType != ElementType.CALL_EXPRESSION) return null
    return getChildren(null).firstOrNull { it.elementType == ElementType.LAMBDA_ARGUMENT }
}

/**
 * For a chain like `<base>.not().someCall(args)`, checks if the receiver of the outer
 * DOT_QUALIFIED_EXPRESSION ends in a `not()` call with no arguments.
 *
 * Returns the receiver DOT_QUALIFIED_EXPRESSION containing `.not()` if matched, null otherwise.
 */
internal fun ASTNode.findNotReceiverChain(): NotChainMatch? {
    if (elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return null

    val lastCall = findLastCallExpression() ?: return null
    val receiver = dotQualifiedReceiver() ?: return null

    if (receiver.elementType != ElementType.DOT_QUALIFIED_EXPRESSION) return null

    val notCall = receiver.findLastCallExpression() ?: return null
    if (notCall.callExpressionName() != "not") return null
    if (!notCall.hasNoValueArguments()) return null

    val baseReceiver = receiver.dotQualifiedReceiver() ?: return null

    return NotChainMatch(
        outerDotExpr = this,
        lastCall = lastCall,
        notDotExpr = receiver,
        notCall = notCall,
        baseReceiver = baseReceiver,
    )
}

/**
 * Result of matching a `.not().someCall()` chain.
 */
internal data class NotChainMatch(
    val outerDotExpr: ASTNode,
    val lastCall: ASTNode,
    val notDotExpr: ASTNode,
    val notCall: ASTNode,
    val baseReceiver: ASTNode,
)
