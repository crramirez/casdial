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
import java.util.ArrayList;
import java.util.List;

/**
 * DialogOptions parses and holds the command line options for casDial.
 */
public class DialogOptions {

    /**
     * Dialog types supported by casDial.
     */
    public enum DialogType {
        MSGBOX,
        YESNO,
        INFOBOX,
        INPUTBOX,
        PASSWORDBOX,
        TEXTBOX,
        MENU,
        CHECKLIST,
        RADIOLIST,
        GAUGE,
        FSELECT,
        DSELECT,
        CALENDAR
    }

    // Common options
    private String title = "";
    private String backtitle = "";
    private boolean clearScreen = false;
    private boolean interpretColors = false;
    private boolean shadow = true;
    private boolean insecure = false;
    private boolean noCancel = false;
    private boolean noOk = false;
    private String okLabel = "OK";
    private String cancelLabel = "Cancel";
    private String yesLabel = "Yes";
    private String noLabel = "No";
    private String defaultButton = null;
    private String defaultItem = null;
    private PrintStream output = System.err;
    private String separator = "\n";
    private boolean help = false;
    private boolean version = false;

    // Dialog specific options
    private DialogType dialogType = null;
    private String text = "";
    private int height = 0;
    private int width = 0;
    private int listHeight = 0;
    private String initialValue = "";
    private int percentValue = 0;
    private String filePath = "";
    private int day = 0;
    private int month = 0;
    private int year = 0;

    // Menu/list items: stored as tag, item, [status] triples
    private final List<String[]> menuItems = new ArrayList<>();

    /**
     * Default constructor.
     */
    public DialogOptions() {}

    /**
     * Parse command line arguments.
     *
     * @param args the command line arguments
     * @throws DialogException if argument parsing fails
     */
    public void parse(final String[] args) throws DialogException {
        int i = 0;
        while (i < args.length) {
            String arg = args[i];

            switch (arg) {
            case "--help":
                help = true;
                i++;
                break;

            case "--version":
                version = true;
                i++;
                break;

            case "--title":
                i++;
                if (i >= args.length) {
                    throw new DialogException("--title requires an argument");
                }
                title = args[i];
                i++;
                break;

            case "--backtitle":
                i++;
                if (i >= args.length) {
                    throw new DialogException("--backtitle requires an argument");
                }
                backtitle = args[i];
                i++;
                break;

            case "--clear":
                clearScreen = true;
                i++;
                break;

            case "--colors":
                interpretColors = true;
                i++;
                break;

            case "--no-shadow":
                shadow = false;
                i++;
                break;

            case "--shadow":
                shadow = true;
                i++;
                break;

            case "--insecure":
                insecure = true;
                i++;
                break;

            case "--no-cancel", "--nocancel":
                noCancel = true;
                i++;
                break;

            case "--no-ok", "--nook":
                noOk = true;
                i++;
                break;

            case "--ok-label":
                i++;
                if (i >= args.length) {
                    throw new DialogException("--ok-label requires an argument");
                }
                okLabel = args[i];
                i++;
                break;

            case "--cancel-label":
                i++;
                if (i >= args.length) {
                    throw new DialogException("--cancel-label requires an argument");
                }
                cancelLabel = args[i];
                i++;
                break;

            case "--yes-label":
                i++;
                if (i >= args.length) {
                    throw new DialogException("--yes-label requires an argument");
                }
                yesLabel = args[i];
                i++;
                break;

            case "--no-label":
                i++;
                if (i >= args.length) {
                    throw new DialogException("--no-label requires an argument");
                }
                noLabel = args[i];
                i++;
                break;

            case "--default-button":
                i++;
                if (i >= args.length) {
                    throw new DialogException("--default-button requires an argument");
                }
                defaultButton = args[i];
                i++;
                break;

            case "--default-item":
                i++;
                if (i >= args.length) {
                    throw new DialogException("--default-item requires an argument");
                }
                defaultItem = args[i];
                i++;
                break;

            case "--output-fd":
                i++;
                if (i >= args.length) {
                    throw new DialogException("--output-fd requires an argument");
                }
                // For simplicity, we only support stdout (1) and stderr (2)
                int fd;
                try {
                    fd = Integer.parseInt(args[i]);
                } catch (NumberFormatException e) {
                    throw new DialogException("--output-fd requires a numeric argument");
                }
                if (fd == 1) {
                    output = System.out;
                } else if (fd == 2) {
                    output = System.err;
                } else {
                    throw new DialogException("--output-fd only supports values 1 (stdout) or 2 (stderr)");
                }
                i++;
                break;

            case "--stdout":
                output = System.out;
                i++;
                break;

            case "--stderr":
                output = System.err;
                i++;
                break;

            case "--separator", "--output-separator":
                i++;
                if (i >= args.length) {
                    throw new DialogException("--separator requires an argument");
                }
                separator = args[i];
                i++;
                break;

            case "--msgbox":
                dialogType = DialogType.MSGBOX;
                i = parseBasicBox(args, i + 1);
                break;

            case "--yesno":
                dialogType = DialogType.YESNO;
                i = parseBasicBox(args, i + 1);
                break;

            case "--infobox":
                dialogType = DialogType.INFOBOX;
                i = parseBasicBox(args, i + 1);
                break;

            case "--inputbox":
                dialogType = DialogType.INPUTBOX;
                i = parseInputBox(args, i + 1);
                break;

            case "--passwordbox":
                dialogType = DialogType.PASSWORDBOX;
                i = parseInputBox(args, i + 1);
                break;

            case "--textbox":
                dialogType = DialogType.TEXTBOX;
                i = parseTextBox(args, i + 1);
                break;

            case "--menu":
                dialogType = DialogType.MENU;
                i = parseMenuBox(args, i + 1, false);
                break;

            case "--checklist":
                dialogType = DialogType.CHECKLIST;
                i = parseMenuBox(args, i + 1, true);
                break;

            case "--radiolist":
                dialogType = DialogType.RADIOLIST;
                i = parseMenuBox(args, i + 1, true);
                break;

            case "--gauge":
            case "--mixedgauge":
                dialogType = DialogType.GAUGE;
                i = parseGaugeBox(args, i + 1);
                break;

            case "--fselect":
                dialogType = DialogType.FSELECT;
                i = parseFileBox(args, i + 1);
                break;

            case "--dselect":
                dialogType = DialogType.DSELECT;
                i = parseFileBox(args, i + 1);
                break;

            case "--calendar":
                dialogType = DialogType.CALENDAR;
                i = parseCalendarBox(args, i + 1);
                break;

            default:
                // Unknown option - skip for compatibility
                i++;
                break;
            }
        }
    }

