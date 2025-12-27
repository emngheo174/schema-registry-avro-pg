package com.example.sr.controller;

import com.example.sr.model.SchemaEntity;
import com.example.sr.service.SchemaService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/subjects")
public class SchemaController {

    private final SchemaService service;

    public SchemaController(SchemaService service) {
        this.service = service;
    }

    @PostMapping("/{subject}/versions")
    public Map<String, Object> register(
            @PathVariable String subject,
            @RequestBody Map<String, String> body
    ) {
        SchemaEntity s = service.register(subject, body.get("schema"));
        return Map.of(
                "id", s.id(),
                "version", s.version()
        );
    }

    @GetMapping("/{subject}/versions/latest")
    public SchemaEntity latest(@PathVariable String subject) {
        return service.latest(subject);
    }
}
