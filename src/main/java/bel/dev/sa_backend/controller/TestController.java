package bel.dev.sa_backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "test")
public class TestController {

    
    @GetMapping(path = "string")
    public String getString() {
        return "Test successful!";
    }

    @GetMapping(path = "list")
    public List<String> getList() {
        return List.of("Test successful!", "Test successful!1", "Test successful!2");
    }
}
