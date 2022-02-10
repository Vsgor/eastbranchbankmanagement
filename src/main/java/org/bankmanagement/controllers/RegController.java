package org.bankmanagement.controllers;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.data_transfer_objects.RegisterTicket;
import org.bankmanagement.services.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegController {

    private final SecurityService service;

    @PostMapping("/api/login/reg")
    public ResponseEntity<?> registerClient(@RequestBody @Validated RegisterTicket ticket) {
        return new ResponseEntity(service.register(ticket), HttpStatus.OK);
    }
}
