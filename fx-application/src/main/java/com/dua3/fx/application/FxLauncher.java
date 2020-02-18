package com.dua3.fx.application;

import com.dua3.utility.lang.LangUtil;
import com.dua3.utility.lang.Platform;
import javafx.application.Application;

import java.awt.*;
import java.awt.desktop.*;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application launcher class.
 */
public class FxLauncher {

    /**
     * Logger
     */
    private static final Logger LOG = Logger.getLogger(FxLauncher.class.getName());

    private static final Deque<URI> uriList = new ConcurrentLinkedDeque<URI>();
    
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
        
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop!=null) {
                if (desktop.isSupported(Desktop.Action.APP_OPEN_FILE)) {
                    desktop.setOpenFileHandler(e -> e.getFiles().forEach(f -> uriList.add(f.toURI())));
                }
                if (desktop.isSupported(Desktop.Action.APP_OPEN_URI)) {
                    desktop.setOpenURIHandler(e -> uriList.add(e.getURI()));
                }
            }
        }
    }
    
    /**
     * Mark launch as finished, and return the URIs collected during startup.
     * @param app the FxApplication instance that was launched
     * @return collection containing the URI collected by the file and URI handlers
     */
    static List<URI> launchFinished(FxApplication<?,?> app) {
        Objects.requireNonNull(app);

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.APP_OPEN_FILE)) {
                desktop.setOpenFileHandler(app::openFiles);
            }
            if (desktop.isSupported(Desktop.Action.APP_OPEN_URI)) {
                desktop.setOpenURIHandler(app::openURI);
            }
            if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
                desktop.setAboutHandler(app::handleAbout);
            }
            if (desktop.isSupported(Desktop.Action.APP_PREFERENCES)) {
                desktop.setPreferencesHandler(app::handlePreferences);
            }
        }

        List<URI> uris = new ArrayList<>(uriList);
        uriList.clear();
        return uris;
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
     * @param <C>
     *  the controller class
     * @param cls
     *  the application class
     * @param args
     *  the comand line arguments
     */
    public static
    <A extends FxApplication<A, C>, C extends FxController<A, C>>
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
                if (arg.length()>0) {
                    argL.add(arg.toString());
                }
                arg.setLength(0);
            }

            if (arg.length()>0) {
                arg.append(' ');
            }
            arg.append(s);
        }
        if (arg.length()>0) {
            argL.add(arg.toString());
        }

        LOG.info(() -> "arguments have been re-parsed!");
        LOG.fine(() -> "arguments: "+argL);
        
        return argL;
    }

}
