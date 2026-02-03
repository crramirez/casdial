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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds the parsed configuration from a dialogrc file.
 * Contains both behavioral settings and color attributes.
 */
public class DialogRcConfig {

    // Behavioral settings
    private int aspect = 0;
    private String separateWidget = "";
    private int tabLen = 0;
    private boolean visitItems = false;
    private boolean useScrollbar = false;
    private boolean useShadow = true;
    private boolean useColors = true;

    // Color attributes map (key = dialogrc color name, value = resolved CellAttributes)
    private final Map<String, CellAttributes> colors = new HashMap<>();

    /**
     * Default constructor.
     */
    public DialogRcConfig() {
        // Initialize with defaults
    }

    /**
     * Get the aspect ratio setting.
     *
     * @return the aspect ratio
     */
    public int getAspect() {
        return aspect;
    }

    /**
     * Set the aspect ratio setting.
     *
     * @param aspect the aspect ratio
     */
    public void setAspect(final int aspect) {
        this.aspect = aspect;
    }

    /**
     * Get the separator widget string.
     *
     * @return the separator widget string
     */
    public String getSeparateWidget() {
        return separateWidget;
    }

    /**
     * Set the separator widget string.
     *
     * @param separateWidget the separator widget string
     */
    public void setSeparateWidget(final String separateWidget) {
        this.separateWidget = separateWidget;
    }

    /**
     * Get the tab length.
     *
     * @return the tab length
     */
    public int getTabLen() {
        return tabLen;
    }

    /**
     * Set the tab length.
     *
     * @param tabLen the tab length
     */
    public void setTabLen(final int tabLen) {
        this.tabLen = tabLen;
    }

    /**
     * Get whether to visit items during tab traversal.
     *
     * @return true if items should be visited
     */
    public boolean isVisitItems() {
        return visitItems;
    }

    /**
     * Set whether to visit items during tab traversal.
     *
     * @param visitItems true if items should be visited
     */
    public void setVisitItems(final boolean visitItems) {
        this.visitItems = visitItems;
    }

    /**
     * Get whether to use scrollbars.
     *
     * @return true if scrollbars should be used
     */
    public boolean isUseScrollbar() {
        return useScrollbar;
    }

    /**
     * Set whether to use scrollbars.
     *
     * @param useScrollbar true if scrollbars should be used
     */
    public void setUseScrollbar(final boolean useScrollbar) {
        this.useScrollbar = useScrollbar;
    }

    /**
     * Get whether to use shadows.
     *
     * @return true if shadows should be used
     */
    public boolean isUseShadow() {
        return useShadow;
    }

    /**
     * Set whether to use shadows.
     *
     * @param useShadow true if shadows should be used
     */
    public void setUseShadow(final boolean useShadow) {
        this.useShadow = useShadow;
    }

    /**
     * Get whether to use colors.
     *
     * @return true if colors should be used
     */
    public boolean isUseColors() {
        return useColors;
    }

    /**
     * Set whether to use colors.
     *
     * @param useColors true if colors should be used
     */
    public void setUseColors(final boolean useColors) {
        this.useColors = useColors;
    }

    /**
     * Get a color attribute by name.
     * Returns a defensive copy to prevent external modification.
     *
     * @param name the dialogrc color name
     * @return a copy of the CellAttributes, or null if not set
     */
    public CellAttributes getColor(final String name) {
        CellAttributes original = colors.get(name);
        return (original != null) ? new CellAttributes(original) : null;
    }

    /**
     * Set a color attribute.
     *
     * @param name the dialogrc color name
     * @param attributes the CellAttributes
     */
    public void setColor(final String name, final CellAttributes attributes) {
        colors.put(name, attributes);
    }

    /**
     * Get all color names as an unmodifiable map.
     * The returned map is a read-only view to prevent external modification.
     *
     * @return an unmodifiable map of color names to attributes
     */
    public Map<String, CellAttributes> getColors() {
        return Collections.unmodifiableMap(colors);
    }

    /**
     * Parse a color name string to a Color enum value.
     *
     * @param colorName the color name (e.g., "WHITE", "BLUE")
     * @return the Color enum value, or null if not recognized
     */
    public static Color parseColor(final String colorName) {
        if (colorName == null) {
            return null;
        }
        return switch (colorName.toUpperCase().trim()) {
            case "BLACK" -> Color.BLACK;
            case "RED" -> Color.RED;
            case "GREEN" -> Color.GREEN;
            case "YELLOW" -> Color.YELLOW;
            case "BLUE" -> Color.BLUE;
            case "MAGENTA" -> Color.MAGENTA;
            case "CYAN" -> Color.CYAN;
            case "WHITE" -> Color.WHITE;
            default -> null;
        };
    }

    /**
     * Parse a boolean value from ON/OFF string.
     *
     * @param value the string value
     * @return true if "ON", false otherwise
     */
    public static boolean parseBoolean(final String value) {
        return "ON".equalsIgnoreCase(value.trim());
    }
}
