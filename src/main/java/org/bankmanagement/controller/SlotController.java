package org.bankmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.aspect.log.LogMethod;
import org.bankmanagement.dataobject.SlotDto;
import org.bankmanagement.dataobject.TransferDto;
import org.bankmanagement.service.SlotService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("slot")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @LogMethod
    @GetMapping
    public List<SlotDto> getAllSlots(Principal principal) {
        return slotService.getAllSlots(principal.getName());
    }

    @LogMethod
    @GetMapping("{slotId}")
    public SlotDto getSlot(@PathVariable Long slotId, Principal principal) {
        return slotService.getSlot(slotId, principal.getName());
    }

    @LogMethod
    @PostMapping
    public SlotDto openSlot(Principal principal) {
        return slotService.createSlot(principal.getName());
    }

    @LogMethod
    @PatchMapping
    public SlotDto updateSlotState(@RequestParam(required = false) Long slotId,
                                   @RequestParam @NotNull Boolean deposit,
                                   @RequestParam @NotNull @Positive Long transferSum,
                                   Principal principal) {
        return slotService.updateSlotState(slotId, deposit, transferSum, principal.getName());
    }

    @LogMethod
    @PostMapping("transfer")
    public SlotDto transferToCustomer(@Validated @RequestBody TransferDto transferDto, Principal principal) {
        return slotService.transferToCustomer(transferDto, principal.getName());
    }

    @LogMethod
    @DeleteMapping("{slotId}")
    public SlotDto closeSlot(@PathVariable Long slotId, Principal principal) {
        return slotService.deactivateSlot(slotId, principal.getName());
    }
}
