# VTTRPG VoiceChanger

A **Fantasy Grounds**-oriented TTRPG voice/audio helper. v1 focus: **chat → text-to-speech (TTS)** broadcast to all players, with **whispers spoken only to the intended recipient** via per-player join links.

> Not affiliated with or endorsed by SmiteWorks. *Fantasy Grounds* is a trademark of SmiteWorks USA, LLC.

## Components
- `relay/` — **Relay server** (Spring Boot)
  - Receives chat events from the FG extension
  - Issues per-identity join links (`!ttslink <identity>`) and rotates/revokes old links
  - Streams events to browsers via **SSE**
  - (Future) Optional end-to-end encrypted whispers
- `web/` — **Player web client**
  - Opens from join link
  - Uses **Web Speech API** (`speechSynthesis`) for offline-capable TTS
  - Voice selection + per-type filters (IC/OOC/ROLL/SYSTEM)
- `fg-extension/` — **Fantasy Grounds extension** (Lua)
  - Adds chat commands (e.g. `!ttslink`)
  - Forwards chat messages/whispers to the relay

## Current status
Scaffold / MVP-in-progress.

## Quick start (dev)
### Relay
- Build/run from `relay/` (instructions will be added once the scaffold is committed)

### Web client
- Served by the relay in dev mode (or from `web/`)

### Fantasy Grounds extension
- Copy the built extension into your FG extensions directory (instructions TBD)
