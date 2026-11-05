package br.com.taskflow.dto;

import jakarta.validation.constraints.Size;

public record RegisterTaskDTO(
        @Size(min=3, max = 36, message = "the field title size is 36 characters")
        String title,

        @Size(min=3, max = 150, message = "the field message size is 150 characters")
        String message
) {
}
