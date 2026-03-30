# VTTRPG VoiceChanger – Fantasy Grounds Extension

## What This Extension Does

This extension hooks into Fantasy Grounds chat and forwards chat messages to the **VTTRPG VoiceChanger relay** server. The relay then broadcasts a text-to-speech (TTS) event to all connected player browser tabs, where the browser's Web Speech API speaks the message aloud.

Whisper messages are routed only to the intended recipient's browser tab.

---

## Current Status

> **Stub / Work in Progress** – HTTP callouts are not yet implemented.

The extension registers the `!ttslink` chat command and a chat message hook, but the actual HTTP POST to the relay server is a `TODO`. The HTTP callout approach depends on the Fantasy Grounds version and available Lua networking libraries (libCURL extension or the FGU `network` API).

---

## Installation

1. Copy the `vttrpg-voicechanger.ext` file and the `scripts/` folder to your Fantasy Grounds extensions directory:
   - **Windows:** `%AppData%\SmiteWorks\Fantasy Grounds\extensions\vttrpg-voicechanger\`
   - Or zip the contents and rename the archive to `vttrpg-voicechanger.ext` (Fantasy Grounds accepts `.ext` files as zip archives)
2. Launch Fantasy Grounds and enable the **VTTRPG VoiceChanger** extension in the Extension Manager.

---

## Available Commands

| Command | Status | Description |
|---------|--------|-------------|
| `!ttslink <label>` | Planned | Generates a join URL for the named player |
| `!ttsrevoke <label>` | Planned | Revokes the active join link for the named player |
| `!ttstest` | Planned | Sends a test TTS message to all connected players |

---

## Prerequisites

- The **VTTRPG VoiceChanger relay** server must be running on the GM's machine.
- Default relay URL: `http://127.0.0.1:37812`
- See [docs/dev-quickstart.md](../docs/dev-quickstart.md) for instructions on running the relay.
