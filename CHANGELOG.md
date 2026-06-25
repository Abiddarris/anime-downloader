Changelog
## [unreleased]

### 🚀 Features

- Add --verbose flag and set default log level to WARN
- Print anime title and episode number after successful query
- Show error message when anime is not found

### 🐛 Bug Fixes

- Handle nonce requirement in queryAnime and fetchServer
- Replace boolean server state with IDLE/READY/FAILED enum
- Resolve forbidden error caused by stream session binding failure

### 🚜 Refactor

- Convert Quality from interface to abstract class
- Add logging to DownloadSubcommand
- Use picocli PrintWriter instead of System.out
- Add SLF4J logging to AnimeSource and fix log parameters
- Replace Episode reference in Server with shared Metadata object
- Remove referer parameter from resolveQuality
- Simplify null check to satisfy compiler

### ⚙️ Miscellaneous Tasks

- Add SLF4J and Logback dependencies
- Add Logback configuration
- Add cliff.toml
- Bump project version to 0.2.0
## [0.1.0] - 2026-06-23

### 🚀 Features

- Download single hardcoded episode end-to-end (PoC)
- Accept anilistId, episode, and output name via basic args
- Add quality selection support via argument
- Add server selection via basic args
- Support servers that provide direct URL
- Replace positional quality argument with -Q/--quality option
- Add info subcommand to display episode servers and qualities
- [**breaking**] Migrate download to dedicated subcommand
- Add progress messages to download flow

### 🐛 Bug Fixes

- Correct isReady() hardcoded to true in constructor
- Match quality by exact name instead of partial contains
- Suppress stack trace when server has no available streams

### 🚜 Refactor

- Extract scraping logic into AnimeSource class
- Move AnimeSource and related classes to source package
- Rename models and encapsulate fields
- Move yt-dlp invocation from Main into M3U8Downloader
- Migrate CLI arg parsing to picocli
- Move model construction from AnimeSource to ApiResponseParser
- Use `HttpUrl.Builder` for resolving token in `resolveQuality`
- Standardize resolution format and remove HD/SD prefixes
- Use picocli's out/err streams instead of System.out/err

### 📚 Documentation

- Change command description for info subcommand
- Add --help to download and info subcommands

### ⚙️ Miscellaneous Tasks

- Initial project setup with license and gradle config
- Add OkHttp and logging-interceptor dependencies
- Configure Java 17 bytecode target for compatibility
- Add okhttp-urlconnection as dependency
- Add picocli dependency
- Set project version to 0.1.0 in build.gradle
