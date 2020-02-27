package com.dua3.fx.util;

public class ValidationResult {
    public enum Level {
        OK,
        ERROR;
    }
    
    public final Level level;
    public final String message;
    
    public ValidationResult(Level level, String message) {
        this.level = level;
        this.message = message;
    }
    
    public static ValidationResult ok() {
        return new ValidationResult(Level.OK, "");
    }

    public static ValidationResult error(String message) {
        return new ValidationResult(Level.ERROR, message);
    }
}
