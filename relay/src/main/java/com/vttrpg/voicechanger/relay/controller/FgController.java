package com.vttrpg.voicechanger.relay.controller;

import com.vttrpg.voicechanger.relay.model.FgEvent;
import com.vttrpg.voicechanger.relay.service.SlotService;
import com.vttrpg.voicechanger.relay.service.SseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fg")
public class FgController {

    private final SlotService slotService;
    private final SseService sseService;

    public FgController(SlotService slotService, SseService sseService) {
        this.slotService = slotService;
        this.sseService = sseService;
    }

    @PostMapping("/event")
    public ResponseEntity<Void> event(@RequestBody FgEvent fgEvent) {
        if ("WHISPER".equals(fgEvent.messageType())) {
            slotService.getActiveSlotByLabel(fgEvent.whisperToLabel())
                    .ifPresent(slot -> sseService.sendWhisper(
                            slot.getSlotId(), fgEvent.text(), fgEvent.speakerLabel()));
        } else {
            sseService.broadcastTts(fgEvent.text(), fgEvent.speakerLabel(), fgEvent.messageType());
        }
        return ResponseEntity.ok().build();
    }
}
