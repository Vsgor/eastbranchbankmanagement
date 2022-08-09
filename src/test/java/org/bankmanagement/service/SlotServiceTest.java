package org.bankmanagement.service;

import org.bankmanagement.dataobject.SlotDto;
import org.bankmanagement.entity.Client;
import org.bankmanagement.entity.Slot;
import org.bankmanagement.mapper.SlotMapper;
import org.bankmanagement.repository.SlotRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    void createSlot() {
        String username = "Some user";

        Client client = new Client();
        client.setUsername(username);

        Slot slot = getDefaultSlot(client, null);
        Slot savedSlot = getDefaultSlot(client, 1L);
        SlotDto slotDto = mapToDto(savedSlot);

        when(clientService.findClient(username)).thenReturn(client);
        when(slotRepository.save(slot)).thenReturn(savedSlot);
        when(slotMapper.mapToDto(savedSlot)).thenReturn(slotDto);

        SlotDto result = slotService.createSlot(username);

        assertThat(result).isEqualTo(slotDto);

        verify(clientService).findClient(username);
        verify(slotRepository).save(slot);
        verify(slotMapper).mapToDto(savedSlot);
    }

    private SlotDto mapToDto(Slot savedSlot) {
        SlotDto dto = new SlotDto();
        dto.setId(savedSlot.getId());
        dto.setState(savedSlot.getState());
        dto.setActive(savedSlot.isActive());
        return dto;
    }

    @NotNull
    private static Slot getDefaultSlot(Client client, Long id) {
        Slot slot = new Slot();
        slot.setId(id);
        slot.setState(0);
        slot.setActive(true);
        slot.setClient(client);
        return slot;
    }
}