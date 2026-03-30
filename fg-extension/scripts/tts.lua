-- VTTRPG VoiceChanger - Fantasy Grounds Extension
-- tts.lua: Chat hook and command registration stub
--
-- Future HTTP callout pattern (libCURL / network.post):
--   Fantasy Grounds Lua does not have a built-in HTTP client in all versions.
--   Option A (Classic/Unity with libCURL extension):
--     local curl = require("libcurl")
--     curl.easy_perform({ url = RELAY_BASE_URL .. "/api/fg/event",
--                         post = true, postfields = jsonBody })
--   Option B (FGU network library, when available):
--     network.post(RELAY_BASE_URL .. "/api/fg/event", headers, jsonBody, callback)
--   This file is a stub until the HTTP callout approach is confirmed for the
--   target FG version.

-- ── Config ────────────────────────────────────────────────────────────────────
local RELAY_BASE_URL = "http://127.0.0.1:37812"

-- ── Module ────────────────────────────────────────────────────────────────────
local Module = {}

function Module.onInit()
    -- Register the !ttslink chat command
    ChatManager.registerSlashCommand("ttslink", Module.handleTtsLink)

    -- Register the chat message hook
    -- Note: hook name may vary by FG version; adjust if needed
    if ChatManager.registerHook then
        ChatManager.registerHook("onChat", Module.onChatMessage)
    else
        Debug.chat("[VoiceChanger] WARNING: ChatManager.registerHook is unavailable in this FG version. Chat events will not be forwarded.")
    end

    Debug.chat("VTTRPG VoiceChanger loaded. Relay: " .. RELAY_BASE_URL)
end

--- handleTtsLink: stub for the !ttslink <identity> command.
-- In a future implementation this will call the relay's /api/admin/link
-- endpoint and display the resulting join URL in the chat window.
-- @param sCmd   string  the command string ("ttslink")
-- @param sParams string  parameters passed after the command
function Module.handleTtsLink(sCmd, sParams)
    -- TODO: implement HTTP callout to RELAY_BASE_URL .. "/api/admin/link"
    --       with body { "label": sParams } and display the joinUrl response.
    local msg = "[VoiceChanger] !ttslink " .. (sParams or "<identity>")
              .. " - TODO: implement HTTP callout to relay"
    ChatManager.addMessage({ text = msg, font = "ChatFont" })
end

--- onChatMessage: hook called when a chat message is sent.
-- In a future implementation this will:
--   1. Inspect msgdata.type (ic, ooc, whisper, system, gmroll, etc.)
--   2. Map FG message type to relay messageType enum
--   3. POST to RELAY_BASE_URL .. "/api/fg/event" with the event JSON
-- @param msgdata table  Fantasy Grounds message data object
function Module.onChatMessage(msgdata)
    -- TODO: map msgdata fields to FgEvent and POST to relay
    -- Example payload:
    -- {
    --   "timestamp": os.date("!%Y-%m-%dT%H:%M:%SZ"),
    --   "speakerLabel": msgdata.sender or "Unknown",
    --   "messageType": mapFgType(msgdata.type),
    --   "text": msgdata.text,
    --   "whisperToLabel": msgdata.whisper or nil
    -- }
end

return Module
