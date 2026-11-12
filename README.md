# taskflow

a simple REST API for managing tasks, built with Java and Spring Boot.

### features:
- create a task
- list all tasks
- get a task by ID
- edit a task
- change task status
- delete a task

### endpoints
| Method | Route              | Description          |
|--------|--------------------|----------------------|
| POST   | `/tasks`           | Create a task        |
| GET    | `/tasks`           | List all tasks       |
| GET    | `/tasks/{id}`      | Get a task by ID     |
| PUT    | `/tasks/{id}`      | Edit a task          |
| PATCH  | `/tasks/{id}/status` | Change task status |
| DELETE | `/tasks/{id}`      | Delete a task        |

### task model
```java
 public record ViewTaskDTO(
      UUID id,
      String title,
      String message,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      Status status
) {} 
```

### getting started
```bash
git clone https://github.com/your-user/taskflow.git
cd taskflow
./mvnw spring-boot:run
```

### roadmap
- [ ] add Spring Security to assign tasks to users
- [ ] deploy the API online 
