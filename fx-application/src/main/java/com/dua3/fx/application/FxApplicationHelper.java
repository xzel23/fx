package com.dua3.fx.application;

import com.dua3.utility.fx.FxLogPane;
import com.dua3.utility.fx.FxLogWindow;
import com.dua3.utility.logging.LogBuffer;
import com.dua3.utility.logging.LogEntryDispatcher;
import com.dua3.utility.logging.LogLevel;
import com.dua3.utility.logging.LogUtil;
import com.dua3.utility.logging.log4j.LogUtilLog4J;
import com.dua3.utility.options.ArgumentsParser;
import com.dua3.utility.options.ArgumentsParserBuilder;
import com.dua3.utility.options.Repetitions;
import javafx.application.Application;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * A helper class for initializing and running JavaFX applications with additional configurations such as logging
 * and command-line argument parsing. This class provides utility methods to manage logging options, assertion
 * handling, and dynamic application startup.
 *
 * <p><strong>Note:</strong> The application class must not be a top level class when using the
 * {@link FxApplicationHelper} to start the application as JavaFX "hacks" the startup code to
 * start the JavaFX platform before {@code main()} is called which will lead to a runtime exception.
 *
 * <p>This class is a utility and is not meant to be instantiated.
 */
public final class FxApplicationHelper {

    /**
     * Exit code indicating successful execution of the application.
     */
    public static final int RC_SUCCESS = 0;
    /**
     * Exit code indicating that an error occurred during the execution of the application.
     */
    public static final int RC_ERROR = 1;

    private static volatile @Nullable LogBuffer logBuffer;
    private static volatile boolean showLogWindow;
    private static volatile boolean showLogPane;

    private FxApplicationHelper() {
        // utility class
    }

    private static final int DEFAULT_LOG_BUFFER_SIZE = 100000;

    /**
     * Runs the specified JavaFX application class with the provided arguments and settings.
     *
     * @param applicationClassName the fully qualified name of the JavaFX application class to be launched
     * @param args                 the command-line arguments passed to the application
     * @param appName              the name of the application
     * @param version              the version of the application
     * @param copyright            the copyright notice of the application
     * @param developerMail        the developer's contact email
     * @param appDescription       a brief description of the application
     * @param addOptions          variable-length array of consumers that add additional command-line options to the parser
     * @return the exit code of the application; 0 indicates successful execution, 1 indicates an error
     */
    @SafeVarargs
    public static int runApplication(
            String applicationClassName,
            String[] args,
            String appName,
            String version,
            String copyright,
            String developerMail,
            String appDescription,
            Consumer<ArgumentsParserBuilder>... addOptions
    ) {
        var agp = ArgumentsParser.builder()
                .name(appName)
                .description("Version " + version + "\n"
                        + copyright + " (" + developerMail + ")\n"
                        + "\n"
                        + appDescription
                        + "\n"
                )
                .positionalArgs(0, 0);

        var flagHelp = agp.addFlag(
                "Help",
                "Show Help and quit.",
                "--help", "-h"
        );
        var flagEnableAssertions = agp.addFlag(
                "Runtime Checks",
                "Enable runtime checks.",
                "--enable-assertions", "-ea"
        );
        var flagShowLogWindow = agp.addFlag(
                "Show Log Window",
                "Show Log Messages in a separate Window.",
                "--log-window", "-lw"
        );
        var flagShowLogPane = agp.addFlag(
                "Show Log Messages",
                "Show Log Messages in Main Window.",
                "--log-pane", "-lp"
        );
        var optLogLevel = agp.addEnumOption(
                "Log Level",
                "Set the global Log Level.",
                Repetitions.ZERO_OR_ONE,
                "level",
                () -> LogLevel.INFO,
                LogLevel.class,
                "--log-level", "-ll"
        );
        var optLogNameFilter = agp.addStringOption(
                "Log Filfter",
                "Set global Filter for Logger Names.",
                Repetitions.ZERO_OR_ONE,
                "regex",
                () -> null,
                "--log-filter", "-lf"
        );
        var optLogBufferSize = agp.addIntegerOption(
                "Log Buffer Size",
                "Set the Size of the Log Buffer.",
                Repetitions.ZERO_OR_ONE,
                "size",
                () -> DEFAULT_LOG_BUFFER_SIZE,
                "--log-buffer-size", "-ls"
        );

        for (Consumer<ArgumentsParserBuilder> addOption : addOptions) {
            addOption.accept(agp);
        }

        ArgumentsParser ap = agp.build(flagHelp);

        var arguments = ap.parse(args);

        if (arguments.isSet(flagHelp)) {
            //noinspection UseOfSystemOutOrSystemErr
            System.out.println(ap.help());
            return RC_SUCCESS;
        }

        boolean enableAssertions = arguments.isSet(flagEnableAssertions);
        boolean showLogWindow = arguments.isSet(flagShowLogWindow);
        boolean showLogPane = arguments.isSet(flagShowLogPane);

        int logBufferSize = arguments.getOrThrow(optLogBufferSize);

        LogLevel logLevel = arguments.getOrThrow(optLogLevel);
        LogUtilLog4J.init(logLevel);

        LogEntryDispatcher dispatcher = LogUtil.getGlobalDispatcher();
        arguments.ifPresent(optLogNameFilter, pattern -> {
            Predicate<String> predicate = Pattern.compile(pattern).asMatchPredicate();
            dispatcher.setFilter(entry -> predicate.test(entry.loggerName()));
        });

        if (showLogWindow || showLogPane) {
            LogBuffer logBuffer = new LogBuffer(logBufferSize);
            dispatcher.addLogEntryHandler(logBuffer);
            FxApplicationHelper.logBuffer = logBuffer;
            FxApplicationHelper.showLogWindow = showLogWindow;
            FxApplicationHelper.showLogPane = showLogPane;
        }

        Logger log = LogManager.getLogger(FxApplicationHelper.class);
        int rc;
        try {
            ClassLoader loader = ClassLoader.getSystemClassLoader();

            if (enableAssertions) {
                log.info("enabling assertions");
                loader.setDefaultAssertionStatus(true);
            }
            log.info("loading application class: {}", applicationClassName);
            @SuppressWarnings("unchecked")
            Class<? extends Application> applicationClass = (Class<? extends Application>) loader.loadClass(applicationClassName);
            log.info("starting application: {}", applicationClass.getName());
            FxLauncher.launch(applicationClass, args);
            rc = RC_SUCCESS;
        } catch (Exception e) {
            log.error("exception caught", e);
            rc = RC_ERROR;
        }

        log.info("finished with rc: {}", rc);
        return rc;
    }

