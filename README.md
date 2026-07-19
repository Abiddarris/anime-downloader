# anime-downloader (`anime-dl`)

A lightweight command-line tool to search, inspect, and download anime episodes from `tryembed.us.cc` using AniList IDs.

## Features

- **Anime search** — find AniList IDs by name or keyword, no need to look them up manually
- **Episode inspection** — check available servers and qualities before downloading
- **Quality selection** — specify target resolution or let it pick the best available
- **Server selection** — target a specific streaming server by ID
- **AniList integration** — identify anime using standard AniList IDs
- **Custom progress output** — shows download progress without relying on yt-dlp output
- **Dry-run support** — simulate downloads with `-s/--simulate` flag
- **Configurable timeouts** — set connection, read, and write timeouts
- **Enhanced output formatting** — curly bracket escaping and additional format keys

---

## Installation

Distributed as a pre-compiled application bundle via Gradle.

1. Go to the [Releases](../../releases) page
2. Download the archive for your platform:
   - `anime-dl-0.4.1.zip`
   - `anime-dl-0.4.1.tar`
3. Extract the archive:

```bash
# ZIP
unzip anime-dl-0.4.1.zip

# TAR
tar -xvf anime-dl-0.4.1.tar
```

4. Run the binary:

```bash
cd anime-dl-0.4.1/bin
./anime-dl --help
```

Add the `bin` directory to your `PATH` for global access.

---

## Requirements

