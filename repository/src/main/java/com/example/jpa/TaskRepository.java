package com.example.jpa;
import com.example.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;


public interface TaskRepository extends CrudRepository<TaskEntity,Integer> {
    List<TaskEntity> findAllByUserId(int UserId);
    Page<TaskEntity> findAllByUserId(int userId, Pageable pageable);

    @Query("SELECT t FROM TaskEntity t WHERE t.user.id = :userId AND ((t.startDate <= :endDate AND t.endDate >= :startDate) OR (t.startDate <= :startDate AND t.endDate >= :startDate) OR (t.startDate <= :endDate AND t.endDate >= :endDate))")
    List<TaskEntity> findOverlappingTasks(@Param("userId") Integer userId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);}