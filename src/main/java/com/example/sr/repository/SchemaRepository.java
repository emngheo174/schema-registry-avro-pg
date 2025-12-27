package com.example.sr.repository;

import com.example.sr.model.SchemaEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SchemaRepository {

    private final JdbcTemplate jdbc;

    public SchemaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Integer nextVersion(String subject) {
        return jdbc.queryForObject(
                "SELECT COALESCE(MAX(version), 0) + 1 FROM schemas WHERE subject = ?",
                Integer.class,
                subject
        );
    }

    public void save(SchemaEntity s) {
        jdbc.update("""
            INSERT INTO schemas(subject, version, schema, fingerprint)
            VALUES (?, ?, ?, ?)
        """, s.subject(), s.version(), s.schema(), s.fingerprint());
    }

    public SchemaEntity latest(String subject) {
        return jdbc.queryForObject("""
            SELECT id, subject, version, schema, fingerprint
            FROM schemas
            WHERE subject = ?
            ORDER BY version DESC LIMIT 1
        """, (rs, i) -> new SchemaEntity(
                rs.getInt("id"),
                rs.getString("subject"),
                rs.getInt("version"),
                rs.getString("schema"),
                rs.getString("fingerprint")
        ), subject);
    }
}
