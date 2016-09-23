package pl.eoferty.dao.exceptions;

/**
 * Created by Damian on 2015-03-16.
 */
public class ErrorCodeException extends RuntimeException {

    private final String errorCode;

    public ErrorCodeException(String msg) {
        super(msg);
        this.errorCode = "BRAK KODU ";
    }

    public ErrorCodeException(String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = "BRAK KODU ";
    }

    public ErrorCodeException(String msg, String errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }

    public ErrorCodeException(String msg, Throwable cause, String errorCode) {
        super(msg, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }
}
