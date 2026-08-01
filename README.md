# HBM's Nuclear Tech Mod — 1-of-90 Edition (Minecraft 1.7.10)

[![CI](https://github.com/erstervonelf/NTM-1-of-90-Edition-GIT/actions/workflows/ci.yml/badge.svg)](https://github.com/erstervonelf/NTM-1-of-90-Edition-GIT/actions/workflows/ci.yml)
[![Release](https://github.com/erstervonelf/NTM-1-of-90-Edition-GIT/actions/workflows/release.yml/badge.svg)](https://github.com/erstervonelf/NTM-1-of-90-Edition-GIT/actions/workflows/release.yml)

This repository is a focused fork of HBM's Nuclear Tech Mod for **Minecraft 1.7.10**.  
It keeps upstream gameplay intact while adding a dedicated `com.oneof90` extension layer for fork-specific content and maintenance.

## What this fork adds (special fork-only focus)

Fork-specific work is centered in `src/main/java/com/oneof90`:

- Independent extension registry (`MainRegistry1of90`, `ModBlocks1of90`, `ModItems1of90`)
- Additional blocks and machines (including steel beam variants, cage ladder variants, diesel generator scaffolding)
- Dedicated tile entities and custom renderers for fork content
- Internal architecture and extension documentation set for maintainers and contributors
- Active fork changelog with gameplay/content changes managed separately from upstream history

## Upstream vs fork scope

- **Upstream scope:** base NTM systems, mechanics, and legacy project structure
- **Fork scope (this repo):** additive 1-of-90 features, docs/process hardening, release/deployment automation, and repository quality improvements

## Documentation index

- Contribution guide: [`CONTRIBUTING.md`](CONTRIBUTING.md)
- Release process: [`RELEASE_PROCESS.md`](RELEASE_PROCESS.md)
- Deployment process: [`DEPLOYMENT.md`](DEPLOYMENT.md)
- Support policy: [`SUPPORT.md`](SUPPORT.md)
- Security policy: [`SECURITY.md`](SECURITY.md)
- 1-of-90 architecture docs: `src/main/java/com/oneof90/*.md`

## Getting builds

- GitHub Releases: use the release assets published by this fork
- Nightly prereleases: scheduled `ci.yml` runs publish nightly release assets automatically
- CI artifacts: available from successful workflow runs for quick validation builds

## Building from source

Requirements:

- JDK 8
- Git

Build:

```bash
./gradlew build
```

Output JARs are placed in `build/libs`.

## Releases and versioning

Version source of truth is `gradle.properties`:

- `mod_version`
- `mod_build_number`
- `ntm_1of90_version`

Release tags should use a stable convention (for example `v<mod_version>-<ntm_1of90_version>`).  
See [`RELEASE_PROCESS.md`](RELEASE_PROCESS.md) for full policy.

## Issue reporting

Use the issue templates in `.github/ISSUE_TEMPLATE/`:

- Bug report
- Feature request
- Compatibility issue

Please include version, environment, repro steps, and logs/crash reports when relevant.

## Compatibility notice

NTM has several compatibility behaviors and known interactions with other 1.7.10 mods and server stacks (for example Thermos/Crucible, shader stacks, and Optifine variants).  
If you report a compatibility issue, include your full mod list and launch logs.

## License

This project remains licensed under **GNU LGPL-3.0-or-later** with associated GPL-3.0 terms distributed in this repository.

- `LICENSE.LESSER` — GNU Lesser General Public License v3
- `LICENSE` — GNU General Public License v3

Fork-specific additions are provided under the same licensing terms unless explicitly stated otherwise in-file.
