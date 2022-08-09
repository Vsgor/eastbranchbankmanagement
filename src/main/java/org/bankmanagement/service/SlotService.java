package org.bankmanagement.service;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.dataobject.SlotDto;
import org.bankmanagement.entity.Client;
import org.bankmanagement.entity.Slot;
import org.bankmanagement.mapper.SlotMapper;
import org.bankmanagement.repository.SlotRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final ClientService clientService;
    private final SlotRepository slotRepository;
    private final SlotMapper slotMapper;

    public SlotDto createSlot(String username) {
        Client client = clientService.findClient(username);

        Slot slot = new Slot();
        slot.setClient(client);
        slot.setActive(true);
        slot.setState(0);

        Slot savedSlot = slotRepository.save(slot);

        return slotMapper.mapToDto(savedSlot);
    }
}
