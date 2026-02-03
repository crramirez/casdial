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

import casciian.bits.CellAttributes;
import casciian.bits.Color;
import casciian.bits.ColorTheme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for dialogrc configuration files.
 * Reads the default dialogrc_default resource file, then optionally
 * overrides with a custom file specified by the DIALOGRC environment variable.
 */
public final class DialogRcParser {

    /**
     * The name of the environment variable for custom dialogrc path.
     */
    public static final String DIALOGRC_ENV = "DIALOGRC";

    /**
     * Resource path for the default dialogrc file.
     */
    private static final String DEFAULT_DIALOGRC_RESOURCE = "/dialogrc_default";

    /**
     * Pattern to match attribute values: (foreground,background,highlight?,underline?,reverse?)
     */
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(
            "\\(\\s*([A-Za-z]+)\\s*,\\s*([A-Za-z]+)\\s*(?:,\\s*([A-Za-z]+))?(?:,\\s*([A-Za-z]+))?(?:,\\s*([A-Za-z]+))?\\s*\\)");

    /**
     * Maximum depth for resolving color references to prevent infinite loops.
     */
    private static final int MAX_REFERENCE_DEPTH = 10;

    /**
     * Pattern to match string values: "string"
     */
    private static final Pattern STRING_PATTERN = Pattern.compile("\"([^\"]*)\"");

    /**
     * Pattern to match key = value lines.
     */
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("^\\s*([a-z_][a-z0-9_]*)\\s*=\\s*(.+)\\s*$",
            Pattern.CASE_INSENSITIVE);

    /**
     * Private constructor to prevent instantiation.
     */
    private DialogRcParser() {
        // Utility class
    }

    /**
     * Load configuration from the default dialogrc_default resource,
     * then override with the file specified by DIALOGRC environment variable if set.
     *
     * <p><strong>Security note:</strong> The DIALOGRC environment variable specifies a file path
     * that will be read and parsed. Users should ensure this path points to a trusted location.
     * Path traversal validation is performed to prevent reading files outside of expected directories.</p>
     *
     * @return the parsed configuration
     */
    public static DialogRcConfig load() {
        DialogRcConfig config = new DialogRcConfig();

        // First, load the default configuration from resource
        loadDefaultResource(config);

        // Then, check for DIALOGRC environment variable
        String dialogrcPath = System.getenv(DIALOGRC_ENV);
        if (dialogrcPath != null && !dialogrcPath.isEmpty()) {
            Path path = Path.of(dialogrcPath);

            // Security: Validate path to prevent directory traversal attacks
            if (isValidDialogrcPath(path)) {
                loadFromFile(config, path);
            }
        }

        return config;
    }

    /**
     * Validate that the dialogrc path is safe to read.
     * Rejects paths that could be used for directory traversal or access sensitive files.
     *
     * @param path the path to validate
     * @return true if the path is valid and safe to read
     */
    private static boolean isValidDialogrcPath(final Path path) {
        try {
            // Normalize the path to resolve any .. or . components
            Path normalizedPath = path.toAbsolutePath().normalize();

            // Check if file exists and is readable
            if (!Files.exists(normalizedPath) || !Files.isReadable(normalizedPath)) {
                return false;
            }

            // Ensure it's a regular file, not a directory or special file
            if (!Files.isRegularFile(normalizedPath)) {
                return false;
            }

            // Check file size - reject suspiciously large files (> 1MB)
            long fileSize = Files.size(normalizedPath);
            if (fileSize > 1024 * 1024) {
                return false;
            }

            return true;
        } catch (IOException | SecurityException e) {
            return false;
        }
    }

