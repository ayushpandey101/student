package dev.StudentApp.service;

public class DuplicateAccountException extends RuntimeException {

    private final String field;

    public DuplicateAccountException(String field) {
        super("An account already uses this " + field);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
