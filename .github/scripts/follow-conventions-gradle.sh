#!/usr/bin/env bash
# Moves the Gradle wrapper to the Gradle version the pinned gradle-build-conventions release is built for (the
# `gradle` version in its published catalog), so a conventions bump and its Gradle version land in one change.
#
# The old Gradle is never run: it may fail to configure against the new conventions (e.g. the Kotlin drift check).
# Needs GITHUB_ACTOR/GITHUB_TOKEN with read:packages. Writes changed=true|false to $GITHUB_OUTPUT when set.
set -euo pipefail

cd "$(git rev-parse --show-toplevel)"
properties=gradle/wrapper/gradle-wrapper.properties

pin=$(sed -nE 's/^gradle-build-conventions *= *"([^"]+)".*/\1/p' gradle/libs.versions.toml)
if [ -z "$pin" ]; then
    echo "No gradle-build-conventions pin in gradle/libs.versions.toml" >&2
    exit 1
fi

catalog_url="https://maven.pkg.github.com/kelvSYC/gradle-build-conventions/com/kelvsyc/internal/catalog/${pin}/catalog-${pin}.toml"
catalog=$(curl -fsSL --retry 3 --retry-all-errors -u "${GITHUB_ACTOR}:${GITHUB_TOKEN}" "$catalog_url")
# Only the [versions] table: a `gradle = "…"` line elsewhere would be some other entry.
target=$(awk -F'"' '/^\[/ { table = $0 } table == "[versions]" && /^gradle *=/ { print $2; exit }' <<< "$catalog")
if [ -z "$target" ]; then
    echo "No gradle version in the gradle-build-conventions ${pin} catalog" >&2
    exit 1
fi

current=$(sed -nE 's#^distributionUrl=.*/gradle-(.+)-(bin|all)\.zip$#\1#p' "$properties")
echo "gradle-build-conventions ${pin} expects Gradle ${target}; the wrapper is at ${current:-<unknown>}"

changed=false
if [ "$current" != "$target" ]; then
    sed -i.bak -E "s#^(distributionUrl=.*/gradle-).+(-(bin|all)\.zip)\$#\1${target}\2#" "$properties"
    rm "${properties}.bak"
    # Now under the new Gradle: refreshes the wrapper scripts and jar.
    ./gradlew --quiet wrapper --gradle-version "$target"
    changed=true
fi

if [ -n "${GITHUB_OUTPUT:-}" ]; then
    echo "changed=${changed}" >> "$GITHUB_OUTPUT"
    echo "gradle=${target}" >> "$GITHUB_OUTPUT"
fi
echo "changed=${changed}"
