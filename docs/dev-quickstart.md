# Dev Quickstart – VTTRPG VoiceChanger

## Prerequisites

- **Java 17+** (check with `java -version`)
- **Fantasy Grounds** (Unity or Classic) for the FG extension
- A modern browser with Web Speech API support (Chrome/Edge recommended)

---

## 1. Run the Relay Server

```bash
cd relay
./gradlew bootRun
```

The relay starts on port **37812** by default.  
To change the port, edit `relay/src/main/resources/application.properties`.

---

## 2. Create a Player Join Link

Replace `Alice` with the player's in-game name:

```bash
curl -X POST http://localhost:37812/api/admin/link \
  -H "Content-Type: application/json" \
  -d '{"label":"Alice"}'
```

**Example response:**

```json
{
  "slotId": "550e8400-e29b-41d4-a716-446655440000",
  "label": "Alice",
  "identityKey": "Alice#a1b2c3",
  "joinUrl": "http://localhost:37812/join?token=<long-token>"
}
```

Send the `joinUrl` to the player. They open it in their browser to connect.

---

## 3. Open the Join Page (Player)

Navigate to the URL returned above, for example:

```
http://<relay-host-ip>:37812/join?token=<token>
```

The player selects their preferred voice and settings. When connected, any TTS event sent by the GM will be spoken aloud in their browser.

---

## 4. Send a Test TTS Event (GM / Debug)

```bash
curl -X POST http://localhost:37812/api/fg/event \
  -H "Content-Type: application/json" \
  -d '{"timestamp":"2024-01-01T00:00:00Z","speakerLabel":"GM","messageType":"IC","text":"Hello world"}'
```

All connected players will hear "Hello world" spoken aloud.

### Send a Whisper

```bash
curl -X POST http://localhost:37812/api/fg/event \
  -H "Content-Type: application/json" \
  -d '{"timestamp":"2024-01-01T00:00:00Z","speakerLabel":"GM","messageType":"WHISPER","text":"Secret message","whisperToLabel":"Alice"}'
```

Only Alice's browser tab will speak the whisper.

---

## 5. Revoke a Player Link

```bash
curl -X POST http://localhost:37812/api/admin/revoke \
  -H "Content-Type: application/json" \
  -d '{"label":"Alice"}'
```

Alice's browser will show a "link expired" message and stop receiving events.

---

## 6. Run Relay Tests

```bash
cd relay
./gradlew test
```

Test reports are in `relay/build/reports/tests/test/index.html`.
