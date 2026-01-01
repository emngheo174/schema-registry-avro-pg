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

    @Transactional
    public SchemaEntity register(String subject, String schemaText) {

        // 1. validate avro schema
        try {
            new org.apache.avro.Schema.Parser().parse(schemaText);
        } catch (Exception e) {
            throw new InvalidSchemaException("Invalid Avro schema: " + e.getMessage());
        }

        // 2. lock subject (race condition fix)
        schemaRepository.lockSubject(subject);

        // 3. calculate next version
        Integer maxVersion = schemaRepository.findMaxVersionBySubject(subject);
        int nextVersion = (maxVersion == null) ? 1 : maxVersion + 1;

        // 4. save
        SchemaEntity entity = new SchemaEntity();
        entity.setSubject(subject);
        entity.setVersion(nextVersion);
        entity.setSchema(schemaText);

        return schemaRepository.save(entity);
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
