package com.naloga.rest.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class HelloWorldController {

    @GetMapping(value = "/hello-world", produces = "application/json")
    @ApiOperation(value = "Responses with Hello world!")
    @ResponseStatus(HttpStatus.OK)
    public HashMap<String, String> hello_world() {
        HashMap<String, String> response = new HashMap<>();
        response.put("data", "Hello world!");
        return response;
    }
}
