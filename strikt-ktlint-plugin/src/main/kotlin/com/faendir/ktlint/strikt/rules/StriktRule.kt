package com.faendir.ktlint.strikt.rules

import com.pinterest.ktlint.rule.engine.core.api.Rule
import com.pinterest.ktlint.rule.engine.core.api.RuleAutocorrectApproveHandler

/**
 * Base class for all Strikt ktlint rules.
 *
 * Provides the shared [About] metadata and implements [RuleAutocorrectApproveHandler]
 * so subclasses only need to override [beforeVisitChildNodes].
 */
abstract class StriktRule(name: String) : Rule(
    ruleId(name),
    About(
        maintainer = "F43nd1r",
        repositoryUrl = "https://github.com/F43nd1r/strikt-ktlint-plugin",
        issueTrackerUrl = "https://github.com/F43nd1r/strikt-ktlint-plugin/issues",
    ),
), RuleAutocorrectApproveHandler
