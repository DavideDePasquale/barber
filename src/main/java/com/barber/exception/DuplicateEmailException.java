package com.barber.exception;

public class DuplicateEmailException extends Exception{
    public DuplicateEmailException(String ex){
        super(ex);
    }
}