    /**
     * Retrieves the current LogBuffer instance if it is available.
     *
     * @return an {@link Optional} containing the LogBuffer instance if it exists, or an empty {@link Optional} if not.
     */
    public static Optional<LogBuffer> getLogBuffer() {
        return Optional.ofNullable(logBuffer);
    }

    /**
     * Determines whether the log pane should be displayed.
     *
     * @return true if the log pane is set to be displayed, false otherwise.
     */
    public static boolean isShowLogPane() {
        return showLogPane;
    }

    /**
     * Determines whether the log window should be displayed.
     *
     * @return true if the log window is configured to be displayed, false otherwise.
     */
    public static boolean isShowLogWindow() {
        return showLogWindow;
    }

    /**
     * Displays a log window for the application, if configured to do so.
     *
     * @param stage the owner window for the log window; can be null if no parent stage is specified.
     * @param title the title of the log window to be displayed.
     * @return an {@link Optional} containing the {@link FxLogWindow} instance if the log window is displayed,
     *         or an empty {@link Optional} if the log window is not configured to be displayed.
     */
    public static Optional<FxLogWindow> showLogWindow(Window stage, String title) {
        if (showLogWindow) {
            FxLogWindow logWindow = new FxLogWindow(title, getLogBuffer().orElseThrow());
            logWindow.initOwner(stage);
            logWindow.show();
            return Optional.of(logWindow);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Displays a log window for the application, if configured to do so.
     *
     * @param stage the owner window for the log window; can be null if no parent stage is specified.
     * @return an {@link Optional} containing the {@link FxLogWindow} instance if the log window is displayed,
     *         or an empty {@link Optional} if the log window is not configured to be displayed.
     */
    public static Optional<FxLogWindow> showLogWindow(Window stage) {
        return showLogWindow(stage, "Log Messages");
    }

    /**
     * Retrieves an instance of the {@link FxLogPane} if the log pane is configured to be displayed.
     *
     * @return an {@link Optional} containing the {@link FxLogPane} instance if the log pane is enabled,
     *         or an empty {@link Optional} if the log pane is disabled.
     */
    public static Optional<FxLogPane> getLogPane() {
        if (!showLogPane) {
            return Optional.empty();
        }

        return Optional.of(new FxLogPane(getLogBuffer().orElseThrow()));
    }
}
