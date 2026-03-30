package com.vttrpg.voicechanger.relay.controller;

import com.vttrpg.voicechanger.relay.model.LinkRequest;
import com.vttrpg.voicechanger.relay.model.LinkResponse;
import com.vttrpg.voicechanger.relay.model.RevokeRequest;
import com.vttrpg.voicechanger.relay.service.SlotService;
import com.vttrpg.voicechanger.relay.service.SseService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final SlotService slotService;
    private final SseService sseService;

    public AdminController(SlotService slotService, SseService sseService) {
        this.slotService = slotService;
        this.sseService = sseService;
    }

    @PostMapping("/link")
    public ResponseEntity<LinkResponse> link(@RequestBody LinkRequest request,
                                              HttpServletRequest httpRequest) {
        String scheme = httpRequest.getScheme();
        String host = httpRequest.getServerName();
        int port = httpRequest.getServerPort();
        String baseUrl = scheme + "://" + host + ":" + port;

        LinkResponse response = slotService.createLink(request.label(), baseUrl);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/revoke")
    public ResponseEntity<Void> revoke(@RequestBody RevokeRequest request) {
        String label = request.label();
        // Notify SSE client before revoking
        slotService.getActiveSlotByLabel(label).ifPresent(slot ->
                sseService.sendRevoked(slot.getSlotId()));
        slotService.revokeByLabel(label);
        return ResponseEntity.ok().build();
    }
}
