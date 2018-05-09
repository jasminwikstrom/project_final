package se.javagroup.projecttask.service.exception;

public class WorkItemNotFoundException extends RuntimeException{
    public WorkItemNotFoundException(String message){
        super(message);
    }
}
