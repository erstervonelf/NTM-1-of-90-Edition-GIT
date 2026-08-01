# Deployment Process

## Targets

Primary deployment target is GitHub Releases via `.github/workflows/release.yml`.

Optional external distribution (Modrinth/CurseForge) should be performed only after GitHub release validation and using repository secrets.

## Preconditions

- Release commit merged
- Tag created using release convention
- CI passed for target commit

## GitHub Release deployment

1. Push a release tag (`v*`) or run the release workflow manually with `release_tag`.
2. Workflow builds with Gradle wrapper and JDK 8.
3. Workflow publishes JAR files and license files as release assets.
4. Validate artifact names and checksum locally when needed.

## Optional external deployment

If enabled later:

1. Upload identical JAR artifact to target platform.
2. Reuse same version string and changelog summary.
3. Confirm visibility and dependency metadata.

## Post-deploy verification

- Confirm release appears in repository releases list
- Confirm assets download correctly
- Confirm changelog/release notes reference known changes
