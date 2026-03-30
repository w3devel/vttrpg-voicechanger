package com.vttrpg.voicechanger.relay.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    private static final Logger log = LoggerFactory.getLogger(SseService.class);

    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void addEmitter(String slotId, SseEmitter emitter) {
        emitters.put(slotId, emitter);
    }

    public void removeEmitter(String slotId) {
        emitters.remove(slotId);
    }

    public void broadcastTts(String text, String speakerLabel, String messageType) {
        String payload = buildTtsPayload(text, speakerLabel, messageType);
        emitters.forEach((slotId, emitter) -> sendEvent(slotId, emitter, "tts", payload));
    }

    public void sendWhisper(String slotId, String text, String speakerLabel) {
        SseEmitter emitter = emitters.get(slotId);
        if (emitter != null) {
            String payload = buildTtsPayload(text, speakerLabel, "WHISPER");
            sendEvent(slotId, emitter, "tts", payload);
        }
    }

    public void sendRevoked(String slotId) {
        SseEmitter emitter = emitters.get(slotId);
        if (emitter != null) {
            sendEvent(slotId, emitter, "revoked", "{\"reason\":\"link revoked\"}");
            emitter.complete();
            removeEmitter(slotId);
        }
    }

    private String buildTtsPayload(String text, String speakerLabel, String messageType) {
        try {
            return objectMapper.writeValueAsString(Map.of(
                    "text", text != null ? text : "",
                    "speakerLabel", speakerLabel != null ? speakerLabel : "",
                    "messageType", messageType != null ? messageType : ""));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize TTS payload", e);
            return "{}";
        }
    }

    private void sendEvent(String slotId, SseEmitter emitter, String eventName, String data) {
        try {
            emitter.send(SseEmitter.event().name(eventName).data(data));
        } catch (IOException e) {
            log.warn("Failed to send SSE event to slot {}: {}", slotId, e.getMessage());
            removeEmitter(slotId);
        }
    }
}
