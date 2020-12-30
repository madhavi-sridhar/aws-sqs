package com.prasan.controller;

import com.prasan.dto.Person;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {

    @PostMapping("/process")
    public void sendMessage(@RequestBody Person person) {
        System.out.println("\n------------Person details-------------");
        System.out.println(person.toString());
    }
}
