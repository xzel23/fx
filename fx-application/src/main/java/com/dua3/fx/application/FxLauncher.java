package com.dua3.fx.application;

import com.dua3.utility.lang.Platform;
import javafx.application.Application;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application launcher class.
 */
public final class FxLauncher {

    private FxLauncher() {}
    
    /**
     * Logger
     */
    private static final Logger LOG = Logger.getLogger(FxLauncher.class.getName());

    static {
        // start the runtime
        CountDownLatch latch = new CountDownLatch(1);
        javafx.application.Platform.startup(latch::countDown);
        while (true) {
            try {
                latch.await();
                break;
            } catch (InterruptedException e) {
                LOG.log(Level.FINE, "interrupted while waiting for platform startup", e);
            }
        }
    }
    
    /**
     * Start application.
     * This method is a drop-in replacement for `Application.launch(cls, args)`.
     * <ul>
     *     <li><strong>Comand line arguments</strong> are re-parsed on windows, for details see 
     *     {@link #reparseCommandLine(String[])}.
     * </ul>
     *
     * @param <A>
     *  the application class
     * @param cls
     *  the application class
     * @param args
     *  the comand line arguments
     */
    public static
    <A extends Application>
    void launch(Class<A> cls, String... args) {
        LOG.fine(() -> "arguments: "+Arrays.toString(args));

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
     * It works by iterating over the given array of argumnents like this:
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
     * @param args
     *   the command line arguments
     * @return
     *   the re-parsed argument array
     * @deprecated this method is a workaround that will be removed once the underlying issue is fixed in the JDK.
     */
    @Deprecated
    private static List<String> reparseCommandLine(String[] args) {
        if (!Platform.isWindows() || args.length<2) {
            return List.of(args);
        }

        List<String> argL = new ArrayList<>();
        StringBuilder arg = new StringBuilder();
        for (String s:args) {
            // split if s contains spaces, starts with a double dash, or a windows path
            if (s.indexOf(' ')>=0 || s.matches("^(--|[a-zA-Z]:[/\\\\]).*")) {
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

        LOG.info(() -> "arguments have been re-parsed!");
        LOG.fine(() -> "arguments: "+argL);
        
        return argL;
    }

}
