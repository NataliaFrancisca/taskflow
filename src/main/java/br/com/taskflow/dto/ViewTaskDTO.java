package br.com.taskflow.dto;

import br.com.taskflow.entity.Status;
import br.com.taskflow.entity.Task;

import java.time.LocalDateTime;
import java.util.UUID;

public record ViewTaskDTO(
        UUID id,
        String title,
        String message,
        LocalDateTime createdAt,
        Status status
) {
    public ViewTaskDTO(Task task){
        this(
                task.getId(),
                task.getTitle(),
                task.getMessage(),
                task.getCreatedAt(),
                task.getStatus()
        );
    }
}