    /**
     * Load the default dialogrc_default resource file.
     *
     * @param config the configuration to populate
     */
    private static void loadDefaultResource(final DialogRcConfig config) {
        try (InputStream is = DialogRcParser.class.getResourceAsStream(DEFAULT_DIALOGRC_RESOURCE)) {
            if (is != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    parseReader(config, reader);
                }
            }
        } catch (IOException e) {
            // Silently ignore - use defaults
        }
    }

    /**
     * Load configuration from a file, overriding existing values.
     *
     * @param config the configuration to populate
     * @param path the path to the file
     */
    private static void loadFromFile(final DialogRcConfig config, final Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            parseReader(config, reader);
        } catch (IOException e) {
            // Silently ignore - keep existing values
        }
    }

    /**
     * Parse the dialogrc content from a reader.
     *
     * @param config the configuration to populate
     * @param reader the reader to read from
     * @throws IOException if reading fails
     */
    private static void parseReader(final DialogRcConfig config, final BufferedReader reader) throws IOException {
        // First pass: collect raw values (needed for references like "dialog_color")
        Map<String, String> rawValues = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            // Skip comments and empty lines
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            Matcher matcher = KEY_VALUE_PATTERN.matcher(line);
            if (matcher.matches()) {
                String key = matcher.group(1).toLowerCase();
                String value = matcher.group(2).trim();
                rawValues.put(key, value);
            }
        }

        // Second pass: resolve values and populate config
        for (Map.Entry<String, String> entry : rawValues.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            parseKeyValue(config, key, value, rawValues);
        }
    }

    /**
     * Parse a key-value pair and apply it to the configuration.
     *
     * @param config the configuration
     * @param key the key
     * @param value the raw value
     * @param rawValues all raw values for reference resolution
     */
    private static void parseKeyValue(final DialogRcConfig config, final String key, final String value,
            final Map<String, String> rawValues) {
        switch (key) {
        case "aspect":
            config.setAspect(parseNumber(value));
            break;
        case "separate_widget":
            config.setSeparateWidget(parseString(value));
            break;
        case "tab_len":
            config.setTabLen(parseNumber(value));
            break;
        case "visit_items":
            config.setVisitItems(DialogRcConfig.parseBoolean(value));
            break;
        case "use_scrollbar":
            config.setUseScrollbar(DialogRcConfig.parseBoolean(value));
            break;
        case "use_shadow":
            config.setUseShadow(DialogRcConfig.parseBoolean(value));
            break;
        case "use_colors":
            config.setUseColors(DialogRcConfig.parseBoolean(value));
            break;
        default:
            // Try to parse as a color attribute
            if (key.endsWith("_color")) {
                CellAttributes attrs = parseAttribute(value, rawValues);
                if (attrs != null) {
                    config.setColor(key, attrs);
                }
            }
            break;
        }
    }

    /**
     * Parse a number value.
     *
     * @param value the string value
     * @return the parsed number, or 0 if invalid
     */
    private static int parseNumber(final String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Parse a string value (remove quotes).
     *
     * @param value the raw value
     * @return the parsed string
     */
    private static String parseString(final String value) {
        Matcher matcher = STRING_PATTERN.matcher(value);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return value.trim();
    }

    /**
     * Parse an attribute value, resolving references if needed.
     *
     * @param value the raw value
     * @param rawValues all raw values for reference resolution
     * @return the parsed CellAttributes, or null if invalid
     */
    private static CellAttributes parseAttribute(final String value, final Map<String, String> rawValues) {
        // Check if it's a reference to another color (e.g., "dialog_color")
        String resolved = value.trim();
        int depth = MAX_REFERENCE_DEPTH;
        while (!resolved.startsWith("(") && depth > 0) {
            String referenced = rawValues.get(resolved.toLowerCase());
            if (referenced == null) {
                break;
            }
            resolved = referenced.trim();
            depth--;
        }

        // Parse the attribute tuple
        Matcher matcher = ATTRIBUTE_PATTERN.matcher(resolved);
        if (!matcher.find()) {
            return null;
        }

        String fgStr = matcher.group(1);
        String bgStr = matcher.group(2);
        String highlightStr = matcher.group(3);
        String underlineStr = matcher.group(4);
        String reverseStr = matcher.group(5);

        Color fg = DialogRcConfig.parseColor(fgStr);
        Color bg = DialogRcConfig.parseColor(bgStr);

        if (fg == null || bg == null) {
            return null;
        }

        CellAttributes attrs = new CellAttributes();
        attrs.setForeColor(fg);
        attrs.setBackColor(bg);

        if (highlightStr != null) {
            attrs.setBold(DialogRcConfig.parseBoolean(highlightStr));
        }
        if (underlineStr != null) {
            attrs.setUnderline(DialogRcConfig.parseBoolean(underlineStr));
        }
        if (reverseStr != null) {
            attrs.setReverse(DialogRcConfig.parseBoolean(reverseStr));
        }

        return attrs;
    }

    /**
     * Apply the dialogrc configuration to a ColorTheme.
     * Maps dialogrc color names to casciian ColorTheme color names.
     *
     * @param theme the ColorTheme to modify
     * @param config the dialogrc configuration
     */
    public static void applyToTheme(final ColorTheme theme, final DialogRcConfig config) {
        if (theme == null || config == null) {
            return;
        }

        // Map dialogrc colors to casciian ColorTheme colors
        // Screen/Desktop
        applyColor(theme, ColorTheme.TDESKTOP_BACKGROUND, config.getColor("screen_color"));

        // Window/Dialog colors
        applyColor(theme, ColorTheme.TWINDOW_BACKGROUND, config.getColor("dialog_color"));
        applyColor(theme, ColorTheme.TWINDOW_BACKGROUND_MODAL, config.getColor("dialog_color"));

        // Window border: use title_color if available (since dialog shows title on border),
        // otherwise fall back to border_color
        CellAttributes titleColor = config.getColor("title_color");
        CellAttributes borderColor = config.getColor("border_color");
        CellAttributes effectiveBorderColor = (titleColor != null) ? titleColor : borderColor;
        applyColor(theme, ColorTheme.TWINDOW_BORDER, effectiveBorderColor);
        applyColor(theme, ColorTheme.TWINDOW_BORDER_MODAL, effectiveBorderColor);

        // Button colors
        applyColor(theme, ColorTheme.TBUTTON_ACTIVE, config.getColor("button_active_color"));
        applyColor(theme, ColorTheme.TBUTTON_INACTIVE, config.getColor("button_inactive_color"));
        applyColor(theme, ColorTheme.TBUTTON_MNEMONIC, config.getColor("button_key_active_color"));
        applyColor(theme, ColorTheme.TBUTTON_MNEMONIC_HIGHLIGHTED, config.getColor("button_label_active_color"));

        // Input/Field colors
        applyColor(theme, ColorTheme.TFIELD_INACTIVE, config.getColor("inputbox_color"));
        applyColor(theme, ColorTheme.TFIELD_ACTIVE, config.getColor("form_active_text_color"));

        // Menu colors
        applyColor(theme, ColorTheme.TMENU, config.getColor("menubox_color"));
        applyColor(theme, ColorTheme.TMENU_HIGHLIGHTED, config.getColor("item_selected_color"));

        // List colors
        applyColor(theme, ColorTheme.TLIST, config.getColor("item_color"));
        applyColor(theme, ColorTheme.TLIST_SELECTED, config.getColor("item_selected_color"));

        // Checkbox colors
        applyColor(theme, ColorTheme.TCHECKBOX_INACTIVE, config.getColor("check_color"));
        applyColor(theme, ColorTheme.TCHECKBOX_ACTIVE, config.getColor("check_selected_color"));

        // Radiobutton colors
        applyColor(theme, ColorTheme.TRADIOBUTTON_INACTIVE, config.getColor("tag_color"));
        applyColor(theme, ColorTheme.TRADIOBUTTON_ACTIVE, config.getColor("tag_selected_color"));

        // Progress bar / Gauge
        applyColor(theme, ColorTheme.TPROGRESSBAR_COMPLETE, config.getColor("gauge_color"));

        // Label
        applyColor(theme, ColorTheme.TLABEL, config.getColor("dialog_color"));
        applyColor(theme, ColorTheme.TTEXT, config.getColor("dialog_color"));

        // Calendar
        CellAttributes dialogColor = config.getColor("dialog_color");
        if (dialogColor != null) {
            applyColor(theme, ColorTheme.TCALENDAR_BACKGROUND, dialogColor);
            applyColor(theme, ColorTheme.TCALENDAR_DAY, dialogColor);
        }
        applyColor(theme, ColorTheme.TCALENDAR_DAY_SELECTED, config.getColor("item_selected_color"));
        applyColor(theme, ColorTheme.TCALENDAR_TITLE, config.getColor("title_color"));

        // TreeView (for file selection)
        applyColor(theme, ColorTheme.TTREEVIEW, config.getColor("menubox_color"));
        applyColor(theme, ColorTheme.TTREEVIEW_SELECTED, config.getColor("item_selected_color"));

        // Scroller
        applyColor(theme, ColorTheme.TSCROLLER_ARROWS, config.getColor("uarrow_color"));
    }

    /**
     * Apply a color to a theme if the color is not null.
     *
     * @param theme the theme
     * @param themeName the theme color name
     * @param attrs the attributes to apply
     */
    private static void applyColor(final ColorTheme theme, final String themeName, final CellAttributes attrs) {
        if (attrs != null) {
            theme.setColor(themeName, attrs);
        }
    }
}
