package com.vttrpg.voicechanger.relay.model;

public record LinkResponse(
        String slotId,
        String label,
        String identityKey,
        String joinUrl
) {}
