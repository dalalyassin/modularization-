package com.example.service;
import com.example.aggregate.Users;
import com.example.exceptions.NotAllowedException;
import org.modelmapper.ModelMapper;
import com.example.JWTSecurity.JwtUtil;
import com.example.aggregate.Tasks;
import com.example.entity.TaskEntity;
import com.example.entity.UserEntity;
import com.example.exceptions.ExceptionNotFound;
import com.example.jpa.TaskRepository;
import com.example.jpa.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class TaskService implements ITaskService {
    public final Logger LOGGER = LoggerFactory.getLogger(TaskService.class.getName());

    @Autowired
    private TaskRepository TaskRepo;
    @Autowired
    private UserRepository UserRepo;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
     @Autowired
     ModelMapper modelMapper;
    @Override
    @Transactional
    public List<Tasks> getTasks() {
        LOGGER.debug("getting task");
        return (List<Tasks>) convertToModelT(TaskRepo.findAll());
    }

    @Override
    @Transactional
    public Tasks createTask(@RequestBody Tasks task,Users user){
        LOGGER.trace("creating task");

        TaskEntity taskEntity=convertToEntity(task);
        UserEntity userEntity=convertToEntity(user);
        taskEntity.setUser(userEntity);
        userEntity = UserRepo.findById(taskEntity.getUser().getId()).orElse(null);

        if (userEntity == null) {
            throw new ExceptionNotFound("No user id found so add user first");
        } else {
            taskEntity.setUser(userEntity);
        }
        CheckIfValidate(taskEntity, false); //This check will ensure that the task object contains a user object and the user object contains an ID before calling the checkTimeValidation method.
        return  convertToModel(TaskRepo.save(taskEntity));
    }


    @Override
    @Transactional
    public Tasks getTaskById(int id) {
        if (TaskRepo.findById(id).isEmpty()) {
            throw new ExceptionNotFound("This id doesn't exist");
        }
        return convertToModel(TaskRepo.findById(id).get());
    }


    @Override
    @Transactional
    public void deleteTask(int id) {
        LOGGER.trace("deleting task");
        Optional<TaskEntity> task = TaskRepo.findById(id);
        if (task.isEmpty()) {
            throw new ExceptionNotFound("Task with id: " + id + " does not exist.");
        }
        TaskRepo.deleteById(id);
    }

        public   Tasks updateTask(Tasks task, int taskId ,Users user) {
        TaskEntity taskEntity=convertToEntity(task);
        CheckIfValidate(taskEntity, true);
        TaskEntity existingTask = TaskRepo.findById(taskId).orElse(null);
        if (existingTask != null) {
            existingTask.setDescription(task.getDescription());
            existingTask.setStartDate(task.getStartDate());
            existingTask.setEndDate(task.getEndDate());
            existingTask.setDescription(task.getDescription());
             TaskRepo.save(existingTask);
            return convertToModel(existingTask);
        } else {
            throw new ExceptionNotFound("Task not found");
        }
    }

    public void CheckIfValidate(TaskEntity task, boolean updateTask) {
        {
            HttpServletRequest httpServletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Integer userId = jwtUtil.getUserIdFromToken(httpServletRequest);
            Date startDate = task.getStartDate();
            Date endDate = task.getEndDate();
            // Retrieve only the tasks that have overlapping start and end times for the current user
            List<TaskEntity> existingTasks = TaskRepo.findOverlappingTasks(userId, startDate, endDate);
            for (TaskEntity existingTask : existingTasks) {
                if (updateTask && (existingTask.getId() == task.getId())) {
                    continue; // Allow updating the same task
                }
                throw new NotAllowedException("Conflict with existing task: " + existingTask.getId());
            }
        }
    }

    private Tasks convertToModel(TaskEntity taskEntity) {
        return modelMapper.map(taskEntity, Tasks.class);
    }
    private TaskEntity convertToEntity(Tasks tasks) {

        return modelMapper.map(tasks, TaskEntity.class);
    }
    private UserEntity convertToEntity(Users users) {
        return modelMapper.map(users, UserEntity.class);
    }

    private Tasks convertToModelT(Iterable<TaskEntity> taskEntity) {
        return modelMapper.map(taskEntity, Tasks.class);
    }
}