    /**
     * Parse basic box arguments: text height width.
     *
     * @param args the arguments
     * @param start starting index
     * @return next index to process
     * @throws DialogException if parsing fails
     */
    private int parseBasicBox(final String[] args, final int start) throws DialogException {
        if (start + 2 >= args.length) {
            throw new DialogException("Box requires text, height, and width arguments");
        }
        text = sanitizeLineEnds(args[start]);
        height = parseIntArg(args[start + 1], "height");
        width = parseIntArg(args[start + 2], "width");
        return start + 3;
    }

    private static String sanitizeLineEnds(String text) {
        return text.replace("\\n", "\n");
    }

    /**
     * Parse input box arguments: text height width [init].
     *
     * @param args the arguments
     * @param start starting index
     * @return next index to process
     * @throws DialogException if parsing fails
     */
    private int parseInputBox(final String[] args, final int start) throws DialogException {
        if (start + 2 >= args.length) {
            throw new DialogException("Input box requires text, height, and width arguments");
        }
        text = sanitizeLineEnds(args[start]);
        height = parseIntArg(args[start + 1], "height");
        width = parseIntArg(args[start + 2], "width");

        int next = start + 3;
        if (next < args.length && !args[next].startsWith("--")) {
            initialValue = args[next];
            next++;
        }
        return next;
    }

    /**
     * Parse text box arguments: file height width.
     *
     * @param args the arguments
     * @param start starting index
     * @return next index to process
     * @throws DialogException if parsing fails
     */
    private int parseTextBox(final String[] args, final int start) throws DialogException {
        if (start + 2 >= args.length) {
            throw new DialogException("Text box requires file, height, and width arguments");
        }
        filePath = args[start];
        height = parseIntArg(args[start + 1], "height");
        width = parseIntArg(args[start + 2], "width");
        return start + 3;
    }

