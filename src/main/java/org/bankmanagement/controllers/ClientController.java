package org.bankmanagement.controllers;

import lombok.RequiredArgsConstructor;
import org.bankmanagement.data_transfer_objects.UpdateTicket;
import org.bankmanagement.data_transfer_objects.UserDto;
import org.bankmanagement.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client")
public class ClientController {

    private final UserService service;

    @GetMapping("hello_world")
    public ResponseEntity<String> helloWorld() {
        return new ResponseEntity("Hello world! I'm Client!", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<UserDto> getUserInfo(Principal principal) {
        return new ResponseEntity(service.getUser(principal.getName()), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> updateUserInfo(Principal principal, @RequestBody @Validated UpdateTicket ticket) {
        return new ResponseEntity(service.updateUser(principal.getName(), ticket), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<UserDto> deleteUser(Principal principal) {
        return new ResponseEntity(service.deleteUser(principal.getName()), HttpStatus.OK);
    }
}
