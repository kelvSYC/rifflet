package com.kelvsyc.rifflet.civ3.validation

import com.kelvsyc.rifflet.civ3.Civ3File
import com.kelvsyc.rifflet.core.RiffletParseException

/**
 * A single editor-confirmed constraint checked against a parsed [Civ3File]. Implementations
 * never throw; a rule whose required section(s) are absent from [Civ3File.sections] returns an
 * empty list rather than failing.
 *
 * This is a distinct concern from parsing: a [RiffletParseException] means the bytes couldn't
 * become a [Civ3File] at all (the file's *shape* doesn't obey the wire format). A [ValidationRule]
 * only ever runs against an already-successfully-parsed [Civ3File] and reports on its *content* —
 * whether the real Civ3 editors would consider the values present sane — which is why it returns
 * issues rather than throwing.
 */
fun interface ValidationRule {
    fun validate(file: Civ3File): List<ValidationIssue>
}
