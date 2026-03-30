package com.vttrpg.voicechanger.relay.model;

import java.time.Instant;

public class Slot {
    private final String slotId;
    private final String label;
    private final String identityKey;
    private final String token;
    private final Instant createdAt;
    private volatile Instant claimedAt;
    private volatile boolean active;
    // TODO(Option 2): placeholder for future end-to-end encrypted whispers;
    //   not used in v1 token-based routing
    private String encryptedKey;

    public Slot(String slotId, String label, String identityKey, String token,
                Instant createdAt, boolean active) {
        this.slotId = slotId;
        this.label = label;
        this.identityKey = identityKey;
        this.token = token;
        this.createdAt = createdAt;
        this.active = active;
    }

    public String getSlotId() { return slotId; }
    public String getLabel() { return label; }
    public String getIdentityKey() { return identityKey; }
    public String getToken() { return token; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getClaimedAt() { return claimedAt; }
    public boolean isActive() { return active; }
    public String getEncryptedKey() { return encryptedKey; }

    public void setClaimedAt(Instant claimedAt) { this.claimedAt = claimedAt; }
    public void setActive(boolean active) { this.active = active; }
    public void setEncryptedKey(String encryptedKey) { this.encryptedKey = encryptedKey; }
}
