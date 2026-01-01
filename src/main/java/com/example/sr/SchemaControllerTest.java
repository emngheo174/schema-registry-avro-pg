package com.example.sr;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SchemaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void registerAndGetLatest() throws Exception {
        String schema = """
        {
          "type": "record",
          "name": "User",
          "fields": [{"name":"id","type":"int"}]
        }
        """;

        mockMvc.perform(post("/subjects/test/versions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                { "schema": %s }
                """.formatted(schema)))
            .andExpect(status().isOk());

        mockMvc.perform(get("/subjects/test/versions/latest"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.schema").exists());
    }

    @Test
    void invalidSchemaReturns400() throws Exception {
        mockMvc.perform(post("/subjects/bad/versions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"schema\":\"invalid\"}"))
            .andExpect(status().isBadRequest());
    }
}
