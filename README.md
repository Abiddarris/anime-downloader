# 🚀 anime-downloader (`anime-dl`)

A lightweight command-line utility to fetch, and download anime episodes from `tryembed.us.cc` using AniList IDs. 🍿

## ✨ Features

* **🔍 Episode Inspection:** Check available streaming servers and video qualities before downloading.
* **⚙️ Customizable Downloads:** Specify target resolutions, specific server configurations, and custom output filenames.
* **📅 AniList Integration:** Native indexing utilizing standard AniList anime and episode IDs.

---

## 📦 Installation

The application is distributed as a pre-compiled application bundle via Gradle. 🛠️

1. Head over to the **Releases** page. 📂
2. Download the preferred archive format for your environment:
* `anime-dl-0.x.x.zip` 📦
* `anime-dl-0.x.x.tar` 📦


3. Extract the archive to your desired location:
```bash
# For TAR archive
tar -xvf anime-dl-0.x.x.tar

# For ZIP archive
unzip anime-dl-0.x.x.zip

```

4. Navigate to the extracted directory and run the binary:
```bash
 cd anime-dl-0.1.0/bin
 ./anime-dl --help

```

*(Optional)* 💡 Add the `bin` directory to your system's `PATH` variable for global access.

---

## ⚡ Usage

### 🌐 Global Options

```bash
Usage: anime-downloader [-hV] [COMMAND]

Options:
  -h, --help     Show this help message and exit.
  -V, --version  Print version information and exit.

```

### 1. View Episode Details (`info`) 📋

Fetch and display available host servers and video streams for a specific episode.

```bash
./anime-dl info <animeId> <episodeId>

```

* `<animeId>`: The AniList anime ID.
* `<episodeId>`: The episode number.

### 2. Download an Episode (`download`) 📥

Stream and save an episode directly to your local storage.

```bash
./anime-dl download [-o=<output>] [-Q=<quality>] [-s=<serverId>] <animeId> <episodeId>

```

#### Options:

| Flag | Long Flag | Description | Example |
| --- | --- | --- | --- |
| `-o` | `--output` | Custom output file name 💾 | `-o="K-On_S2_Ep08.mp4"` |
| `-Q` | `--quality` | Video resolution selection 🎬 | `-Q=1080p` (or `720p`, `360p`) |
| `-s` | `--server` | ID of the specific server to source 🖥️ | `-s=alpha` |

---

## 📝 Examples

Using **K-ON!! Season 2 (AniList ID: 7791) Episode 8** as an example: 🎸

**Check available servers and resolutions:**

```bash
❯ ./anime-dl info 7791 8
No   Server Name   Server Id    Quality
---------------------------------------------
1    Alpha Server  alpha         1080p
2    Alpha Server  alpha         720p
3    Alpha Server  alpha         360p
4    Timi Server   timi          1080p
5    Timi Server   timi          720p
6    Timi Server   timi          360p
7    Zen Server    zen           1080p
8    Zen Server    zen           720p
9    Zen Server    zen           360p
10   Beta Server   beta          Default

```

**Download the episode in 1080p:**

```bash
./anime-dl download -Q 1080p 7791 8

```

**Download from a specific server (e.g., Timi Server) with a custom file name:**

```bash
./anime-dl download -s timi -o "K-ON_S2_EP08.mp4" 7791 8

```
