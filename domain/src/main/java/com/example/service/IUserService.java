package com.example.service;

import com.example.aggregate.Users;

import javax.servlet.http.HttpServletRequest;


public interface IUserService {
    public void deleteUser(int id);
    public Users getById(int id);
    public Users createUser(Users User);
    int getCurrentUserId(HttpServletRequest request);
    public Users updateUser(Users User);

    }
