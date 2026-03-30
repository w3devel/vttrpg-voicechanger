package com.vttrpg.voicechanger.relay;

import com.vttrpg.voicechanger.relay.model.LinkResponse;
import com.vttrpg.voicechanger.relay.service.SlotService;
import com.vttrpg.voicechanger.relay.service.SseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FgEventRoutingTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SlotService slotService;

    @MockBean
    private SseService sseService;

    @Test
    void icEvent_broadcastsToAll() throws Exception {
        mockMvc.perform(post("/api/fg/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"timestamp":"2024-01-01T00:00:00Z","speakerLabel":"GM",
                                 "messageType":"IC","text":"Hello world","whisperToLabel":null}
                                """))
                .andExpect(status().isOk());

        verify(sseService, times(1)).broadcastTts(eq("Hello world"), eq("GM"), eq("IC"));
        verify(sseService, never()).sendWhisper(anyString(), anyString(), anyString());
    }

    @Test
    void whisperEvent_sendsOnlyToTargetSlot() throws Exception {
        LinkResponse link = slotService.createLink("Alice", "http://localhost:37812");

        mockMvc.perform(post("/api/fg/event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"timestamp":"2024-01-01T00:00:00Z","speakerLabel":"GM",
                                 "messageType":"WHISPER","text":"Secret","whisperToLabel":"Alice"}
                                """))
                .andExpect(status().isOk());

        verify(sseService, times(1)).sendWhisper(
                eq(link.slotId()), eq("Secret"), eq("GM"));
        verify(sseService, never()).broadcastTts(anyString(), anyString(), anyString());
    }
}
