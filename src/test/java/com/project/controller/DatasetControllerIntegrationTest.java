package com.project.controller;

import com.project.repository.DatasetRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DatasetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatasetRecordRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldInsertAndGroupAndSortRecordsEndToEnd() throws Exception {
        // 1. Insert three records
        mockMvc.perform(post("/api/dataset/employee_dataset/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"id": 1, "name": "John Doe", "age": 30, "department": "Engineering"}
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.message").value("Record added successfully"))
            .andExpect(jsonPath("$.dataset").value("employee_dataset"))
            .andExpect(jsonPath("$.recordId").isNumber());

        mockMvc.perform(post("/api/dataset/employee_dataset/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"id": 2, "name": "Jane Smith", "age": 25, "department": "Engineering"}
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/dataset/employee_dataset/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"id": 3, "name": "Alice Brown", "age": 28, "department": "Marketing"}
                    """))
            .andExpect(status().isCreated());

        // 2. Query with groupBy=department
        mockMvc.perform(get("/api/dataset/employee_dataset/query")
                .param("groupBy", "department"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.groupedRecords.Engineering", hasSize(2)))
            .andExpect(jsonPath("$.groupedRecords.Marketing", hasSize(1)));

        // 3. Query with sortBy=age&order=asc
        mockMvc.perform(get("/api/dataset/employee_dataset/query")
                .param("sortBy", "age")
                .param("order", "asc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sortedRecords", hasSize(3)))
            .andExpect(jsonPath("$.sortedRecords[0].name").value("Jane Smith"))
            .andExpect(jsonPath("$.sortedRecords[1].name").value("Alice Brown"))
            .andExpect(jsonPath("$.sortedRecords[2].name").value("John Doe"));
    }

    @Test
    void shouldReturnNotFoundForUnknownDataset() throws Exception {
        mockMvc.perform(get("/api/dataset/unknown_dataset/query")
                .param("groupBy", "department"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void shouldReturnBadRequestWhenBothGroupByAndSortByProvided() throws Exception {
        mockMvc.perform(get("/api/dataset/employee_dataset/query")
                .param("groupBy", "department")
                .param("sortBy", "age"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }
}