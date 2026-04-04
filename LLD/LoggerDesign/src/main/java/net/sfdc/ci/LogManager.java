package net.sfdc.ci;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum LogLevel {
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL
}

interface Appender {

    public void write(String message) throws IOException;
}

class ConsoleAppender implements Appender {

    @Override
    public void write(String message) {
        System.out.println(message);
    }
}

class FileAppender implements Appender {
    private String filename;
    public FileAppender(String filename) {
        this.filename = filename;
    }

    @Override
    public void write(String message) throws IOException {
        // Write to a file
        try(FileOutputStream fs = new FileOutputStream(this.filename)) {
            fs.write((message).getBytes());
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}

class DatabaseAppender implements Appender {
    @Override
    public void write(String message) {

    }
}

class LogHandler {
    private List<Appender> appenders;
    // Next handler in chain
    private LogHandler logHandler;
    private LogLevel level;

    private boolean canHandle(LogLevel level) {
        if(this.level.compareTo(level) <= 0) return true;
        return false;
    }

    public LogHandler(LogLevel logLevel) {
        this.level = logLevel;
    }

    public void logMessage(LogLevel level, String message) throws IOException {
        for (Appender appender : appenders) {
            appender.write(level.toString() + message);
        }
    }

    public void setAppender(List<Appender> appenders) {
        this.appenders = appenders;
    }

    public void setNextLogHandler(LogHandler logHandler) {
        this.logHandler = logHandler;
    }
}

class Config {
    private HashMap<LogLevel, List<Appender>> mapping;
    public HashMap<LogLevel, List<Appender>> getMapping() {
        return mapping;
    }
}

public class LogManager {
    public static LogHandler getLogger(Config config) {

        LogHandler debugHandler = new LogHandler(LogLevel.DEBUG);
        LogHandler infoHandler = new LogHandler(LogLevel.INFO);
        LogHandler warnHandler = new LogHandler(LogLevel.WARN);
        LogHandler errorHandler = new LogHandler(LogLevel.ERROR);
        LogHandler fatalHandler = new LogHandler(LogLevel.FATAL);

        fatalHandler.setNextLogHandler(errorHandler);
        errorHandler.setNextLogHandler(warnHandler);
        warnHandler.setNextLogHandler(infoHandler);
        infoHandler.setNextLogHandler(debugHandler);

        for(Map.Entry<LogLevel, List<Appender>> e: config.getMapping().entrySet()) {
            LogHandler logHandler = getHandler(e.getKey(), debugHandler, infoHandler, warnHandler, errorHandler, fatalHandler);
            logHandler.setAppender(e.getValue());
        }

        return fatalHandler;
    }

    private static LogHandler getHandler(LogLevel key, LogHandler debugHandler, LogHandler infoHandler, LogHandler warnHandler, LogHandler errorHandler, LogHandler fatalHandler) {
        switch (key) {
            case DEBUG:
                return debugHandler;
            case INFO:
                return infoHandler;
            case WARN:
                return warnHandler;
            case ERROR:
                return errorHandler;
            case FATAL:
                return fatalHandler;
        }

        throw new IllegalArgumentException("Bad key: " + key);
    }
}

