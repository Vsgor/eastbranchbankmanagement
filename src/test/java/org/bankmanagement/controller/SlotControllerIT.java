package org.bankmanagement.controller;

import lombok.SneakyThrows;
import org.bankmanagement.configuration.TestWebConfiguration;
import org.bankmanagement.dataobject.SlotDto;
import org.bankmanagement.service.SlotService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.bankmanagement.configuration.ResponseResultMatcher.responseBody;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SlotController.class)
@Import(TestWebConfiguration.class)
class SlotControllerIT {

    private static final String URI = "/slot";
    private static final String USERNAME = "Some username";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SlotService slotService;

    @Test
    @SneakyThrows
    @WithMockUser(username = USERNAME)
    void openSlot() {
        SlotDto slotDto = getSlotDto();

        when(slotService.createSlot(USERNAME)).thenReturn(slotDto);

        mockMvc.perform(MockMvcRequestBuilders.post(URI))
                .andExpect(status().isOk())
                .andExpect(responseBody().containsObject(slotDto, SlotDto.class));

        verify(slotService).createSlot(USERNAME);
    }

    @NotNull
    private static SlotDto getSlotDto() {
        SlotDto slotDto = new SlotDto();
        slotDto.setId(1L);
        slotDto.setActive(false);
        slotDto.setState(300);
        return new SlotDto();
    }
}