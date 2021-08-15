package com.dua3.fx.util;

public record ValidationResult(Level level, String message) {
    public enum Level {
        OK,
        ERROR
    }
    
    public static ValidationResult ok() {
        return new ValidationResult(Level.OK, "");
    }

    public static ValidationResult error(String message) {
        return new ValidationResult(Level.ERROR, message);
    }
}
