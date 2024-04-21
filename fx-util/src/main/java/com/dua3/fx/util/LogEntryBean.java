package com.dua3.fx.util;

import com.dua3.utility.logging.LogEntry;
import com.dua3.utility.logging.LogLevel;

import java.time.Instant;

public record LogEntryBean(LogEntry getLogEntry) {
    public Instant getTime() {return getLogEntry.time();}

    public String getLoggerName() {return getLogEntry.loggerName();}

    public LogLevel getLevel() {return getLogEntry.level();}

    public String getMessage() {return getLogEntry.message();}

    public String getMarker() {return getLogEntry.marker();}

    public Throwable getThrowable() {return getLogEntry.throwable();}
}
