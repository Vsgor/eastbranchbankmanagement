package org.bankmanagement.service;

import org.bankmanagement.dataobject.SlotDto;
import org.bankmanagement.dataobject.TransferDto;
import org.bankmanagement.entity.Client;
import org.bankmanagement.entity.Slot;
import org.bankmanagement.exception.SlotIsDisabledException;
import org.bankmanagement.exception.SlotNotFoundException;
import org.bankmanagement.exception.WithdrawException;
import org.bankmanagement.mapper.SlotMapper;
import org.bankmanagement.repository.SlotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlotServiceTest {

    @Mock
    private ClientService clientService;
    @Mock
    private SlotRepository slotRepository;
    @Mock
    private SlotMapper slotMapper;

    @InjectMocks
    private SlotService slotService;

    @Captor
    private ArgumentCaptor<Slot> slotCaptor;

    private static Client getDefaultCLient(String username) {
        Client client = new Client();
        client.setUsername(username);
        return client;
    }

    @Test
    void testGetAllSlots() {
        String username = "mock username";

        Client client = new Client();
        Slot firstSlot = getDefaultSlot(client, 1L);
        Slot secondSlot = getDefaultSlot(client, 2L);

        List<Slot> slotList = List.of(firstSlot, secondSlot);
        List<SlotDto> expected = slotList.stream().map(this::mapToDto).collect(Collectors.toList());

        client.setUsername(username);
        client.setSlots(slotList);

        when(clientService.findClient(username)).thenReturn(client);
        when(slotMapper.mapToDto(firstSlot)).thenReturn(mapToDto(firstSlot));
        when(slotMapper.mapToDto(secondSlot)).thenReturn(mapToDto(secondSlot));

        List<SlotDto> result = slotService.getAllSlots(username);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);

        verify(clientService).findClient(username);
        verify(slotMapper).mapToDto(firstSlot);
        verify(slotMapper).mapToDto(secondSlot);
        verifyNoMoreInteractions(slotMapper);

        verifyNoMoreInteractions(slotRepository);
    }

    @Test
    void testGetSlot_WithInactiveSlot_ShouldThrow_SlotIsDisabledException() {
        Long slotId = 3L;
        String username = "mockUsername";

        Client client = new Client();
        client.setUsername(username);

        Slot firstSlot = getDefaultSlot(client, 5L);
        Slot secondSlot = getDefaultSlot(client, slotId);
        secondSlot.setActive(false);

        List<Slot> slotList = List.of(firstSlot, secondSlot);
        client.setSlots(slotList);

        when(clientService.findClient(username)).thenReturn(client);

        assertThatThrownBy(() -> slotService.getSlot(slotId, username))
                .isExactlyInstanceOf(SlotIsDisabledException.class);

        verify(clientService).findClient(username);

        verifyNoInteractions(slotMapper);
        verifyNoInteractions(slotRepository);
    }

    @Test
    void testGetSlot_WithInactiveSlot_ShouldThrow_SlotNotFoundException() {
        Long slotId = 3L;
        String username = "mockUsername";

        Client client = new Client();
        client.setUsername(username);

        Slot firstSlot = getDefaultSlot(client, 5L);
        Slot secondSlot = getDefaultSlot(client, 2L);

        List<Slot> slotList = List.of(firstSlot, secondSlot);
        client.setSlots(slotList);

        when(clientService.findClient(username)).thenReturn(client);

        assertThatThrownBy(() -> slotService.getSlot(slotId, username))
                .isExactlyInstanceOf(SlotNotFoundException.class);

        verify(clientService).findClient(username);

        verifyNoInteractions(slotMapper);
        verifyNoInteractions(slotRepository);
    }

    @Test
    void testGetSlot() {
        Long slotId = 3L;
        String username = "mockUsername";

        Client client = new Client();
        client.setUsername(username);

        Slot firstSlot = getDefaultSlot(client, 5L);
        Slot targetSlot = getDefaultSlot(client, slotId);
        List<Slot> slotList = List.of(firstSlot, targetSlot);

        client.setSlots(slotList);
        SlotDto expected = mapToDto(targetSlot);

        when(clientService.findClient(username)).thenReturn(client);
        when(slotMapper.mapToDto(targetSlot)).thenReturn(expected);

        SlotDto result = slotService.getSlot(slotId, username);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);

        verify(clientService).findClient(username);
        verify(slotMapper).mapToDto(targetSlot);

        verifyNoInteractions(slotRepository);
    }

    @Test
    void testCreateSlot() {
        String username = "Some user";

        Client client = getDefaultCLient(username);

        Slot slot = getDefaultSlot(client, null);
        SlotDto slotDto = mapToDto(slot);

        when(clientService.findClient(username)).thenReturn(client);
        when(slotMapper.mapToDto(any(Slot.class))).thenReturn(slotDto);

        SlotDto result = slotService.createSlot(username);

        assertThat(result).usingRecursiveComparison().isEqualTo(slotDto);

        verify(clientService).findClient(username);

        verify(slotRepository).save(slotCaptor.capture());
        verify(slotMapper).mapToDto(slotCaptor.capture());

        assertThat(slotCaptor.getAllValues())
                .usingRecursiveFieldByFieldElementComparator()
                .containsOnly(slot);
    }

    @Test
    void testUpdateSlotState_WithDeposit() {
        Long slotId = 15L;
        String username = "mock username";

        Client client = getDefaultCLient(username);
        Slot slot = getDefaultSlot(client, slotId);
        client.setSlots(List.of(slot));
        SlotDto expected = mapToDto(slot);

        when(clientService.findClient(username)).thenReturn(client);
        when(slotMapper.mapToDto(slot)).thenReturn(expected);

        SlotDto result = slotService.updateSlotState(slotId, true, 300L, username);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);

        verify(clientService).findClient(username);
        verify(slotMapper).mapToDto(slot);

        verifyNoInteractions(slotRepository);
    }

    @Test
    void testUpdateSlotState_WithWithdraw_ShouldThrow_WithdrawException() {
        Long slotId = 15L;
        String username = "mock username";

        Client client = getDefaultCLient(username);
        Slot slot = getDefaultSlot(client, slotId);
        slot.setState(250);
        client.setSlots(List.of(slot));

        when(clientService.findClient(username)).thenReturn(client);

        assertThatThrownBy(() -> slotService.updateSlotState(slotId, false, 300L, username))
                .isExactlyInstanceOf(WithdrawException.class);

        verify(clientService).findClient(username);

        verifyNoInteractions(slotMapper);
        verifyNoInteractions(slotRepository);
    }

    @Test
    void testUpdateSlotState_WithWithdraw() {
        Long slotId = 15L;
        String username = "mock username";

        Client client = getDefaultCLient(username);
        Slot slot = getDefaultSlot(client, slotId);
        slot.setState(250);
        client.setSlots(List.of(slot));
        SlotDto expected = mapToDto(slot);

        when(clientService.findClient(username)).thenReturn(client);
        when(slotMapper.mapToDto(slot)).thenReturn(expected);

        SlotDto result = slotService.updateSlotState(slotId, false, 250L, username);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);

        verify(clientService).findClient(username);
        verify(slotMapper).mapToDto(slot);

        verifyNoInteractions(slotRepository);
    }

    @Test
    void testTransferToCustomer_ShouldThrow_WithdrawException() {
        String withdrawClientUsername = "mock username";
        Long depositClintId = 527L;

        Client withdrawClient = getDefaultCLient(withdrawClientUsername);
        Slot withdrawSlot = getDefaultSlot(withdrawClient, null);
        withdrawSlot.setState(400L);
        withdrawClient.setSlots(singletonList(withdrawSlot));

        Client depositClient = getDefaultCLient(null);
        depositClient.setId(depositClintId);
        Slot depositSlot = getDefaultSlot(depositClient, null);
        depositClient.setSlots(singletonList(depositSlot));

        TransferDto transferDto = new TransferDto();
        transferDto.setTransferSum(401L);
        transferDto.setDepositClientId(depositClintId);

        when(clientService.findClient(withdrawClientUsername)).thenReturn(withdrawClient);
        when(clientService.findClient(depositClintId)).thenReturn(depositClient);

        assertThatThrownBy(() -> slotService.transferToCustomer(transferDto, withdrawClientUsername))
                .isExactlyInstanceOf(WithdrawException.class);

        verify(clientService).findClient(withdrawClientUsername);
        verify(clientService).findClient(depositClintId);

        verifyNoMoreInteractions(clientService);

        verifyNoInteractions(slotMapper);
        verifyNoInteractions(slotRepository);
    }

    @Test
    void testTransferToCustomer() {
        String withdrawClientUsername = "mock username";
        Long depositClintId = 527L;

        Client withdrawClient = getDefaultCLient(withdrawClientUsername);
        Slot withdrawSlot = getDefaultSlot(withdrawClient, null);
        withdrawSlot.setState(400L);
        withdrawClient.setSlots(singletonList(withdrawSlot));

        Client depositClient = getDefaultCLient(null);
        depositClient.setId(depositClintId);
        Slot depositSlot = getDefaultSlot(depositClient, null);
        depositClient.setSlots(singletonList(depositSlot));

        TransferDto transferDto = new TransferDto();
        transferDto.setTransferSum(400L);
        transferDto.setDepositClientId(depositClintId);

        SlotDto expected = mapToDto(withdrawSlot);

        when(clientService.findClient(withdrawClientUsername)).thenReturn(withdrawClient);
        when(clientService.findClient(depositClintId)).thenReturn(depositClient);
        when(slotMapper.mapToDto(withdrawSlot)).thenReturn(expected);

        SlotDto result = slotService.transferToCustomer(transferDto, withdrawClientUsername);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);

        verify(clientService).findClient(withdrawClientUsername);
        verify(clientService).findClient(depositClintId);
        verify(slotMapper).mapToDto(withdrawSlot);

        verifyNoMoreInteractions(clientService);
        verifyNoInteractions(slotRepository);
    }

    @Test
    void testDeactivateSlot_ShouldThrow_SlotIsDisabledException() {
        String username = "mock username";
        long slotId = 4L;

        Client client = getDefaultCLient(username);
        Slot slot = getDefaultSlot(client, slotId);
        slot.setActive(false);
        client.setSlots(singletonList(slot));

        when(clientService.findClient(username)).thenReturn(client);

        assertThatThrownBy(() -> slotService.deactivateSlot(slotId, username))
                .isExactlyInstanceOf(SlotIsDisabledException.class);

        verify(clientService).findClient(username);

        verifyNoInteractions(slotMapper);
        verifyNoInteractions(slotRepository);
    }

    @Test
    void testDeactivateSlot() {
        String username = "mock username";
        long slotId = 4L;

        Client client = getDefaultCLient(username);
        Slot slot = getDefaultSlot(client, slotId);
        client.setSlots(singletonList(slot));

        SlotDto expected = mapToDto(slot);
        expected.setActive(false);

        when(clientService.findClient(username)).thenReturn(client);
        when(slotMapper.mapToDto(slot)).thenReturn(expected);

        SlotDto result = slotService.deactivateSlot(slotId, username);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);

        verify(clientService).findClient(username);
        verify(slotMapper).mapToDto(slot);

        verifyNoInteractions(slotRepository);
    }

    private SlotDto mapToDto(Slot savedSlot) {
        SlotDto dto = new SlotDto();
        dto.setId(savedSlot.getId());
        dto.setState(savedSlot.getState());
        dto.setActive(savedSlot.isActive());
        return dto;
    }

    private static Slot getDefaultSlot(Client client, Long id) {
        Slot slot = new Slot();
        slot.setId(id);
        slot.setActive(true);
        slot.setClient(client);
        return slot;
    }
}