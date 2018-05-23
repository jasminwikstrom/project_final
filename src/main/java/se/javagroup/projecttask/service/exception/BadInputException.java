package se.javagroup.projecttask.service.exception;

public final class BadInputException extends RuntimeException {
    public BadInputException(String message) {
        super(message);
    }
}
