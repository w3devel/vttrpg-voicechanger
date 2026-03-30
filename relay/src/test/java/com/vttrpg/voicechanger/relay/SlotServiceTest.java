package com.vttrpg.voicechanger.relay;

import com.vttrpg.voicechanger.relay.model.LinkResponse;
import com.vttrpg.voicechanger.relay.model.Slot;
import com.vttrpg.voicechanger.relay.service.SlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SlotServiceTest {

    private SlotService slotService;

    @BeforeEach
    void setUp() {
        slotService = new SlotService();
    }

    @Test
    void createLink_createsActiveSlot() {
        LinkResponse response = slotService.createLink("Alice", "http://localhost:37812");

        assertThat(response.label()).isEqualTo("Alice");
        assertThat(response.slotId()).isNotNull();
        assertThat(response.identityKey()).startsWith("Alice#");
        assertThat(response.joinUrl()).contains("/join?token=");

        Optional<Slot> slot = slotService.findSlotByToken(
                response.joinUrl().split("token=")[1]);
        assertThat(slot).isPresent();
        assertThat(slot.get().isActive()).isTrue();
    }

    @Test
    void createLink_sameLabelRevokesOldSlot() {
        LinkResponse first = slotService.createLink("Bob", "http://localhost:37812");
        String firstToken = first.joinUrl().split("token=")[1];

        LinkResponse second = slotService.createLink("Bob", "http://localhost:37812");
        String secondToken = second.joinUrl().split("token=")[1];

        Optional<Slot> oldSlot = slotService.findSlotByToken(firstToken);
        Optional<Slot> newSlot = slotService.findSlotByToken(secondToken);

        assertThat(oldSlot).isPresent();
        assertThat(oldSlot.get().isActive()).isFalse();
        assertThat(newSlot).isPresent();
        assertThat(newSlot.get().isActive()).isTrue();
    }

    @Test
    void revokeByLabel_marksSlotInactive() {
        LinkResponse response = slotService.createLink("Carol", "http://localhost:37812");
        String token = response.joinUrl().split("token=")[1];

        slotService.revokeByLabel("Carol");

        Optional<Slot> slot = slotService.findSlotByToken(token);
        assertThat(slot).isPresent();
        assertThat(slot.get().isActive()).isFalse();
    }

    @Test
    void claimSlot_firstTime_setsClaimedAt() {
        LinkResponse response = slotService.createLink("Dave", "http://localhost:37812");
        String token = response.joinUrl().split("token=")[1];

        Optional<Slot> claimed = slotService.claimSlot(token);

        assertThat(claimed).isPresent();
        assertThat(claimed.get().getClaimedAt()).isNotNull();
    }

    @Test
    void claimSlot_secondTime_returnsSameSlotWithoutError() {
        LinkResponse response = slotService.createLink("Eve", "http://localhost:37812");
        String token = response.joinUrl().split("token=")[1];

        Optional<Slot> first = slotService.claimSlot(token);
        java.time.Instant claimedAtAfterFirst = first.get().getClaimedAt();

        Optional<Slot> second = slotService.claimSlot(token);

        assertThat(first).isPresent();
        assertThat(second).isPresent();
        assertThat(first.get().getSlotId()).isEqualTo(second.get().getSlotId());
        // claimedAt must not change on reconnect; compare timestamp captured before second claim
        assertThat(claimedAtAfterFirst).isEqualTo(second.get().getClaimedAt());
    }
}
