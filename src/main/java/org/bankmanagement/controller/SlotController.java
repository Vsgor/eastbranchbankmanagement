package org.bankmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.dataobject.SlotDto;
import org.bankmanagement.service.SlotService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("slot")
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @PostMapping
    public SlotDto openSlot(Principal principal) {
        return slotService.createSlot(principal.getName());
    }

    public SlotDto deposit() {
        return null;
    }

    public SlotDto withdraw() {
        return null;
    }

    public SlotDto transferToCustomer() {
        return null;
    }

    public SlotDto closeSlot() {
        return null;
    }
}
