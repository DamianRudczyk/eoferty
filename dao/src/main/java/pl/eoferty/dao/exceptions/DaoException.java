package pl.eoferty.dao.exceptions;

/**
 * Created by Damian on 2015-03-16.
 */


public class DaoException extends ErrorCodeException {

    public DaoException(String msg) {
        super(msg);
    }

    public DaoException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DaoException(String msg, String errorCode) {
        super(msg, errorCode);
    }

    public DaoException(String msg, Throwable cause, String errorCode) {
        super(msg, cause, errorCode);
    }

}
