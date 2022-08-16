package org.bankmanagement.service;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.aspect.log.LogMethod;
import org.bankmanagement.dataobject.SlotDto;
import org.bankmanagement.dataobject.TransferDto;
import org.bankmanagement.entity.Client;
import org.bankmanagement.entity.Slot;
import org.bankmanagement.exception.NoActiveSlotsException;
import org.bankmanagement.exception.SlotIsDisabledException;
import org.bankmanagement.exception.SlotNotFoundException;
import org.bankmanagement.exception.WithdrawException;
import org.bankmanagement.mapper.SlotMapper;
import org.bankmanagement.repository.SlotRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class SlotService {

    private final ClientService clientService;
    private final SlotRepository slotRepository;
    private final SlotMapper slotMapper;

    @LogMethod
    public List<SlotDto> getAllSlots(String username) {
        return clientService.findClient(username).getSlots()
                .stream()
                .filter(Slot::isActive)
                .map(slotMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @LogMethod
    public SlotDto getSlot(Long slotId, String username) {
        return slotMapper.mapToDto(
                findSLot(slotId, clientService.findClient(username)));
    }

    @LogMethod
    @Transactional
    public SlotDto createSlot(String username) {
        Client client = clientService.findClient(username);

        Slot slot = new Slot();
        slot.setClient(client);
        slot.setActive(true);
        slot.setState(0);

        slotRepository.save(slot);
        return slotMapper.mapToDto(slot);
    }

    @LogMethod
    @Transactional
    public SlotDto updateSlotState(Long slotId, Boolean deposit, Long transferSum, String username) {
        Slot operatedSlot = getOperatedSlot(slotId, username);
        long state = operatedSlot.getState();

        state = deposit ? state + transferSum : state - transferSum;
        if (state < 0) throw new WithdrawException(operatedSlot.getId(), operatedSlot.getState(), transferSum);
        operatedSlot.setState(state);

        return slotMapper.mapToDto(operatedSlot);
    }

    @LogMethod
    @Transactional
    public SlotDto transferToCustomer(TransferDto transferDto, String username) {
        Long withdrawSlotId = transferDto.getWithdrawSlotId();
        Long transferSum = transferDto.getTransferSum();
        Client depositClient = clientService.findClient(transferDto.getDepositClientId());

        Slot withdrawSlot = getOperatedSlot(withdrawSlotId, username);
        Slot depositSlot = getMostValuableSlot(depositClient);

        long withdrawSlotState = withdrawSlot.getState();
        if (withdrawSlotState < transferSum) {
            throw new WithdrawException(withdrawSlotId, withdrawSlotState, transferSum);
        }

        withdrawSlot.setState(withdrawSlotState - transferSum);
        depositSlot.setState(depositSlot.getState() + transferSum);

        return slotMapper.mapToDto(withdrawSlot);
    }

    @LogMethod
    @Transactional
    public SlotDto deactivateSlot(Long slotId, String username) {
        Slot slot = findSLot(slotId, clientService.findClient(username));
        slot.setActive(false);
        return slotMapper.mapToDto(slot);
    }

    private Slot getOperatedSlot(Long slotId, String username) {
        Client client = clientService.findClient(username);
        return isNull(slotId) ? getMostValuableSlot(client) : findSLot(slotId, client);
    }

    private Slot getMostValuableSlot(Client client) {
        return client.getSlots()
                .stream()
                .filter(Slot::isActive)
                .max((o1, o2) -> Comparator.comparingLong(Slot::getState).compare(o1, o2))
                .orElseThrow(() -> new NoActiveSlotsException(client.getUsername()));
    }

    private Slot findSLot(Long slotId, Client client) {
        String username = client.getUsername();
        Slot slot = client.getSlots()
                .stream()
                .filter(slotTmp -> slotTmp.getId().equals(slotId))
                .findFirst()
                .orElseThrow(() -> new SlotNotFoundException(slotId, username));
                if (!slot.isActive()) throw new SlotIsDisabledException(slotId, username);
        return slot;
    }

}