    /**
     * Parse menu box arguments: text height width menu-height tag item...
     *
     * @param args the arguments
     * @param start starting index
     * @param hasStatus whether items have a status field (checklist/radiolist)
     * @return next index to process
     * @throws DialogException if parsing fails
     */
    private int parseMenuBox(final String[] args, final int start, final boolean hasStatus)
            throws DialogException {
        if (start + 3 >= args.length) {
            throw new DialogException("Menu box requires text, height, width, and menu-height arguments");
        }
        text = sanitizeLineEnds(args[start]);
        height = parseIntArg(args[start + 1], "height");
        width = parseIntArg(args[start + 2], "width");
        listHeight = parseIntArg(args[start + 3], "menu-height");

        int next = start + 4;
        int itemSize = hasStatus ? 3 : 2;

        while (next + itemSize <= args.length) {
            // Check if next arg is an option
            if (args[next].startsWith("--")) {
                break;
            }

            String[] item;
            if (hasStatus) {
                item = new String[] { args[next], args[next + 1], args[next + 2] };
            } else {
                item = new String[] { args[next], args[next + 1], "off" };
            }
            menuItems.add(item);
            next += itemSize;
        }
        return next;
    }

    /**
     * Parse gauge box arguments: text height width percent.
     *
     * @param args the arguments
     * @param start starting index
     * @return next index to process
     * @throws DialogException if parsing fails
     */
    private int parseGaugeBox(final String[] args, final int start) throws DialogException {
        if (start + 3 >= args.length) {
            throw new DialogException("Gauge box requires text, height, width, and percent arguments");
        }
        text = sanitizeLineEnds(args[start]);
        height = parseIntArg(args[start + 1], "height");
        width = parseIntArg(args[start + 2], "width");
        percentValue = parseIntArg(args[start + 3], "percent");
        return start + 4;
    }

    /**
     * Parse file selection box arguments: filepath height width.
     *
     * @param args the arguments
     * @param start starting index
     * @return next index to process
     * @throws DialogException if parsing fails
     */
    private int parseFileBox(final String[] args, final int start) throws DialogException {
        if (start + 2 >= args.length) {
            throw new DialogException("File box requires filepath, height, and width arguments");
        }
        filePath = args[start];
        height = parseIntArg(args[start + 1], "height");
        width = parseIntArg(args[start + 2], "width");
        return start + 3;
    }

    /**
     * Parse calendar box arguments: text height width [day] [month] [year].
     *
     * @param args the arguments
     * @param start starting index
     * @return next index to process
     * @throws DialogException if parsing fails
     */
    private int parseCalendarBox(final String[] args, final int start) throws DialogException {
        if (start + 2 >= args.length) {
            throw new DialogException("Calendar box requires text, height, and width arguments");
        }
        text = sanitizeLineEnds(args[start]);
        height = parseIntArg(args[start + 1], "height");
        width = parseIntArg(args[start + 2], "width");

        int next = start + 3;

        // Optional day, month, year
        if (next < args.length && !args[next].startsWith("--")) {
            day = parseIntArg(args[next], "day");
            next++;
        }
        if (next < args.length && !args[next].startsWith("--")) {
            month = parseIntArg(args[next], "month");
            next++;
        }
        if (next < args.length && !args[next].startsWith("--")) {
            year = parseIntArg(args[next], "year");
            next++;
        }
        return next;
    }

