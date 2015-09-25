package com.itrane.boothealth.service;

public class EntityNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public EntityNotFoundException() {
        this("");
    }

    public EntityNotFoundException(String msg) {
        super(msg);
    }

}
