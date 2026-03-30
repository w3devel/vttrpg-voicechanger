package com.vttrpg.voicechanger.relay.controller;

import com.vttrpg.voicechanger.relay.model.Slot;
import com.vttrpg.voicechanger.relay.service.SlotService;
import com.vttrpg.voicechanger.relay.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

@RestController
public class SseController {

    private final SlotService slotService;
    private final SseService sseService;

    public SseController(SlotService slotService, SseService sseService) {
        this.slotService = slotService;
        this.sseService = sseService;
    }

    @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@RequestParam String token) {
        Optional<Slot> slotOpt = slotService.claimSlot(token);
        if (slotOpt.isEmpty()) {
            return ResponseEntity.status(403).build();
        }

        Slot slot = slotOpt.get();
        SseEmitter emitter = new SseEmitter(0L); // no timeout

        sseService.addEmitter(slot.getSlotId(), emitter);

        emitter.onCompletion(() -> sseService.removeEmitter(slot.getSlotId()));
        emitter.onTimeout(() -> sseService.removeEmitter(slot.getSlotId()));
        emitter.onError(e -> sseService.removeEmitter(slot.getSlotId()));

        return ResponseEntity.ok(emitter);
    }
}
