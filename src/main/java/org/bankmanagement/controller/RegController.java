package org.bankmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.dataobject.RegisterTicket;
import org.bankmanagement.service.ClientService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegController {

    private final ClientService service;

    @PostMapping("/api/login/reg")
    public ClientDto registerClient(@RequestBody @Validated RegisterTicket ticket) {
        return service.regUser(ticket);
    }
}
