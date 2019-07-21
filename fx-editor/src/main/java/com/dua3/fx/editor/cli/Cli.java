package com.dua3.fx.editor.cli;

import com.dua3.fx.editor.EditorController;
import picocli.CommandLine;

import java.util.Objects;
import java.util.logging.*;

import static com.dua3.fx.editors.EditorBase.LOGGER_TAG_JAVA_SCRIPT;

public class Cli implements Runnable {

    private static final Logger LOG = Logger.getLogger(Cli.class.getName());
    private final EditorController controller;

    @CommandLine.Option(names = {"-l", "--log-level"}, description = "set log level for application package")
    Level logLevel = Level.INFO;

    @CommandLine.Option(names = {"-lj", "--log-level-javascript"}, description = "set log level for JavaScript messages")
    Level logLevelJavaScript = Level.INFO;

    @CommandLine.Option(names = {"-lg", "--log-level-global"}, description = "set global log level")
    Level logLevelGlobal = Level.WARNING;

    private Cli(EditorController controller) { this.controller = Objects.requireNonNull(controller); }

    public static void apply(EditorController controller, String... args) {
        Cli cli = new Cli(controller);
        CommandLine cl =  new CommandLine(cli);
        cl.registerConverter(Level.class, s -> Level.parse(s));
        cl.execute(args);
    }

    @Override
    public void run() {
        initLogger();
    }

    private void initLogger() {
        int minLevel = Math.min(Math.min(logLevel.intValue(), logLevelJavaScript.intValue()), logLevelGlobal.intValue());

        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.parse("" + minLevel));

        Filter f = record -> {
            String loggerName = record.getLoggerName();
            if (loggerName.startsWith("com.dua3.")) {
                if (loggerName.contains(LOGGER_TAG_JAVA_SCRIPT)) {
                    return record.getLevel().intValue() >= logLevelJavaScript.intValue();
                } else {
                    return record.getLevel().intValue() >= logLevel.intValue();
                }
            }

            return record.getLevel().intValue() >= logLevelGlobal.intValue();
        };

        for (Handler h : rootLogger.getHandlers()) {
            h.setFilter(f);
            h.setLevel(logLevel);
        }

        LOG.info(() -> "log level set to "+logLevel);
    }

}
