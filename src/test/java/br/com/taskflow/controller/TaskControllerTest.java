package br.com.taskflow.controller;

import br.com.taskflow.dto.RegisterTaskDTO;
import br.com.taskflow.dto.ViewTaskDTO;
import br.com.taskflow.entity.Status;
import br.com.taskflow.service.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import javax.print.attribute.standard.Media;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private TaskService service;

    @Test
    void shouldReturnCode400WhenRequestTaskWithError() throws Exception {
        String json = "{}";

        var response = mvc.perform(
                post("/api/tasks")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Assertions.assertEquals(400, response.getStatus());
    }

    @Test
    void shouldReturnCode200WhenRequestTaskWithoutError() throws Exception {

        String json = """
                {
                    "title": "clean house",
                    "message": "clean my room and bedroom"
                }
                """;

        ViewTaskDTO dto = new ViewTaskDTO(
                UUID.randomUUID(),
                "clean house",
                "clean my room and bedroom",
                LocalDateTime.now(),
                LocalDateTime.now(),
                Status.PLANNED
        );

        when(service.create(any(RegisterTaskDTO.class)))
                .thenReturn(dto);

        var response = mvc.perform(
                post("/api/tasks")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Assertions.assertEquals(201, response.getStatus());
    }

    @Test
    void shouldReturnCode200WhenRequestAListTask() throws Exception {
        when(service.getAll())
                .thenReturn(Collections.emptyList());

        mvc.perform(
                get("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                        .andExpect(content().json("[]"));

        verify(service).getAll();
    }

    @Test
    void shouldReturnCode200WhenRequestViewTask() throws Exception {
        UUID id = UUID.randomUUID();

        ViewTaskDTO dto = new ViewTaskDTO(
                id,
                "clean house",
                "clean my room and bedroom",
                LocalDateTime.now(),
                LocalDateTime.now(),
                Status.PLANNED
        );

        when(service.getById(id)).thenReturn(dto);

        mvc.perform(
                get("/api/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("PLANNED"));

        verify(service).getById(id);
    }
}