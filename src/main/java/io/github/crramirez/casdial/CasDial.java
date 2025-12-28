/*
 * Casdial - Dialog command compatible based on casciian
 *
 * Copyright 2025 Carlos Rafael Ramirez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.crramirez.casdial;

import java.io.PrintStream;

/**
 * CasDial - A clone of the Linux dialog command using casciian as the backend.
 * This provides Turbo Vision-like interfaces in bash scripts.
 */
public class CasDial {

    /**
     * Version string.
     */
    public static final String VERSION = "1.0.0";

    /**
     * Default constructor.
     */
    public CasDial() {}

    /**
     * Print version information.
     */
    private static void printVersion() {
        System.out.println("casDial version " + VERSION);
        System.out.println("Using casciian Text User Interface Library");
    }

    /**
     * Print help information.
     *
     * @param out the output stream
     */
    private static void printHelp(final PrintStream out) {
        out.println("casDial - A dialog clone using casciian TUI library");
        out.println();
        out.println("Usage: casDial [common-options] [box-options]");
        out.println();
        out.println("Common options:");
        out.println("  --title <title>           Set dialog title");
        out.println("  --backtitle <backtitle>   Set background title");
        out.println("  --clear                   Clear screen on exit");
        out.println("  --colors                  Interpret embedded colors");
        out.println("  --no-shadow               Suppress shadows");
        out.println("  --shadow                  Draw shadow around dialog");
        out.println("  --insecure                Make password dialog visible");
        out.println("  --no-cancel               Suppress cancel button");
        out.println("  --no-ok                   Suppress ok button");
        out.println("  --ok-label <text>         Override OK button label");
        out.println("  --cancel-label <text>     Override Cancel button label");
        out.println("  --yes-label <text>        Override Yes button label");
        out.println("  --no-label <text>         Override No button label");
        out.println("  --default-button <button> Set default button");
        out.println("  --default-item <item>     Set default item in menu/list");
        out.println("  --output-fd <fd>          Output to file descriptor");
        out.println("  --stdout                  Output to stdout");
        out.println("  --stderr                  Output to stderr (default)");
        out.println("  --separator <sep>         String to separate items");
        out.println("  --help                    Print this help message");
        out.println("  --version                 Print version information");
        out.println();
        out.println("Box options:");
        out.println("  --msgbox <text> <height> <width>");
        out.println("                            Display a message box");
        out.println("  --yesno <text> <height> <width>");
        out.println("                            Display a yes/no dialog");
        out.println("  --infobox <text> <height> <width>");
        out.println("                            Display an info box (auto-close)");
        out.println("  --inputbox <text> <height> <width> [init]");
        out.println("                            Display an input box");
        out.println("  --passwordbox <text> <height> <width> [init]");
        out.println("                            Display a password input box");
        out.println("  --textbox <file> <height> <width>");
        out.println("                            Display a text file");
        out.println("  --menu <text> <height> <width> <menu-height> <tag> <item>...");
        out.println("                            Display a menu");
        out.println("  --checklist <text> <height> <width> <list-height> <tag> <item> <status>...");
        out.println("                            Display a checklist");
        out.println("  --radiolist <text> <height> <width> <list-height> <tag> <item> <status>...");
        out.println("                            Display a radiolist");
        out.println("  --gauge <text> <height> <width> <percent>");
        out.println("                            Display a progress gauge");
        out.println("  --fselect <filepath> <height> <width>");
        out.println("                            Display a file selection dialog");
        out.println("  --dselect <dirpath> <height> <width>");
        out.println("                            Display a directory selection dialog");
        out.println("  --calendar <text> <height> <width> [day] [month] [year]");
        out.println("                            Display a calendar");
        out.println();
        out.println("Exit codes:");
        out.println("  0 - OK/Yes selected");
        out.println("  1 - Cancel/No selected");
        out.println("  2 - Help selected");
        out.println("  3 - Extra button selected");
        out.println("  255 - ESC pressed or error");
    }

    /**
     * Main entry point.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        if (args.length == 0) {
            printHelp(System.err);
            System.exit(255);
        }

        // Parse arguments and execute dialog
        DialogOptions options = new DialogOptions();

        try {
            options.parse(args);

            if (options.isHelp()) {
                printHelp(System.out);
                System.exit(0);
            }

            if (options.isVersion()) {
                printVersion();
                System.exit(0);
            }

            if (options.getDialogType() == null) {
                System.err.println("casDial: No dialog type specified");
                printHelp(System.err);
                System.exit(255);
            }

            // Execute the dialog
            DialogRunner runner = new DialogRunner(options);
            int exitCode = runner.run();
            System.exit(exitCode);

        } catch (DialogException e) {
            System.err.println("casDial: " + e.getMessage());
            System.exit(255);
        } catch (Exception e) {
            System.err.println("casDial: Error - " + e.getMessage());
            System.exit(255);
        }
    }
}
