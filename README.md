# anime-downloader (`anime-dl`)

A lightweight command-line tool to download anime episodes from `tryembed.us.cc` using AniList IDs.

## Features

- **Episode inspection** — check available servers and qualities before downloading
- **Quality selection** — specify target resolution or let it pick the best available
- **Server selection** — target a specific streaming server by ID
- **AniList integration** — identify anime using standard AniList IDs

---

## Installation

Distributed as a pre-compiled application bundle via Gradle.

1. Go to the [Releases](../../releases) page
2. Download the archive for your platform:
   - `anime-dl-0.x.x.zip`
   - `anime-dl-0.x.x.tar`
3. Extract the archive:
```bash
# ZIP
unzip anime-dl-0.x.x.zip

# TAR
tar -xvf anime-dl-0.x.x.tar
```
4. Run the binary:
```bash
cd anime-dl-0.x.x/bin
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
  -h, --help      Show this help message and exit.
  -V, --version   Print version information and exit.

Commands:
  download  Download an anime episode from tryembed.us.cc
  info      Fetch and display available servers and qualities for an episode
```

### info

Fetch and display available servers and qualities for an episode without downloading.

```
Usage: anime-downloader info [-hvV] <animeId> <episodeId>

      <animeId>     AniList anime ID
      <episodeId>   Episode number
  -h, --help        Show this help message and exit.
  -v, --verbose     Enable debug logging
  -V, --version     Print version information and exit.
```

### download

Download an episode to local storage.

```
Usage: anime-downloader download [-hvV] [-o=<output>] [-Q=<quality>]
                                 [-s=<serverId>] <animeId> <episodeId>

      <animeId>             AniList anime ID
      <episodeId>           Episode number
  -h, --help                Show this help message and exit.
  -o, --output=<output>     Output file name
  -Q, --quality=<quality>   Video resolution (e.g. 1080p, 720p, 480p)
  -s, --server=<serverId>   Server ID to download from
  -v, --verbose             Enable debug logging
  -V, --version             Print version information and exit.
```

---

## Examples

Using **K-ON!! Season 2 (AniList ID: 7791) Episode 8** as an example.

**Check available servers and qualities:**
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

**Download at 1080p:**
```bash
./anime-dl download -Q 1080p 7791 8
```

**Download from a specific server with a custom filename:**
```bash
./anime-dl download -s timi -o "K-ON_S2_EP08.mp4" 7791 8
```

**Download with verbose logging for debugging:**
```bash
./anime-dl download -Q 1080p -v 7791 8
```

---

## Known Limitations

- Hardsubbed video only (subtitles burned into video)
- Requires AniList ID — find yours at [anilist.co](https://anilist.co)

---

## License

[Apache 2.0](LICENSE)