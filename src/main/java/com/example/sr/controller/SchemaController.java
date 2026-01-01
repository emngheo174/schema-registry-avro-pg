package com.example.sr.controller;

import com.example.sr.service.SchemaRegistryService;
import org.springframework.web.bind.annotation.*;
import com.example.sr.dto.RegisterSchemaRequest;
import com.example.sr.model.SchemaEntity;

import java.util.Map;

@RestController
@RequestMapping("/subjects")
public class SchemaController {

    private final SchemaRegistryService service;

    public SchemaController(SchemaRegistryService service) {
        this.service = service;
    }

    @PostMapping("/{subject}/versions")
    public Map<String, Object> register(
            @PathVariable String subject,
            @RequestBody RegisterSchemaRequest req
    ) {
        return service.register(subject, body.get("schema"));
    }

    @GetMapping("/{subject}/versions/latest")
    public Map<String, Object> latest(@PathVariable String subject) {
        return service.latest(subject);
    }

    @GetMapping("/subjects")
    public List<String> listSubjects() {
        return schemaRepository.findAllSubjects();
    }

    @GetMapping("/{subject}/versions")
    public List<Integer> listVersions(@PathVariable String subject) {
        return schemaRepository.findVersionsBySubject(subject);
    }
}
