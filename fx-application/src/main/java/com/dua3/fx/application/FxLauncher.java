package com.dua3.fx.application;

import com.dua3.utility.lang.Platform;
import javafx.application.Application;

import java.awt.*;
import java.awt.desktop.*;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Application launcher class.
 * 
 * @param <A>
 *  the application class
 * @param <C>
 *  the controller class
 */
public class FxLauncher<A extends FxApplication<A, C>, C extends FxController<A, C>>
        implements SystemEventListener, OpenFilesHandler, OpenURIHandler {

    /**
     * Logger
     */
    private static final Logger LOG = Logger.getLogger(FxLauncher.class.getName());

    public static final String PAR_FXLAUNCHER_ID = "fxlauncherid";
    
    private static Map<Integer, FxLauncher> LAUNCHERS = new ConcurrentHashMap<>();

    private static final AtomicInteger instanceCount = new AtomicInteger();
    
    private final int id;
    private final Deque<URI> uriList = new ConcurrentLinkedDeque<URI>();
    private final Class<A> appClass;

    public static Optional<FxLauncher> get(int id) {
        return Optional.ofNullable(LAUNCHERS.get(id));    
    }
    
    public FxLauncher(Class<A> cls) {
        this.id = instanceCount.incrementAndGet();
        this.appClass = cls;
        
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            desktop.addAppEventListener(this);
            if (desktop.isSupported(Desktop.Action.APP_OPEN_FILE)) {
                desktop.setOpenFileHandler(this);
            }
            if (desktop.isSupported(Desktop.Action.APP_OPEN_URI)) {
                desktop.setOpenURIHandler(this);
            }
        }
        
        LAUNCHERS.put(id, this);
    }
    
    @Override
    public void openFiles(OpenFilesEvent e) {
        e.getFiles().forEach(f -> uriList.add(f.toURI()));
    }

    @Override
    public void openURI(OpenURIEvent e) {
        uriList.add(e.getURI());
    }

    /**
     * Get the URIs to open.
     * @return collection containing the URI collected by the file and URI handlers
     */
    public Collection<URI> getUris() {
        return Collections.unmodifiableCollection(uriList);
    }

    /**
     * Get Launcher ID.
     * 
     * @return the ID of this launcher
     */
    public int id() {
        return id;
    }

    @Override
    public String toString() {
        return "FxLauncher{id=" + id + "}";
    }

    /**
     * Start application.
     * This method is a drop-in replacement for `Application.launch(cls, args)`.
     * <ul>
     *     <li><strong>Comand line arguments</strong> are re-parsed on windows, for details see 
     *     {@link #reparseCommandLine(String[])}.
     * </ul>
     *
     * @param args
     *  the comand line arguments
     */
    public void launch(String... args) {
        LOG.fine(() -> "arguments: "+Arrays.toString(args));

        // prepare arguments
        var reparsedArgs = reparseCommandLine(args);

        // add launcher-id
        List<String> newArgs = new ArrayList<>();
        newArgs.add(String.format("--%s=%d", PAR_FXLAUNCHER_ID, id()));
        newArgs.addAll(reparsedArgs);
        
        // launch
        Application.launch(appClass, newArgs.toArray(String[]::new));
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
    public static List<String> reparseCommandLine(String[] args) {
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
