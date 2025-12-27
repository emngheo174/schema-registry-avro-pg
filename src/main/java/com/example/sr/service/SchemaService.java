package com.example.sr.service;

import com.example.sr.model.SchemaEntity;
import com.example.sr.repository.SchemaRepository;
import org.apache.avro.Schema;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class SchemaService {

    private final SchemaRepository repo;

    public SchemaService(SchemaRepository repo) {
        this.repo = repo;
    }

    public SchemaEntity register(String subject, String schemaJson) {
        // Validate Avro
        new Schema.Parser().parse(schemaJson);

        int version = repo.nextVersion(subject);
        String fingerprint = sha256(schemaJson);

        SchemaEntity entity = new SchemaEntity(
                null, subject, version, schemaJson, fingerprint
        );

        repo.save(entity);
        return entity;
    }

    public SchemaEntity latest(String subject) {
        return repo.latest(subject);
    }

    private String sha256(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
