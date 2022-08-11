package org.bankmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.aspect.log.LogMethod;
import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.dataobject.UpdateTicket;
import org.bankmanagement.service.ClientService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class ClientController {

    private final ClientService service;

    @LogMethod
    @GetMapping
    public ClientDto getClientInfo(Principal principal) {
        return service.getClient(principal.getName());
    }

    @LogMethod
    @PutMapping
    public ClientDto updateClientInfo(@RequestBody @Validated UpdateTicket ticket, Principal principal) {
        return service.updateClient(principal.getName(), ticket);
    }

}
