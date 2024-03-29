package org.bankmanagement.controller;

import lombok.SneakyThrows;
import org.bankmanagement.dataobject.SlotDto;
import org.bankmanagement.dataobject.TransferDto;
import org.bankmanagement.service.SlotService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.bankmanagement.configuration.ResponseResultMatcher.responseBody;
import static org.bankmanagement.controller.AbstractControllerIT.USERNAME;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(SlotController.class)
@WithMockUser(username = USERNAME)
class SlotControllerIT extends AbstractControllerIT {

    private static final String URI = "/slot";
    private static final String TRANSFER_URI = String.join("/", URI, "transfer");

    @MockBean
    private SlotService slotService;
    @Captor
    private ArgumentCaptor<TransferDto> transferCaptor;

    private static SlotDto getSlotDto(long id) {
        SlotDto slotDto = new SlotDto();
        slotDto.setId(id);
        slotDto.setActive(false);
        slotDto.setState(300);
        return slotDto;
    }

    @Test
    @SneakyThrows
    void getAllSlots() {
        SlotDto firstSlotDto = getSlotDto(1L);
        SlotDto secondSlotDto = getSlotDto(2L);
        List<SlotDto> slotList = new ArrayList<>();
        slotList.add(firstSlotDto);
        slotList.add(secondSlotDto);

        when(slotService.getAllSlots(USERNAME)).thenReturn(slotList);

        mockMvc.perform(MockMvcRequestBuilders.get(URI))
                .andExpect(status().isOk())
                .andExpect(responseBody().containsList(slotList, SlotDto.class));

        verify(slotService).getAllSlots(USERNAME);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void getAllSlots_WithAnonymousUser() {
        mockMvc.perform(get(URI))
                .andExpect(status().isForbidden());
        verifyNoInteractions(slotService);
    }

    @Test
    @SneakyThrows
    void getSlot() {
        Long slotId = 25L;
        SlotDto slotDto = getSlotDto(slotId);

        String getSlotUri = String.join("/", URI, String.valueOf(slotId));
        when(slotService.getSlot(slotId, USERNAME)).thenReturn(slotDto);

        mockMvc.perform(MockMvcRequestBuilders.get(getSlotUri))
                .andExpect(status().isOk())
                .andExpect(responseBody().containsObject(slotDto, SlotDto.class));

        verify(slotService).getSlot(slotId, USERNAME);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void getSlot_WithAnonymousUser() {
        String getSlotUri = String.join("/", URI, "3");
        mockMvc.perform(get(getSlotUri))
                .andExpect(status().isForbidden());
        verifyNoInteractions(slotService);
    }

    @Test
    @SneakyThrows
    void openSlot() {
        SlotDto slotDto = getSlotDto(1L);

        when(slotService.createSlot(USERNAME)).thenReturn(slotDto);

        mockMvc.perform(post(URI))
                .andExpect(status().isOk())
                .andExpect(responseBody().containsObject(slotDto, SlotDto.class));

        verify(slotService).createSlot(USERNAME);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void openSlot_WithAnonymousUser() {
        mockMvc.perform(post(URI))
                .andExpect(status().isForbidden());
        verifyNoInteractions(slotService);
    }

    @Test
    @SneakyThrows
    void updateSlotState_WithNotPositiveTransferSum() {
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("slotId", "23452");
        paramMap.add("deposit", "true");
        paramMap.add("transferSum", "0");

        mockMvc.perform(patch(URI).params(paramMap))
                .andExpect(status().isBadRequest())
                .andExpect(responseBody().messageStartsWith("updateSlotState.transferSum: must be greater than 0"));

        verifyNoInteractions(slotService);
    }

    @Test
    @SneakyThrows
    void updateSlotState_WithNoSlotId() {
        SlotDto slotDto = getSlotDto(200L);

        Long transferSum = 500L;
        when(slotService.updateSlotState(null, true, transferSum, USERNAME)).thenReturn(slotDto);
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("deposit", "true");
        paramMap.add("transferSum", transferSum.toString());

        mockMvc.perform(patch(URI).params(paramMap))
                .andExpect(status().isOk())
                .andExpect(responseBody().containsObject(slotDto, SlotDto.class));

        verify(slotService).updateSlotState(null, true, transferSum, USERNAME);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void updateSlotState_WithAnonymousUser() {
        mockMvc.perform(patch(URI))
                .andExpect(status().isForbidden());
        verifyNoInteractions(slotService);
    }

    @Test
    @SneakyThrows
    void transferToCustomer_WithEemptyTicket() {
        mockMvc.perform(post(TRANSFER_URI))
                .andExpect(status().isBadRequest())
                .andExpect(responseBody().messageStartsWith("Required request body is missing"));

        verifyNoInteractions(slotService);
    }

    @Test
    @SneakyThrows
    void transferToCustomer_WithNotPositiveSum() {
        TransferDto transferDto = new TransferDto();
        transferDto.setDepositClientId(3L);
        transferDto.setTransferSum(0L);

        mockMvc.perform(post(TRANSFER_URI).contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(transferDto)))
                .andExpect(status().isBadRequest())
                .andExpect(responseBody().messageStartsWith("Validation failed"));

        verifyNoInteractions(slotService);
    }

    @Test
    @SneakyThrows
    void transferToCustomer() {
        TransferDto transferDto = new TransferDto();
        transferDto.setDepositClientId(3L);
        transferDto.setTransferSum(15L);

        SlotDto slotDto = getSlotDto(991234L);

        when(slotService.transferToCustomer(any(TransferDto.class), eq(USERNAME))).thenReturn(slotDto);

        mockMvc.perform(post(TRANSFER_URI).contentType(MediaType.APPLICATION_JSON)
                        .content(mapToJson(transferDto)))
                .andExpect(status().isOk())
                .andExpect(responseBody().containsObject(slotDto, SlotDto.class));

        verify(slotService).transferToCustomer(transferCaptor.capture(), eq(USERNAME));

        assertThat(transferCaptor.getValue()).usingRecursiveComparison().isEqualTo(transferDto);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void transferToCustomer_WithAnonymousUser() {
        mockMvc.perform(post(TRANSFER_URI))
                .andExpect(status().isForbidden());
        verifyNoInteractions(slotService);
    }

    @Test
    @SneakyThrows
    void closeSlot() {
        Long slotId = 52L;
        String deleteSlotUri = String.join("/", URI, slotId.toString());

        SlotDto slotDto = getSlotDto(slotId);

        when(slotService.deactivateSlot(slotId, USERNAME)).thenReturn(slotDto);

        mockMvc.perform(delete(deleteSlotUri))
                .andExpect(status().isOk())
                .andExpect(responseBody().containsObject(slotDto, SlotDto.class));

        verify(slotService).deactivateSlot(slotId, USERNAME);
    }

    @Test
    @SneakyThrows
    @WithAnonymousUser
    void closeSlot_WithAnonymousUser() {
        String deleteSlotUri = String.join("/", URI, "3");

        mockMvc.perform(delete(deleteSlotUri))
                .andExpect(status().isForbidden());
        verifyNoInteractions(slotService);
    }
}