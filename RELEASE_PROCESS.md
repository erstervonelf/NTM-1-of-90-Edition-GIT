# Release Process

## Version source of truth

Version data is maintained in `gradle.properties`:

- `mod_version`
- `mod_build_number`
- `ntm_1of90_version`

## Tag convention

Use a stable tag format:

- `v<mod_version>-<ntm_1of90_version>` for standard releases
- Optional suffixes (e.g. `-rc1`, `-beta1`) for pre-releases

Examples:

- `v1.0.27-r.11-hotfix.3`
- `v1.0.27-r.11-hotfix.3-rc1`

## Release flow

1. Ensure CI is green on the release commit.
2. Prepare/update changelog content.
3. Create and push release tag.
4. `release.yml` builds with JDK 8 and publishes GitHub release assets.
5. Verify JAR artifact and attached license files.

## Nightly flow

- `ci.yml` runs nightly and on manual dispatch.
- Each run uploads CI artifacts and also publishes a nightly prerelease with build JARs and license files.

## Pre-release handling

Pre-release status is inferred from tag text containing `-alpha`, `-beta`, or `-rc`.

## Rollback

If a bad release is published:

1. Mark release as not latest / draft a replacement.
2. Publish corrective tag and release with notes.
3. Document impact and migration guidance in release notes.
