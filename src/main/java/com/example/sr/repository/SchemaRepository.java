package com.example.sr.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class SchemaRepository {

    private final JdbcTemplate jdbc;

    public SchemaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Integer findIdByFingerprint(String fingerprint) {
        return jdbc.query(
                "SELECT id FROM schemas WHERE fingerprint = ?",
                rs -> rs.next() ? rs.getInt("id") : null,
                fingerprint
        );
    }

    public int nextVersion(String subject) {
        return jdbc.queryForObject(
                "SELECT COALESCE(MAX(version), 0) + 1 FROM schemas WHERE subject = ?",
                Integer.class,
                subject
        );
    }

    public int insert(String subject, int version, String schema, String fingerprint) {
        return jdbc.queryForObject(
                """
                INSERT INTO schemas(subject, version, schema, fingerprint)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """,
                Integer.class,
                subject, version, schema, fingerprint
        );
    }

    public Map<String, Object> latest(String subject) {
        return jdbc.queryForMap(
                """
                SELECT subject, version, schema
                FROM schemas
                WHERE subject = ?
                ORDER BY version DESC
                LIMIT 1
                """,
                subject
        );
    }
}
