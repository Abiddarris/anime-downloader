## [0.4.0] - 2026-07-10

### 🚀 Features

- Implement custom progress output instead of relying on yt-dlp output
- Add -s/--simulate flag for dry-run downloads
- Show `--output` default values in `download` subcommand `--help`
- Add curly brackets escaping feature to formatter utility
- Add `server_id`, `quality` and `server_name` as keys for output format strings
- Add configurable network timeout options (--connect-timeout, --read-timeout, --write-timeout)

### 🐛 Bug Fixes

- Do not throw AnimeNotFoundException when animeTitle is null
- Add additional validation checks before throwing AnimeNotFoundException
## [0.3.0] - 2026-07-02

### 🚀 Features

- Add AniList search integration
- Add basic output formatting support

### 🐛 Bug Fixes

- Throw exception when yt-dlp exits with non-zero code
- Print stacktrace only on verbose mode when fetching servers
- Hide unexpected Exception stacktraces by default in info subcommand
- Hide stacktrace on search subcommand unless verbose is enabled
- Hide stacktrace on download subcommand unless verbose is enabled
- Do not discard string after last {} on --output
- Validate curly brace matching and ensure non-empty output blocks
## [0.2.0] - 2026-06-25

### 🚀 Features

- Add --verbose flag and set default log level to WARN
- Print anime title and episode number after successful query
- Show error message when anime is not found

### 🐛 Bug Fixes

- Handle nonce requirement in queryAnime and fetchServer
- Replace boolean server state with IDLE/READY/FAILED enum
- Resolve forbidden error caused by stream session binding failure
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

### 📚 Documentation

- Change command description for info subcommand
- Add --help to download and info subcommands
