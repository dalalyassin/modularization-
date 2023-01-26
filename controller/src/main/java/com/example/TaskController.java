package com.example;

import com.example.JWTSecurity.JwtUtil;
import com.example.aggregate.Tasks;
import com.example.aggregate.Users;
import com.example.entity.TaskEntity;
import com.example.entity.UserEntity;
import com.example.exceptions.NotAllowedException;
import com.example.jpa.TaskRepository;
import com.example.service.IUserService;
import com.example.service.TaskService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/task")

public class TaskController {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TaskService taskservice;

    private IUserService userService;
    private JwtUtil jwtUtil;
    @Autowired
    private TaskRepository taskRepository;

    public TaskController(TaskService taskservice, IUserService userservice, JwtUtil jwtUtil, TaskRepository taskRepository) {
        this.taskservice = taskservice;
        this.userService = userservice;
        this.jwtUtil = jwtUtil;
        this.taskRepository = taskRepository;
    }
    @GetMapping()
    public Page<TaskEntity> returnAllTasks(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam Optional<String> sortBy ,
                                           @RequestParam Optional<String> sortDirection, HttpServletRequest request) {

        int userId = jwtUtil.getUserIdFromToken(request);
        String sortByValue = sortBy.orElse("id");
        String sortDirectionValue = sortDirection.orElse("asc");
        PageRequest pageRequest = PageRequest.of(page, 4, Sort.Direction.fromString(sortDirectionValue), sortByValue);
        return taskRepository.findAllByUserId(userId, pageRequest);
    }

    @GetMapping("/tasks")
    public List<TaskEntity> getTask(HttpServletRequest request) {
        // Get the user ID of the currently logged-in user
        int currentUserId =  jwtUtil.getUserIdFromToken(request);
        // Retrieve the list of tasks belonging to the current user from the repository
        List<TaskEntity> tasks = taskRepository.findAllByUserId(currentUserId);
        return tasks;
    }



     @PostMapping
    public Tasks createTask(@RequestBody Tasks task, HttpServletRequest request) {
             // Get the user ID from the token
          TaskEntity taskEntity=convertToEntity(task);
             int userId = jwtUtil.getUserIdFromToken(request);
             // Retrieve the user from the service
             Users user = userService.getById(userId);
        UserEntity userEntity=convertToEntity(user);
             // Set the user ID of the task to the user's ID
             taskEntity.setUser(userEntity);
             // Save the task
             return taskservice.createTask(convertToModel(taskEntity),user);
         }

    @PutMapping("/{id}")
    public Tasks updateTask(@RequestBody Tasks task, @PathVariable int id,Users user, HttpServletRequest request)  {
       // checkPermission(id, request);
        return taskservice.updateTask(task, id,user);
    }

        // Update the task

    void checkPermission(int taskId,Users user, HttpServletRequest request) {
        // Get the user ID of the currently logged-in user
          int currentUserId = jwtUtil.getUserIdFromToken(request);
        // Retrieve the task from the service
            Tasks task = taskservice.getTaskById(taskId);
            TaskEntity taskEntity=convertToEntity(task);
              UserEntity userEntity=convertToEntity(user);
                taskEntity.setUser(userEntity);
        // Check that the task belongs to the current user
            if (taskEntity.getUser().getId() != currentUserId) {
            throw new NotAllowedException("You do not have permission to access this task.");
        }
    }
    @DeleteMapping("/{id}")
    public String deleteTask ( @PathVariable int id,Users users, HttpServletRequest request) {
            // Check that the user has permission to delete the task
            checkPermission(id, users, request);
            // Delete the task
            taskservice.deleteTask(id);
        return "Task deleted successfully";
    }
    @GetMapping ("/{id}")
    public Tasks getById(@PathVariable int id,Users user, HttpServletRequest request) {
        // Check that the user has permission to view the task
        checkPermission(id,user, request);
        // Retrieve the task from the service
        return taskservice.getTaskById(id);
    }
   private UserEntity convertToEntity(Users user) {
    return modelMapper.map(user, UserEntity.class);
}
    private TaskEntity convertToEntity(Tasks tasks) {

        return modelMapper.map(tasks, TaskEntity.class);
    }
    private Tasks convertToModel(TaskEntity taskEntity) {

        return modelMapper.map(taskEntity, Tasks.class);
    }
}