    /**
     * Parse an integer argument.
     *
     * @param value the string value
     * @param name the argument name for error messages
     * @return the integer value
     * @throws DialogException if parsing fails
     */
    private int parseIntArg(final String value, final String name) throws DialogException {
        final int parsed;
        try {
            parsed = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new DialogException("Invalid " + name + " value: " + value);
        }

        // Basic range validation for critical numeric arguments.
        if ("height".equals(name) || "width".equals(name)) {
            // Allow 0 to indicate auto-sizing; reject only negative values.
            if (parsed < 0) {
                throw new DialogException("Invalid " + name + " value (must be >= 0, where 0 enables auto-sizing): " + value);
            }
        } else if ("day".equals(name)) {
            if (parsed < 1 || parsed > 31) {
                throw new DialogException("Invalid day value (must be between 1 and 31): " + value);
            }
        } else if ("month".equals(name)) {
            if (parsed < 1 || parsed > 12) {
                throw new DialogException("Invalid month value (must be between 1 and 12): " + value);
            }
        } else if ("year".equals(name)) {
            if (parsed < 1900 || parsed > 2100) {
                throw new DialogException("Invalid year value (must be between 1900 and 2100): " + value);
            }
        } else if ("percent".equals(name)) {
            if (parsed < 0 || parsed > 100) {
                throw new DialogException("Invalid percent value (must be between 0 and 100): " + value);
            }
        } else if ("menu-height".equals(name)) {
            if (parsed <= 0) {
                throw new DialogException("Invalid menu-height value (must be > 0): " + value);
            }
        }

        return parsed;
    }

    // Getters

    /**
     * Get the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the backtitle.
     *
     * @return the backtitle
     */
    public String getBacktitle() {
        return backtitle;
    }

    /**
     * Get whether to clear screen on exit.
     *
     * @return true if screen should be cleared
     */
    public boolean isClearScreen() {
        return clearScreen;
    }

    /**
     * Get whether to interpret embedded colors.
     *
     * @return true if colors should be interpreted
     */
    public boolean isInterpretColors() {
        return interpretColors;
    }

    /**
     * Get whether shadow is enabled.
     *
     * @return true if shadow is enabled
     */
    public boolean isShadow() {
        return shadow;
    }

    /**
     * Get whether password is visible.
     *
     * @return true if password is visible
     */
    public boolean isInsecure() {
        return insecure;
    }

    /**
     * Get whether cancel button is suppressed.
     *
     * @return true if cancel is suppressed
     */
    public boolean isNoCancel() {
        return noCancel;
    }

    /**
     * Get whether OK button is suppressed.
     *
     * @return true if OK is suppressed
     */
    public boolean isNoOk() {
        return noOk;
    }

    /**
     * Get the OK button label.
     *
     * @return the OK label
     */
    public String getOkLabel() {
        return okLabel;
    }

    /**
     * Get the Cancel button label.
     *
     * @return the Cancel label
     */
    public String getCancelLabel() {
        return cancelLabel;
    }

    /**
     * Get the Yes button label.
     *
     * @return the Yes label
     */
    public String getYesLabel() {
        return yesLabel;
    }

    /**
     * Get the No button label.
     *
     * @return the No label
     */
    public String getNoLabel() {
        return noLabel;
    }

    /**
     * Get the default button.
     *
     * @return the default button
     */
    public String getDefaultButton() {
        return defaultButton;
    }

    /**
     * Get the default item.
     *
     * @return the default item
     */
    public String getDefaultItem() {
        return defaultItem;
    }

    /**
     * Get the output stream.
     *
     * @return the output stream
     */
    public PrintStream getOutput() {
        return output;
    }

    /**
     * Get the separator.
     *
     * @return the separator
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Get whether help was requested.
     *
     * @return true if help was requested
     */
    public boolean isHelp() {
        return help;
    }

    /**
     * Get whether version was requested.
     *
     * @return true if version was requested
     */
    public boolean isVersion() {
        return version;
    }

    /**
     * Get the dialog type.
     *
     * @return the dialog type
     */
    public DialogType getDialogType() {
        return dialogType;
    }

    /**
     * Get the text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Get the height.
     *
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the width.
     *
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the list height.
     *
     * @return the list height
     */
    public int getListHeight() {
        return listHeight;
    }

    /**
     * Get the initial value.
     *
     * @return the initial value
     */
    public String getInitialValue() {
        return initialValue;
    }

    /**
     * Get the percent value.
     *
     * @return the percent value
     */
    public int getPercentValue() {
        return percentValue;
    }

    /**
     * Get the file path.
     *
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Get the day.
     *
     * @return the day
     */
    public int getDay() {
        return day;
    }

    /**
     * Get the month.
     *
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * Get the year.
     *
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * Get the menu items.
     *
     * @return the menu items
     */
    public List<String[]> getMenuItems() {
        return menuItems;
    }
}
