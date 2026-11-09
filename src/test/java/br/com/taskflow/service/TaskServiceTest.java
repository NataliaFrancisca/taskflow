package br.com.taskflow.service;

import br.com.taskflow.dto.EditStatusTaskDTO;
import br.com.taskflow.dto.EditTaskDTO;
import br.com.taskflow.dto.RegisterTaskDTO;
import br.com.taskflow.dto.ViewTaskDTO;
import br.com.taskflow.entity.Status;
import br.com.taskflow.entity.Task;
import br.com.taskflow.infra.exception.TaskNotFoundException;
import br.com.taskflow.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    @Test
    void shouldCreateTaskSuccessfully(){
        // given
        RegisterTaskDTO dto = new RegisterTaskDTO("finish study about junit", "chapter 1 e 2");
        when(repository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Task.class));

        // when
        ViewTaskDTO response = service.create(dto);

        // then
        assertNotNull(response);

        verify(repository).save(taskCaptor.capture());

        Task capturedTask = taskCaptor.getValue();

        assertEquals(dto.title(), capturedTask.getTitle());
        assertEquals(dto.message(), capturedTask.getMessage());
    }

    @Test
    void shouldReturnTaskWhenIdExists(){
        // given
        UUID id = UUID.randomUUID();

        Task task = new Task("should wash hair", "get the shampoo from apice");

        when(repository.findById(any(UUID.class)))
                .thenReturn(Optional.of(task));

        // when
        ViewTaskDTO response = service.getById(id);

        // then
        assertNotNull(response);
        verify(repository).findById(id);
    }

    @Test
    void shouldThrowAnExceptionWhenIdNotExits(){
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> {
            service.getById(id);
        });
    }

    @Test
    void shouldReturnListOfTasksWhenIsFull(){
        List<Task> tasks = new ArrayList<>();

        tasks.add(new Task("task number 1", "listen mario - let me love you"));
        tasks.add(new Task("get hair done", "wash my hair and get done"));
        tasks.add(new Task("study english", "listen music and learn new vocabulary"));

        when(repository.findAll())
                .thenReturn(tasks);

        // when
        List<ViewTaskDTO> response = service.getAll();

        // then
        assertNotNull(response);
        assertEquals(3, response.size());

        assertEquals("task number 1", response.get(0).title());
        assertEquals("listen mario - let me love you", response.get(0).message());

        assertEquals("get hair done", response.get(1).title());
        assertEquals("wash my hair and get done", response.get(1).message());

        assertEquals("study english", response.get(2).title());
        assertEquals("listen music and learn new vocabulary", response.get(2).message());

        verify(repository).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenDontHaveTasksSaved(){
        when(repository.findAll())
                .thenReturn(Collections.emptyList());

        // when
        List<ViewTaskDTO> response = service.getAll();

        // then
        assertTrue(response.isEmpty());
        verify(repository).findAll();
    }

    @Test
    void shouldChangeValuesWhenTaskIsUpdated(){
        UUID id = UUID.randomUUID();
        Task task = new Task("homework", "math exercisees");

        when(repository.findById(id))
                .thenReturn(Optional.of(task));

        when(repository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Task.class));

        EditTaskDTO dto = new EditTaskDTO("", "english exercises", null);

        // when
        ViewTaskDTO response = service.edit(id, dto);

        // then
        assertNotNull(response);
        assertEquals(task.getTitle(), response.title());
        assertEquals("english exercises", response.message());
        assertEquals(task.getStatus(), response.status());

        verify(repository).findById(id);
        verify(repository).save(any(Task.class));
    }

    @Test
    void shouldChangeStatusValueWhenTaskStatusIsUpdated(){
        UUID id = UUID.randomUUID();
        Task task = new Task("homework", "math exercises");

        when(repository.findById(id))
                .thenReturn(Optional.of(task));

        when(repository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Task.class));

        EditStatusTaskDTO dto = new EditStatusTaskDTO(Status.ACTIVE);

        LocalDateTime beforeUpdate = LocalDateTime.now();

        ViewTaskDTO response = service.changeTaskStatus(id, dto);

        LocalDateTime afterUpdate = LocalDateTime.now();

        // then
        assertNotNull(response);
        assertEquals(Status.ACTIVE, response.status());

        assertFalse(response.updatedAt().isBefore(beforeUpdate));
        assertFalse(response.updatedAt().isAfter(afterUpdate));

        verify(repository).findById(id);
        verify(repository).save(any(Task.class));
    }

    @Test
    void shouldDeleteTaskWhenIdExists(){
        // given
        UUID id = UUID.randomUUID();

        Task task = new Task("homework", "math exercises");

        when(repository.findById(id))
                .thenReturn(Optional.of(task));

        // when
        service.delete(id);

        // then
        verify(repository).findById(id);
        verify(repository).delete(task);
    }
}