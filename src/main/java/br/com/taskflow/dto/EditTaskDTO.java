package br.com.taskflow.dto;

import br.com.taskflow.entity.Status;
import jakarta.validation.constraints.Size;

public record EditTaskDTO(
        @Size(min = 1, max = 50, message = "the field title is required.")
        String title,
        @Size(min = 1, max = 250, message = "the field message is required.")
        String message,
        Status status
) {
}
