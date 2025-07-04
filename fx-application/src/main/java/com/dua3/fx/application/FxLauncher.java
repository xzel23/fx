package com.dua3.fx.application;

import com.dua3.utility.lang.Platform;
import javafx.application.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;


/**
 * Application launcher class.
 */
public final class FxLauncher {

    /**
     * Logger
     */
    private static final Logger LOG = LogManager.getLogger(FxLauncher.class);
    private static final Pattern PATTERN_PATH_OR_STARTS_WITH_DOUBLE_DASH = Pattern.compile("^(--|[a-zA-Z]:[/\\\\]).*");

    static {
        // start the runtime
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.startup(latch::countDown);
        while (true) {
            try {
                latch.await();
                break;
            } catch (InterruptedException e) {
                LOG.debug("interrupted while waiting for platform startup", e);
                Thread.currentThread().interrupt(); // Restore the interrupt status
            }
        }
    }

    private FxLauncher() {}

    /**
     * Executes the given Runnable object.
     * <p>
     * Delegation the task to the launcher makes sure that the platform startup is completed
     * before the task is run.
     *
     * @param r the Runnable object to be executed
     */
    public static void run(Runnable r) {
        r.run();
    }

    /**
     * Start application.
     * This method is a drop-in replacement for `Application.launch(cls, args)`.
     * <ul>
     *     <li><strong>Command line arguments</strong> are re-parsed on windows, for details see
     *     {@link #reparseCommandLine(String[])}.
     * </ul>
     *
     * @param <A>  the application class
     * @param cls  the application class
     * @param args the command line arguments
     */
    public static <A extends Application>
    void launch(Class<A> cls, String... args) {
        LOG.debug("arguments: {}", (Object) args);

        // prepare arguments
        var reparsedArgs = reparseCommandLine(args);

        // launch
        Application.launch(cls, reparsedArgs.toArray(String[]::new));
    }

    /**
     * Re-parse command line arguments.
     * <ul>
     * <li><strong>Windows:</strong>
     * At least when using a jpackaged application with file-associations, command line arguments get messed up
     * when the application start is the result of double-clicking on a registered file type.
     * The command line args are split on whitespace, i. e. paths containing spaces
     * will be split into multiple parts. This method tries to restore what was probably meant.
     * It works by iterating over the given array of arguments like this:
     * <pre>
     * let arg = ""
     * for each s in args:
     *
     *   if s starts with "--" // start of an option
     *   or s starts with "[letter]:\" or "[letter]:/" // probable file path
     *   then
     *     append arg to the output array
     *     arg = ""
     *
     *   if arg != ""
     *     arg = arg + " "
     *
     *   arg = arg + s
     * push arg to the output array
     * </pre>
     * <li><strong>Other platforms:</strong>
     * The input array is returned without any changes.
     * </ul>
     *
     * @param args the command line arguments
     * @return the reparsed argument array
     * @deprecated this method is a workaround that will be removed once the underlying issue is fixed in the JDK.
     */
    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    private static List<String> reparseCommandLine(String[] args) {
        if (!Platform.isWindows() || args.length < 2) {
            return List.of(args);
        }

        List<String> argL = new ArrayList<>();
        StringBuilder arg = new StringBuilder();
        for (String s : args) {
            // split if s contains spaces, starts with a double dash, or a windows path
            if (s.indexOf(' ') >= 0 || PATTERN_PATH_OR_STARTS_WITH_DOUBLE_DASH.matcher(s).matches()) {
                if (!arg.isEmpty()) {
                    argL.add(arg.toString());
                }
                arg.setLength(0);
            }

            if (!arg.isEmpty()) {
                arg.append(' ');
            }
            arg.append(s);
        }
        if (!arg.isEmpty()) {
            argL.add(arg.toString());
        }

        LOG.debug("original arguments: {}", (Object) args);
        LOG.debug("re-parsed arguments: {}", argL);

        return argL;
    }

}
