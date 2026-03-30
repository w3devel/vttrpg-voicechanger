package com.vttrpg.voicechanger.relay.service;

import com.vttrpg.voicechanger.relay.model.LinkResponse;
import com.vttrpg.voicechanger.relay.model.Slot;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class SlotService {

    private final ConcurrentHashMap<String, Slot> slots = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> activeByLabel = new ConcurrentHashMap<>();

    public LinkResponse createLink(String label, String baseUrl) {
        // Revoke any existing active slot for this label
        String existingSlotId = activeByLabel.get(label);
        if (existingSlotId != null) {
            revokeSlot(existingSlotId);
        }

        String slotId = UUID.randomUUID().toString();
        String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        String shortSuffix = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        String identityKey = label + "#" + shortSuffix;

        Slot slot = new Slot(slotId, label, identityKey, token, Instant.now(), true);
        slots.put(slotId, slot);
        activeByLabel.put(label, slotId);

        String joinUrl = baseUrl + "/join?token=" + token;
        return new LinkResponse(slotId, label, identityKey, joinUrl);
    }

    public void revokeByLabel(String label) {
        String slotId = activeByLabel.get(label);
        if (slotId != null) {
            revokeSlot(slotId);
        }
    }

    public Optional<Slot> claimSlot(String token) {
        Optional<Slot> found = findSlotByToken(token);
        found.ifPresent(slot -> {
            if (slot.isActive() && slot.getClaimedAt() == null) {
                slot.setClaimedAt(Instant.now());
            }
        });
        return found.filter(Slot::isActive);
    }

    public Optional<Slot> findSlotByToken(String token) {
        return slots.values().stream()
                .filter(s -> s.getToken().equals(token))
                .findFirst();
    }

    public void revokeSlot(String slotId) {
        Slot slot = slots.get(slotId);
        if (slot != null) {
            slot.setActive(false);
            activeByLabel.remove(slot.getLabel(), slotId);
        }
    }

    public List<Slot> getActiveSlots() {
        return slots.values().stream()
                .filter(Slot::isActive)
                .collect(Collectors.toList());
    }

    public Optional<Slot> getActiveSlotByLabel(String label) {
        String slotId = activeByLabel.get(label);
        if (slotId == null) return Optional.empty();
        Slot slot = slots.get(slotId);
        if (slot == null || !slot.isActive()) return Optional.empty();
        return Optional.of(slot);
    }
}
