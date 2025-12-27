package com.example.sr.model;

public record SchemaEntity(
        Integer id,
        String subject,
        Integer version,
        String schema,
        String fingerprint
) {}
