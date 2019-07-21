// Copyright 2019 Axel Howind
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.dua3.fx.web;

import com.dua3.fx.util.Dialogs;
import javafx.scene.control.ButtonType;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebViews {
    private WebViews() {
        // utility class
    }

    private static final Logger LOG = Logger.getLogger(WebViews.class.getName());

    public static void setConfirmationHandler(WebEngine engine) {
        engine.setConfirmHandler(s -> Dialogs.confirmation().header("%s", s).buttons(ButtonType.YES, ButtonType.NO)
                .defaultButton(ButtonType.NO).showAndWait().filter(b -> b.equals(ButtonType.YES)).isPresent());
    }

    public static void setAlertHandler(WebEngine engine) {
        engine.setOnAlert(e -> Dialogs.warning().header("%s", e.getData()).showAndWait());
    }

    public static void setPromptHandler(WebEngine engine) {
        engine.setPromptHandler(p -> Dialogs.prompt().header("%s", p.getMessage())
                .defaultValue("%s", p.getDefaultValue()).showAndWait().orElse(""));
    }

    public static boolean setLogger(WebEngine engine, Logger logger) {
        JSObject win = (JSObject) engine.executeScript("window");
        win.setMember("javaLogger", new JSLogger(logger));
        String script = "(function () {\n"
                + "  console.error = (msg, ...data) => javaLogger.error(msg, 'data');\n"
                + "  console.warn = (msg, ...data) => javaLogger.warn(msg, 'data');\n"
                + "  console.info = (msg, ...data) => javaLogger.info(msg, 'data');\n"
                + "  console.log = function() { javaLogger.log(arguments) };\n"
                + "  console.debug = (msg, ...data) => javaLogger.debug(msg, 'data');\n"
                + "  console.log('logging initialised %s', 'success')\n"
                + "  return true\n"
                + "}) ();";
        Object ret = engine.executeScript(script);

        boolean success = Boolean.TRUE.equals(ret);

        if (!success) {
            LOG.warning("could not set logger for WebView instance");
        }

        return success;
    }

    public static void setupEngine(WebEngine engine, Logger logger) {
        setAlertHandler(engine);
        setConfirmationHandler(engine);
        setPromptHandler(engine);
        setLogger(engine, logger);
    }

    public static class JSLogger {
        private final Logger logger;

        public JSLogger(Logger logger) {
            this.logger = Objects.requireNonNull(logger);
        }

        private String formatMessage(JSObject args) {
            Object objLength = args.getMember("length");

            if (!(objLength instanceof Integer)) {
                return String.valueOf(args);
            }

            int length = (int) objLength;

            String msg = String.valueOf(args.getSlot(0));

            Object[] restArgs = new Object[Math.max(0, length - 1)];
            for (int i = 1; i < length; i++) {
                restArgs[i - 1] = args.getSlot(i);
            }

            return String.format(msg, restArgs);
        }

        public void error(JSObject args) {
            logger.log(Level.SEVERE, () -> formatMessage(args));
        }

        public void warn(JSObject args) {
            logger.log(Level.WARNING, () -> formatMessage(args));
        }

        public void info(JSObject args) {
            logger.log(Level.INFO, () -> formatMessage(args));
        }

        public void log(JSObject args) {
            logger.log(Level.FINE, () -> formatMessage(args));
        }

        public void debug(JSObject args) {
            logger.log(Level.FINER, () -> formatMessage(args));
        }
    }
}