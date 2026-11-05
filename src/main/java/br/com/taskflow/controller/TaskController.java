package br.com.taskflow.controller;

import br.com.taskflow.dto.EditStatusTaskDTO;
import br.com.taskflow.dto.EditTaskDTO;
import br.com.taskflow.dto.RegisterTaskDTO;
import br.com.taskflow.dto.ViewTaskDTO;
import br.com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService service;

    @PostMapping
    public ResponseEntity<ViewTaskDTO> create(@RequestBody @Valid RegisterTaskDTO dto){
        var response = this.service.create(dto);

        URI location = URI.create("/api/tasks/" + response.id());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ViewTaskDTO>> listAll(){
        return ResponseEntity.ok(this.service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViewTaskDTO> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ViewTaskDTO> edit(@PathVariable UUID id, @RequestBody @Valid EditTaskDTO dto){
        return ResponseEntity.ok(this.service.edit(id, dto));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ViewTaskDTO> changeStatus(@PathVariable UUID id, @RequestBody @Valid EditStatusTaskDTO dto){
        return ResponseEntity.ok(this.service.changeTaskStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id){
        this.service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
