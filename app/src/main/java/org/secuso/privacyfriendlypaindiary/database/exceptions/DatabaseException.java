package org.secuso.privacyfriendlypaindiary.database.exceptions;

/**
 * @author Susanne Felsen
 * @version 20171119
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException() {
        super();
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(Throwable throwable) {
        super(throwable);
    }

    public DatabaseException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
