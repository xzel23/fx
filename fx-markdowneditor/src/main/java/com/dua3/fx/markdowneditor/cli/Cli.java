package com.dua3.fx.markdowneditor.cli;

import com.dua3.fx.markdowneditor.EditorController;
import picocli.CommandLine;

import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.*;

public class Cli implements Runnable {

    private static final Logger LOG = Logger.getLogger(Cli.class.getName());
    private final EditorController controller;

    @CommandLine.Option(names={"-l", "--log-level"})
    Level logLevel = Level.INFO;

    @CommandLine.Parameters(arity = "0..1", description = "the file to open")
    Path file = null;

    private Cli(EditorController controller) { this.controller = Objects.requireNonNull(controller); }

    public static Cli apply(EditorController controller, String... args) {
        Cli cli = new Cli(controller);
        CommandLine cl =  new CommandLine(cli);
        cl.registerConverter(Level.class, s -> Level.parse(s));
        cl.execute(args);
        return cli;
    }

    @Override
    public void run() {
        initLogger();
    }

    private static final int logLevelAllPackages = Level.WARNING.intValue();

    private void initLogger() {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(logLevel);

        Filter f = record -> {
            String loggerName = record.getLoggerName();
            if (!loggerName.startsWith("javafx.") && !loggerName.startsWith("com.sun.webkit.")) {
                return record.getLevel().intValue() >= logLevel.intValue();
            } else {
                return record.getLevel().intValue() >= logLevelAllPackages;
            }
        };

        for (Handler h : rootLogger.getHandlers()) {
            h.setFilter(f);
            h.setLevel(logLevel);
        }

        LOG.info(() -> "log level set to "+logLevel);
    }

    public Optional<Path> getFile() {
        return Optional.ofNullable(file);
    }
}
