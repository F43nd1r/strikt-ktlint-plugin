package com.faendir.ktlint.strikt

import com.faendir.ktlint.strikt.rules.EqualsMisuseRule
import com.faendir.ktlint.strikt.rules.ExpectCatchingToExpectThrowsRule
import com.faendir.ktlint.strikt.rules.ExpectThatBooleanMethodRule
import com.faendir.ktlint.strikt.rules.ExpectThatSizeOrLengthRule
import com.faendir.ktlint.strikt.rules.GetSizeOrLengthRule
import com.faendir.ktlint.strikt.rules.HasLengthZeroRule
import com.faendir.ktlint.strikt.rules.HasSizeZeroRule
import com.faendir.ktlint.strikt.rules.IsEqualToBooleanRule
import com.faendir.ktlint.strikt.rules.IsEqualToCollectionRule
import com.faendir.ktlint.strikt.rules.IsEqualToEmptyCollectionRule
import com.faendir.ktlint.strikt.rules.IsEqualToEmptyStringRule
import com.faendir.ktlint.strikt.rules.IsEqualToNullRule
import com.faendir.ktlint.strikt.rules.IsNotEqualToNullRule
import com.faendir.ktlint.strikt.rules.IsNotNullAndRule
import com.faendir.ktlint.strikt.rules.NotIsEqualToRule
import com.faendir.ktlint.strikt.rules.NotNegationRule
import com.faendir.ktlint.strikt.rules.TraversalAndToWithRule
import com.pinterest.ktlint.cli.ruleset.core.api.RuleSetProviderV3
import com.pinterest.ktlint.rule.engine.core.api.RuleProvider
import com.pinterest.ktlint.rule.engine.core.api.RuleSetId

const val RULESET_ID = "strikt-assertion"

class StriktRuleSetProvider : RuleSetProviderV3(RuleSetId(RULESET_ID)) {
    override fun getRuleProviders(): Set<RuleProvider> = setOf(
        RuleProvider { IsEqualToNullRule() },
        RuleProvider { IsNotEqualToNullRule() },
        RuleProvider { IsEqualToBooleanRule() },
        RuleProvider { IsEqualToCollectionRule() },
        RuleProvider { IsEqualToEmptyCollectionRule() },
        RuleProvider { IsEqualToEmptyStringRule() },
        RuleProvider { GetSizeOrLengthRule() },
        RuleProvider { ExpectThatSizeOrLengthRule() },
        RuleProvider { HasSizeZeroRule() },
        RuleProvider { HasLengthZeroRule() },
        RuleProvider { NotIsEqualToRule() },
        RuleProvider { NotNegationRule() },
        RuleProvider { ExpectThatBooleanMethodRule() },
        RuleProvider { IsNotNullAndRule() },
        RuleProvider { ExpectCatchingToExpectThrowsRule() },
        RuleProvider { TraversalAndToWithRule() },
        RuleProvider { EqualsMisuseRule() },
    )
}
