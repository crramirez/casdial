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

import casciian.TAction;
import casciian.TApplication;
import casciian.TList;

import java.util.ArrayList;
import java.util.List;

/**
 * MenuDialog displays a menu with selectable items.
 */
public class MenuDialog extends BaseDialog {

    /**
     * The list widget.
     */
    private final TList menuList;

    /**
     * The tags for menu items.
     */
    private final List<String> tags;

    /**
     * Construct a new menu dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public MenuDialog(final TApplication application,
                      final DialogOptions options,
                      final DialogRunner runner) {
        super(application, options, runner);

        // Add the message text
        String text = options.getText();
        String[] lines = text.split("\n");

        int y = 0;
        for (String line : lines) {
            if (y < 3) {
                addLabel(line, 1, y, "twindow.background.modal");
                y++;
            }
        }

        if (options.getHeight() == 0) {
            int windowHeight = Math.min(y + options.getMenuItems().size() + 10, application.getScreen().getHeight() - 2);
            setHeight(windowHeight);
            center();
        }

        // Build list items from menu items
        tags = new ArrayList<>();
        List<String> displayItems = new ArrayList<>();

        // Calculate maximum width of item[0] (tag)
        int maxTagWidth = options.getMenuItems().stream()
                .map(a -> a[0])
                .mapToInt(String::length)
                .max()
                .orElse(0);

        for (String[] item : options.getMenuItems()) {
            tags.add(item[0]);
            // Format: tag - description
            String format = " %-" + maxTagWidth + "s  %s";
            displayItems.add(String.format(format, item[0], item[1]));
        }

        // Calculate list dimensions
        int listY = y + 1;
        int listHeight = options.getListHeight();
        if (listHeight <= 0) {
            listHeight = getHeight() - listY - 6;
        }
        if (listHeight < 3) {
            listHeight = 3;
        }
        int listWidth = getWidth() - 4;

        // Add the menu list
        menuList = addList(displayItems, 1, listY, listWidth, listHeight + 1,
                new TAction() {
                    @Override
                    public void DO() {
                        // Enter pressed on item
                        int idx = menuList.getSelectedIndex();
                        if (idx >= 0 && idx < tags.size()) {
                            closeOk(tags.get(idx));
                        }
                    }
                },
                new TAction() {
                    @Override
                    public void DO() {
                        // Move action (optional)
                    }
                });

        menuList.getHorizontalScroller().setVisible(false);
        if (listHeight >= options.getMenuItems().size()) {
            menuList.getVerticalScroller().setVisible(false);
            menuList.setWidth(menuList.getWidth() + 1);
        }

        // Set default item if specified
        if (options.getDefaultItem() != null) {
            for (int i = 0; i < tags.size(); i++) {
                if (tags.get(i).equals(options.getDefaultItem())) {
                    menuList.setSelectedIndex(i);
                    break;
                }
            }
        } else if (!tags.isEmpty()) {
            menuList.setSelectedIndex(0);
        }

        // Add OK and Cancel buttons at the bottom
        String okLabel = options.getOkLabel();
        String cancelLabel = options.getCancelLabel();

        int okWidth = okLabel.length() + 4;
        int cancelWidth = cancelLabel.length() + 4;
        int buttonY = getHeight() - 4;

        if (!options.isNoOk()) {
            int totalWidth = okWidth;
            if (!options.isNoCancel()) {
                totalWidth += cancelWidth + 2;
            }
            int startX = (getWidth() - totalWidth) / 2;

            addButton(okLabel, startX, buttonY, new TAction() {
                @Override
                public void DO() {
                    int idx = menuList.getSelectedIndex();
                    if (idx >= 0 && idx < tags.size()) {
                        closeOk(tags.get(idx));
                    } else {
                        closeCancel();
                    }
                }
            });

            if (!options.isNoCancel()) {
                addButton(cancelLabel, startX + okWidth + 2, buttonY, new TAction() {
                    @Override
                    public void DO() {
                        closeCancel();
                    }
                });
            }
        } else if (!options.isNoCancel()) {
            int startX = (getWidth() - cancelWidth) / 2;
            addButton(cancelLabel, startX, buttonY, new TAction() {
                @Override
                public void DO() {
                    closeCancel();
                }
            });
        }

        // Focus on the list
        activate(menuList);
    }
}
