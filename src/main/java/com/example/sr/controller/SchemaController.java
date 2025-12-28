package com.example.sr.controller;

import com.example.sr.service.SchemaRegistryService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/subjects")
public class SchemaController {

    private final SchemaRegistryService service;

    public SchemaController(SchemaRegistryService service) {
        this.service = service;
    }

    @PostMapping("/subjects/{subject}/versions")
    public Map<String, Object> register(
            @PathVariable String subject,
            @RequestBody RegisterSchemaRequest req
    ) {
        SchemaEntity s = service.register(subject, req.getSchema());
        return Map.of(
            "id", s.id(),
            "version", s.version()
        );
    }

    @GetMapping("/{subject}/versions/latest")
    public Map<String, Object> latest(@PathVariable String subject) {
        return service.latest(subject);
    }
}
