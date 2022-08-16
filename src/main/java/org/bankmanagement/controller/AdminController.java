package org.bankmanagement.controller;

import org.bankmanagement.aspect.log.LogMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
public class AdminController {

    @LogMethod
    @GetMapping
    public String helloWorld() {
        return "Hello world";
    }
}
