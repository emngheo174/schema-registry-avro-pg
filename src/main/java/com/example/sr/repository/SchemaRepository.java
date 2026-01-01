package com.example.sr.repository;

import com.example.sr.model.SchemaEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class SchemaRepository {

    private final JdbcTemplate jdbc;

    public SchemaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Query(value = """
        SELECT subject
        FROM schemas
        WHERE subject = :subject
        FOR UPDATE
    """, nativeQuery = true)
    
    @Query("SELECT DISTINCT s.subject FROM SchemaEntity s")
    List<String> findAllSubjects();

    @Query("SELECT s.version FROM SchemaEntity s WHERE s.subject = :subject ORDER BY s.version")
    List<Integer> findVersionsBySubject(@Param("subject") String subject);

    String lockSubject(@Param("subject") String subject);
    public SchemaEntity findByFingerprint(String fingerprint) {
        return jdbc.query("""
            SELECT id, subject, version, schema, fingerprint
            FROM schemas
            WHERE fingerprint = ?
        """, rs -> rs.next()
                ? new SchemaEntity(
                    rs.getInt("id"),
                    rs.getString("subject"),
                    rs.getInt("version"),
                    rs.getString("schema"),
                    rs.getString("fingerprint")
                )
                : null,
            fingerprint
        );
    }

    public int nextVersion(String subject) {
        Integer v = jdbc.queryForObject("""
            SELECT COALESCE(MAX(version), 0) + 1
            FROM schemas
            WHERE subject = ?
        """, Integer.class, subject);

        return v == null ? 1 : v;
    }

    public SchemaEntity insert(
            String subject,
            int version,
            String schema,
            String fingerprint
    ) {
        return jdbc.queryForObject("""
            INSERT INTO schemas(subject, version, schema, fingerprint)
            VALUES (?, ?, ?, ?)
            RETURNING id, subject, version, schema, fingerprint
        """,
        (rs, rowNum) -> new SchemaEntity(
            rs.getInt("id"),
            rs.getString("subject"),
            rs.getInt("version"),
            rs.getString("schema"),
            rs.getString("fingerprint")
        ),
        subject, version, schema, fingerprint
        );
    }

    public Map<String, Object> latest(String subject) {
        return jdbc.queryForMap("""
            SELECT id, subject, version, schema
            FROM schemas
            WHERE subject = ?
            ORDER BY version DESC
            LIMIT 1
        """, subject);
    }
}
