package com.example.sr.service;

import com.example.sr.repository.SchemaRepository;
import org.apache.avro.Schema;
import org.springframework.stereotype.Service;
import com.example.sr.model.SchemaEntity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;

@Service
public class SchemaRegistryService {

    private final SchemaRepository repository;

    public SchemaRegistryService(SchemaRepository repository) {
        this.repository = repository;
    }

    public SchemaEntity register(String subject, String schemaText) {
        validateAvro(schemaText);

        String fingerprint = fingerprint(schemaText);

        // nếu schema đã tồn tại
        SchemaEntity existing = repository.findByFingerprint(fingerprint);
        if (existing != null) {
            return existing;
        }

        int version = repository.nextVersion(subject);
        return repository.insert(subject, version, schemaText, fingerprint);
    }

    public Map<String, Object> latest(String subject) {
        return repository.latest(subject);
    }

    private void validateAvro(String schemaText) {
        new Schema.Parser().parse(schemaText);
    }

    private String fingerprint(String schema) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(schema.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
