package com.example.exceptions;

public class NotAllowedException extends  RuntimeException{

    public NotAllowedException(String message){
        super(message);
    }
}