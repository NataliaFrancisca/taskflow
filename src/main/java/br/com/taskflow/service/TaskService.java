package br.com.taskflow.service;

import br.com.taskflow.dto.EditStatusTaskDTO;
import br.com.taskflow.dto.EditTaskDTO;
import br.com.taskflow.dto.RegisterTaskDTO;
import br.com.taskflow.dto.ViewTaskDTO;
import br.com.taskflow.entity.Task;
import br.com.taskflow.infra.exception.TaskNotFoundException;
import br.com.taskflow.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    public ViewTaskDTO create(RegisterTaskDTO dto){
        Task task = new Task(dto.title(), dto.message());
        var response = this.repository.save(task);
        return new ViewTaskDTO(response);
    }

    public ViewTaskDTO getById(UUID id){
        Task task = this.repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        return new ViewTaskDTO(task);
    }

    public List<ViewTaskDTO> getAll(){
        var response = this.repository.findAll();
        return response.stream().map(ViewTaskDTO::new).toList();
    }

    public ViewTaskDTO edit(UUID id, EditTaskDTO dto){
        Task task = this.repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.update(dto);

        return new ViewTaskDTO(this.repository.save(task));
    }

    public ViewTaskDTO changeTaskStatus(UUID id, EditStatusTaskDTO dto){
        Task task = this.repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setStatus(dto.status());
        task.setUpdatedAt(LocalDateTime.now());

        return new ViewTaskDTO(this.repository.save(task));
    }

    public void delete(UUID id){
        Task task = this.repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        this.repository.delete(task);
    }

}
