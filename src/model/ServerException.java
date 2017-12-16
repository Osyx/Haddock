package model;

public class ServerException extends Exception {
    private String message;
    private Exception exception;

    ServerException(String msg, Exception e) {
        this.message = msg;
        this.exception = e;
    }

    public ServerException(String msg) {
        this.message = msg;
    }

    public Exception getException() {
        return exception;
    }

    public String getMessage() {
        return message;
    }
}
