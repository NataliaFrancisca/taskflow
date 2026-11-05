package br.com.taskflow.dto;

import br.com.taskflow.entity.Status;
import jakarta.validation.constraints.NotNull;

public record EditStatusTaskDTO(
        @NotNull(message = "the field status is required.")
        Status status
) {
}
