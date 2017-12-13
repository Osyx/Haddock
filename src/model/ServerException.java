package model;

public class ServerException extends Exception {
    private String errorMessage;
    private Exception exception;

    ServerException(String msg, Exception e) {
        this.errorMessage = msg;
        this.exception = e;
    }

    public ServerException(String msg) {
        this.errorMessage = msg;
    }

    public Exception getException() {
        return exception;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
