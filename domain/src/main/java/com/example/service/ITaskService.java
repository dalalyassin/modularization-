package com.example.service;
import com.example.aggregate.Tasks;
import com.example.aggregate.Users;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ITaskService {

    public List<Tasks> getTasks();
    Tasks createTask(@RequestBody Tasks task, Users users);
    public Tasks getTaskById(int id);
    public void deleteTask(int id );

public Tasks updateTask(Tasks task,  int id, Users user) ;
}