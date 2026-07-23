# rifflet-civ3 editor-behavior validation layer

## Motivation

rifflet-civ3 currently encodes knowledge of official-map-editor behavior (valid index
ranges, sentinel values, era-dependent field meaning, bit-flag semantics) entirely as
KDoc prose on `*EntryReferences.kt` resolver functions. That knowledge can inform a
human reading the source, but nothing in the library can check a parsed file against
it programmatically.

This adds a validation layer as a **public API feature for consumers** of the
library: given a parsed `Civ3File`, tell the caller which of the editor's known
constraints the file's contents appear to violate — analogous to a linter for `.biq`/
`.bic`/`.bix` files, built on the constraints this project has already confirmed
against the real Conquests/PTW editors.

A secondary, non-primary benefit is that these same checks can serve as an internal
consistency check during rifflet-civ3's own development, but the design optimizes for
the public-API use case; internal dev-time convenience follows from that, not the
reverse.

## Scope and rollout order

Validation rules cover (at least) four kinds of constraint, in the order they will be
built out:

1. **Cross-reference bounds (A)** — an index field must resolve to a real entry in
   the section it references, or match a documented sentinel (e.g. `-1`, `-2`, or a
   section's own entry count used as an "out of band" marker).
2. **Era-appropriateness (B)** — a field/bit only means what it's documented to mean
   for certain `Civ3FormatEra` values (e.g. the PTW-vs-Conquests
   `requiredGoodsMustBeInCityRadius` bit-position split).
3. **Bit/enum validity (C)** — combinations of bits/fields the real editor's UI
   structurally prevents (e.g. an "improvement" building having any "wonder" flag
   bits set).
4. **Cross-entry / cross-section / file-structural rules** — constraints that can't
   be evaluated from a single entry in isolation: cardinality within a section (e.g.
   at most one "center of empire" building across all of `BLDG`), counts spanning
   multiple sections (e.g. total spaceship parts), or file-level structure (e.g. a
   `Civ3File` built from the "Custom Rules" category must contain a specific set of
   sections, in a specific order).

v1 implements the mechanism end-to-end using a single seed rule from category (1):
`TerrEntry.pollutionEffect`, which already has a fully confirmed sentinel model (see
`TerrPollutionEffect` in `TerrEntryReferences.kt`). Categories (2)-(4) are validated
architecturally (the design must not need retrofitting to add them) but are not
populated with rule content in v1.

## Core types

New package: `com.kelvsyc.rifflet.civ3.validation`, inside the existing
`rifflet-civ3` component (not a new Gradle module).

```kotlin
enum class ValidationSeverity { ERROR, WARNING }

data class ValidationIssue(
    val severity: ValidationSeverity,
    val section: ChunkId,  // e.g. Civ3SectionIds.TERR — reuses the section's own identifier
    val index: Int,        // entry's position within that section
    val field: String,     // e.g. "pollutionEffect"
    val message: String,
)
```

`section` is typed as the existing `ChunkId` (from `rifflet-core`, already exposed by every
`Civ3Section.chunkId`) rather than a raw `String`, so rules populate it from the section
they already pulled out of the file (e.g. `terrSection.chunkId`) instead of hand-typing a
literal that could drift from `Civ3SectionIds`.

`WARNING` is included from the start even though v1 will likely only ever emit
`ERROR`, since era-conditioning (B) is expected to introduce genuinely uncertain
cases (e.g. a vestigial field in a given era having a nonzero value: suspicious, not
necessarily wrong) where a hard `ERROR` would be the wrong signal.

## Rule contract

All rules — regardless of scope — share one contract, general enough to cover
everything from a single field to file-wide structure without changing shape later:

```kotlin
fun interface ValidationRule {
    fun validate(file: Civ3File): List<ValidationIssue>
}
```

Individual rules are ordinary top-level functions matching this shape by SAM
conversion — no generic rule-engine, no reflection, no registry/auto-discovery
mechanism in v1 (deferred until the number of rules makes a hand-written list
unwieldy). Each rule is colocated with the entry type it primarily concerns, in a
new sibling file next to the existing `*EntryReferences.kt` for that entry (e.g.
`TerrEntryValidation.kt` next to `TerrEntryReferences.kt`), keeping "resolves this
field" (existing, always succeeds) visibly distinct from "asserts this field is
well-formed per the editor" (new, opt-in). Rules whose primary subject is the whole
file rather than one entry type (category 4) live in a new `Civ3FileValidation.kt`.

### Worked example (v1 seed rule, category A)

```kotlin
// TerrEntryValidation.kt, alongside TerrEntryReferences.kt

fun validatePollutionEffect(file: Civ3File): List<ValidationIssue> {
    val terrSection = file.sections.filterIsInstance<TerrSection>().singleOrNull()
        ?: return emptyList()
    return terrSection.entries.mapIndexedNotNull { index, entry ->
        val resolved = entry.pollutionEffectResolved(terrSection.entries)
        if (resolved is TerrPollutionEffect.Terrain && resolved.terrain == null) {
            ValidationIssue(
                ValidationSeverity.ERROR, terrSection.chunkId, index, "pollutionEffect",
                "pollutionEffect=${entry.pollutionEffect} is not -1, not the base-terrain " +
                    "sentinel (${terrSection.entries.size}), and not a valid TERR index " +
                    "(0..<${terrSection.entries.size})",
            )
        } else {
            null
        }
    }
}
```

The key move: validation is built **on top of** the existing resolver
(`pollutionEffectResolved`), not a reimplementation of its sentinel logic. Where a
field already has a rich resolved type, the rule just checks whether resolution fell
through to an unresolved reference. Where a field is a plain `getOrNull` with a
documented sentinel but no rich type, the rule does its own small sentinel check
inline — there is no shared generic mechanism for this, matching how
`*EntryReferences.kt` already favors one hand-written function per field over a
declarative framework.

### Other scopes, same contract (illustrative, not built in v1)

- **Section-scoped** (category 4, cardinality): pull `BldgSection.entries` and
  examine the whole list at once — e.g. count entries with a given flag set, and
  return issues for whichever entries violate an "at most one" constraint.
- **Cross-section** (category 4, cross-section counts): pull `BldgSection` and
  whatever section holds the expected total, cross-check.
- **File-structural** (category 4, section presence/order): operate on
  `file.sections` itself rather than on any section's entries — e.g. confirm a
  "Custom Rules"-category file contains its required set of sections in the expected
  order.

### Aggregation

```kotlin
fun Civ3File.validate(): List<ValidationIssue> =
    listOf<ValidationRule>(::validatePollutionEffect)
        .flatMap { it.validate(this) }
```

The list of rules is hand-written and grows one line per confirmed rule; there is no
registry-building or auto-discovery machinery to design or maintain in v1.

## Error handling

Rules never throw. Every rule returns `List<ValidationIssue>` (possibly empty).

If a rule's required section is entirely absent from the file, it returns no issues
for that section — **but only where section presence/order isn't itself the subject
of a dedicated category-4 rule**. Once file-structural rules exist (e.g. "a
Custom-Rules-category file must contain sections X, Y, Z in this order"), a genuinely
missing required section is caught there, once, with a clear message — not
re-flagged as noise by every individual field-level rule that happens to need that
section. This keeps the two concerns cleanly separated: "is this file
structurally complete" is one rule's job; "is this field's value well-formed, given
the data available" is another's, and the latter shouldn't degrade into false
negatives *or* redundant errors when the former's job hasn't run or has already
covered the gap.

## Testing

None of the real `.biq`/`.bix`/`.bic` sample files used for cross-checking editor
behavior are committed to this repo (they live outside version control, in a
gitignored workspace-local directory) — Civ3 game/scenario data isn't this project's
to redistribute. Combined with the fact that editor-produced files are, by
construction, already valid (deliberately constructing a real file with a specific
targeted violation, without introducing unrelated content issues, would be
significant effort for little benefit), **all validation-layer tests use synthetic,
hand-constructed `Civ3File`/section/entry values** — trivial here since every type
involved is a plain immutable `data class`.

Each rule's test coverage includes: a clean case (no issues), each documented
sentinel value (no issues), and at least one genuine violation (exactly one issue,
with the expected severity/section/index/field). This proves a rule correctly
implements the behavior already confirmed against the real editor via the project's
existing screenshot/attachment-driven research process — it does not, and cannot,
prove the rule matches a live real file end-to-end, since no real file is part of
the automated test suite. That confirmation remains the same manual process used for
the rest of rifflet-civ3's field discoveries.

## Non-goals for v1

- No registry or auto-discovery mechanism for rules (deferred until the hand-written
  rule list is unwieldy).
- No rule content for categories B (era-conditioning), C (bit/enum validity), or the
  cross-entry/cross-section/file-structural category beyond what's described
  illustratively above.
- No `WARNING`-severity rules yet (the enum value exists for forward-compatibility
  with era-conditioning, but v1 populates no rule that emits it).
- No dependency on real `.biq`/`.bix`/`.bic` files anywhere in the build or test
  suite.
