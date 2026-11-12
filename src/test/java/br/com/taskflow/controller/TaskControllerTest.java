package br.com.taskflow.controller;

import br.com.taskflow.dto.EditStatusTaskDTO;
import br.com.taskflow.dto.EditTaskDTO;
import br.com.taskflow.dto.RegisterTaskDTO;
import br.com.taskflow.dto.ViewTaskDTO;
import br.com.taskflow.entity.Status;
import br.com.taskflow.infra.exception.TaskNotFoundException;
import br.com.taskflow.service.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    void shouldReturnCode404WhenTaskDontExist() throws Exception {
        UUID id = UUID.randomUUID();

        when(service.getById(id))
                .thenThrow(new TaskNotFoundException(id));

        mvc.perform(
                get("/api/tasks/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
        verify(service).getById(id);
    }

    @Test
    void shouldReturnCode200WhenTaskExists() throws Exception {
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
                .andExpect(jsonPath("$.title").value(dto.title()))
                .andExpect(jsonPath("$.message").value(dto.message()))
                .andExpect(jsonPath("$.status").value("PLANNED"));

        verify(service).getById(id);
    }

    @Test
    void shouldReturnCode200WhenTaskIsUpdated() throws Exception {
        UUID id = UUID.randomUUID();

        String json = """
                {
                    "title": "clean bedroom",
                    "message": "organize my bedroom"
                }
                """;

        ViewTaskDTO responseDTO = new ViewTaskDTO(
                id,
                "clean bedroom",
                "organize my bedroom",
                LocalDateTime.now(),
                LocalDateTime.now(),
                Status.PLANNED
        );

        when(service.edit(eq(id), any(EditTaskDTO.class)))
                .thenReturn(responseDTO);

        mvc.perform(
                put("/api/tasks/{id}", id)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.title").value("clean bedroom"))
        .andExpect(jsonPath("$.message").value("organize my bedroom"));

        verify(service).edit(eq(id), any(EditTaskDTO.class));
    }

    @Test
    void shouldReturnCode200WhenTaskStatusIsUpdated() throws Exception {
        UUID id = UUID.randomUUID();

        String json = """
                {
                    "status": "DONE"
                }
                """;

        ViewTaskDTO responseDTO = new ViewTaskDTO(
            id,
            "clean house",
            "clean my romm and bedroom",
            LocalDateTime.now(),
            LocalDateTime.now(),
            Status.DONE
        );

        when(service.changeTaskStatus(eq(id), any(EditStatusTaskDTO.class)))
                .thenReturn(responseDTO);

        mvc.perform(
                patch("/api/tasks/{id}/status", id)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("DONE"));

        verify(service).changeTaskStatus(eq(id), any(EditStatusTaskDTO.class));
    }

    @Test
    void shouldReturnCode204WhenTaskIsDeleted() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(service).delete(id);

        mvc.perform(
                delete("/api/tasks/{id}", id)
        )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).delete(id);
    }
}