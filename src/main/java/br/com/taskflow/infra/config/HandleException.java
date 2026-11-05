package br.com.taskflow.infra.config;

import br.com.taskflow.infra.exception.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class HandleException {

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity handleTaskNotFound(TaskNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleArgumentNotValid(MethodArgumentNotValidException exception){
        var erros = exception
                .getFieldErrors()
                .stream()
                .map(DataBadRequest::new)
                .toList();

        DataBadRequests data = new DataBadRequests(erros);

        return ResponseEntity.badRequest().body(erros);
    }

    private record DataBadRequests(List<DataBadRequest> erros){}

    private record DataBadRequest(String field, String message){
        public DataBadRequest(FieldError error){
            this(error.getField(), error.getDefaultMessage());
        }
    }


}
