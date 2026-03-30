package com.vttrpg.voicechanger.relay;

import com.vttrpg.voicechanger.relay.model.LinkResponse;
import com.vttrpg.voicechanger.relay.service.SlotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SseTokenTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SlotService slotService;

    @Test
    void validToken_returns200() throws Exception {
        LinkResponse link = slotService.createLink("TestUser", "http://localhost:37812");
        String token = link.joinUrl().split("token=")[1];

        mockMvc.perform(get("/api/sse").param("token", token))
                .andExpect(status().isOk());
    }

    @Test
    void invalidToken_returns403() throws Exception {
        mockMvc.perform(get("/api/sse").param("token", "completely-invalid-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    void revokedSlotToken_returns403() throws Exception {
        LinkResponse link = slotService.createLink("RevokedUser", "http://localhost:37812");
        String token = link.joinUrl().split("token=")[1];
        slotService.revokeByLabel("RevokedUser");

        mockMvc.perform(get("/api/sse").param("token", token))
                .andExpect(status().isForbidden());
    }
}
