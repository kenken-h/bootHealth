package com.itrane.boothealth.service;

public class DbAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public static final int DUPLICATE_ERROR = 1;
    public static final int CONSTRAINT_ERROR = 2;
    public static final int OPTIMISTICK_LOCK_ERROR = 3;
    public static final int OTHER_ERROR = 9;

    private int errorCode;

    public DbAccessException(String msg) {
        super(msg);
    }

    public DbAccessException(String msg, Throwable t) {
        super(msg, t);
    }

    public DbAccessException(String msg, int errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
