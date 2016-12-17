package com.codemate.brewflop

import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class RegexMatcher(private val regex: String) : BaseMatcher<Any>() {
    override fun matches(o: Any) = regex.toRegex(RegexOption.DOT_MATCHES_ALL).matches(o as String)

    override fun describeTo(description: Description) {
        description.appendText("matches regex $regex")
    }

    companion object {
        fun matchesPattern(regex: String) = RegexMatcher(regex)
    }
}
