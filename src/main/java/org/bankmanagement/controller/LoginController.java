package org.bankmanagement.controller;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.aspect.log.LogMethod;
import org.bankmanagement.dataobject.ClientDto;
import org.bankmanagement.dataobject.RegisterTicket;
import org.bankmanagement.service.ClientService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("login")
public class LoginController {

    private final ClientService service;

    @LogMethod
    @PostMapping("sign-up")
    public ClientDto registerClient(@RequestBody @Validated RegisterTicket ticket) {
        return service.registerClient(ticket);
    }

}
