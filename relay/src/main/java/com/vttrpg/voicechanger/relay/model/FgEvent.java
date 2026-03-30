package com.vttrpg.voicechanger.relay.model;

public record FgEvent(
        String timestamp,
        String speakerLabel,
        String messageType,
        String text,
        String whisperToLabel
) {}