- Java 17+
- [yt-dlp](https://github.com/yt-dlp/yt-dlp)

---

## Usage

```
Usage: anime-downloader [-hV] [COMMAND]
Download anime from tryembed.us.cc
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.
Commands:
  download  Download an anime episode from tryembed.us.cc
  info      Fetch and display available servers and qualities for an episode
  search    Search for anime on AniList by name or keyword.
```

### search

Find an anime's AniList ID by name or keyword — the usual starting point before `info` or `download`.

```
Usage: anime-downloader search [-hvV] [--connect-timeout=mills] [-p=<page>]
                               [--read-timeout=mills] [--write-timeout=mills]
                               <keyword>
Search for anime on AniList by name or keyword.
      <keyword>              The name or keyword of the anime you want to find.
      --connect-timeout=mills
                             Connection timeout in milliseconds. 0 for no
                               timeout (default: 10000)
  -h, --help                 Show this help message and exit.
  -p, --page=<page>          The page number of search results to display
                               (default: 1).
      --read-timeout=mills   Read timeout in milliseconds. 0 for no timeout
                               (default: 30000)
  -v, --verbose              Enable debug logging
  -V, --version              Print version information and exit.
      --write-timeout=mills  Write timeout in milliseconds. 0 for no timeout
                               (default: 10000)
```

### info

Fetch and display available servers and qualities for an episode without downloading.

```
Usage: anime-downloader info [-hvV] [--connect-timeout=mills]
                             [--read-timeout=mills] [--write-timeout=mills]
                             <animeId> <episodeId>
Fetch and display available servers and qualities for an episode
      <animeId>              AniList anime ID
      <episodeId>            Episode number
      --connect-timeout=mills
                             Connection timeout in milliseconds. 0 for no
                               timeout (default: 10000)
  -h, --help                 Show this help message and exit.
      --read-timeout=mills   Read timeout in milliseconds. 0 for no timeout
                               (default: 30000)
  -v, --verbose              Enable debug logging
  -V, --version              Print version information and exit.
      --write-timeout=mills  Write timeout in milliseconds. 0 for no timeout
                               (default: 10000)
```

### download

Download an episode to local storage.

```
Usage: anime-downloader download [-hSvV] [--connect-timeout=mills] [-o=output]
                                 [-Q=<quality>] [--read-timeout=mills]
                                 [-s=<serverId>] [--write-timeout=mills]
                                 <animeId> <episodeId>
Download an anime episode from tryembed.us.cc
      <animeId>              AniList anime ID
      <episodeId>            Episode number
      --connect-timeout=mills
                             Connection timeout in milliseconds. 0 for no
                               timeout (default: 10000)
  -h, --help                 Show this help message and exit.
  -o, --output=output        Output file name (default: {anime_title} #
                               {episode} [{id}].{ext})
  -Q, --quality=<quality>    Video resolution (e.g. 1080p, 720p, 480p)
      --read-timeout=mills   Read timeout in milliseconds. 0 for no timeout
                               (default: 30000)
  -s, --server=<serverId>    ID of server to download from
  -S, --simulate             Do not download the video
  -v, --verbose              Enable debug logging
  -V, --version              Print version information and exit.
      --write-timeout=mills  Write timeout in milliseconds. 0 for no timeout
                               (default: 10000)
```

#### Output filename formatting

The `-o`/`--output` flag accepts a template string with the following placeholders:

| Placeholder       | Description                          |
|-------------------|--------------------------------------|
| `{anime_title}`   | Title of the anime                   |
| `{episode}`       | Episode number                       |
| `{id}`            | AniList anime ID                     |
| `{ext}`           | File extension (resolved automatically) |
| `{server_id}`     | ID of the server being used          |
| `{server_name}`   | Name of the server being used        |
| `{quality}`       | Selected video quality               |

Curly brackets can be escaped by doubling them: `{{` and `}}`.

If `-o` is omitted, the default template is used:

```
{anime_title} #{episode} [{id}].{ext}
```

which for K-ON!! Season 2 episode 8 resolves to something like:

```
K-ON! Season 2 #8 [7791].mp4
```

---

## Examples

Using **K-ON!! Season 2** as an example, from search to download.

**1. Search for the anime to find its AniList ID:**

```bash
./anime-dl search "K-ON"
```

```
Search Results for: 'K-ON' (Page 1)
[1] K-ON! (TV) [13 eps] (ID: 5680)
[2] K-ON! Season 2 (TV) [26 eps] (ID: 7791)
    K-ON!!
[3] K-ON!: The Movie (MOVIE) [1 eps] (ID: 9617)
    K-ON! Movie
[4] K-ON!: Live House! (OVA) [1 eps] (ID: 6862)
[5] K-ON! Season 1 Shorts (SPECIAL) [7 eps] (ID: 7017)
    K-ON!: Ura-On!
```

K-ON!! Season 2 is `[2]`, AniList ID `7791`.

**2. Check available servers and qualities for an episode:**

```bash
./anime-dl info 7791 8
```

```
No   Server Name   Server Id   Quality
-------------------------------------------
1    Alpha Server  alpha       1080p
2    Alpha Server  alpha       720p
3    Alpha Server  alpha       360p
4    Timi Server   timi        1080p
5    Timi Server   timi        720p
6    Timi Server   timi        360p
7    Zen Server    zen         1080p
8    Zen Server    zen         720p
9    Zen Server    zen         360p
10   Beta Server   beta        Default
```

**3. Download at 1080p:**

```bash
./anime-dl download -Q 1080p 7791 8
```

**Download from a specific server with a custom filename template:**

```bash
./anime-dl download -s timi -o "{anime_title} - EP{episode}.{ext}" 7791 8
```

**Download with verbose logging for debugging:**

```bash
./anime-dl download -Q 1080p -v 7791 8
```

**Simulate a download (dry-run):**

```bash
./anime-dl download -S 7791 8
```

**Download with custom timeouts:**

```bash
./anime-dl download --connect-timeout=5000 --read-timeout=20000 --write-timeout=5000 7791 8
```

**Use advanced output formatting with new placeholders:**

```bash
./anime-dl download -o "{anime_title} [{id}] {quality}p ({server_name}){ext}" 7791 8
```

**Search with pagination:**

```bash
./anime-dl search "K-ON" -p 2
```

---

## Known Limitations

- Hardsubbed video only (subtitles burned into video)
- `search` relies on AniList's naming/keywords — if a title doesn't turn up, try an alternate or English title

---

## Legal

### License
anime-dl is licensed under the [Apache 2.0](LICENSE). See the LICENSE
file for details.

### Disclaimer

anime-dl is a command-line tool that automates retrieval of anime episode
data via the AniList API and downloads video streams through tryembed,
using yt-dlp for the underlying download/muxing pipeline. It does not
host, cache, or redistribute any video content itself.

- **No DRM circumvention.** anime-dl does not decrypt, bypass, or
  circumvent any access control or DRM mechanism. It retrieves streams
  that are already served without such protections by the source it
  queries.
- **Source dependency.** This tool depends entirely on tryembed's public
  availability and structure. It has no relationship with, and is not
  endorsed by, tryembed, AniList, or any anime rightsholder or
  distribution platform. If tryembed's access changes or is discontinued,
  this tool's functionality changes accordingly.
- **User responsibility.** You are solely responsible for ensuring your
  use of this tool complies with the copyright laws of your jurisdiction
  and the terms of service of any site it interacts with.
- **Personal use.** This project is intended for personal, non-commercial
  use and as a software engineering exercise (API integration,
  CLI architecture). It is not intended to facilitate commercial
  redistribution of copyrighted content.

### No warranty
This software is provided "as is", without warranty of any kind, express
or implied. The author assumes no liability for misuse of this tool.