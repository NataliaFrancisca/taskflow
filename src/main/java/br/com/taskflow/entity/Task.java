package br.com.taskflow.entity;

import br.com.taskflow.dto.EditTaskDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private String message;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Task(){}

    public Task(String title, String message){
        this.title = title;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.status = Status.PLANNED;
    }

    public void update(EditTaskDTO dto){
        if (dto.title() != null) this.title = dto.title();
        if (dto.message() != null) this.message = dto.message();
        if (dto.status() != null) this.status = dto.status();
    }

    public UUID getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
