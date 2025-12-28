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
import casciian.TRadioGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * RadiolistDialog displays a list with radio buttons.
 */
public class RadiolistDialog extends BaseDialog {

    /**
     * The radio group.
     */
    private final TRadioGroup radioGroup;

    /**
     * The tags for items.
     */
    private final List<String> tags;

    /**
     * Construct a new radiolist dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public RadiolistDialog(final TApplication application,
                           final DialogOptions options,
                           final DialogRunner runner) {
        super(application, options, runner);

        // Add the message text
        String text = options.getText();
        String[] lines = text.split("\n");

        int y = 1;
        for (String line : lines) {
            if (y < 3) {
                addLabel(line, 1, y);
                y++;
            }
        }

        // Build radio button items
        tags = new ArrayList<>();

        int listY = y + 1;
        int maxItems = getHeight() - listY - 5;
        int listHeight = Math.min(options.getMenuItems().size(), maxItems);

        // Create radio group
        radioGroup = addRadioGroup(1, listY, getWidth() - 4, "");

        int defaultSelection = -1;
        int idx = 0;
        for (String[] item : options.getMenuItems()) {
            if (idx >= maxItems) {
                break;
            }
            tags.add(item[0]);
            String label = item[0] + "  " + item[1];
            radioGroup.addRadioButton(label);
            if ("on".equalsIgnoreCase(item[2]) && defaultSelection < 0) {
                defaultSelection = idx + 1; // Radio buttons are 1-indexed
            }
            idx++;
        }

        // Set default selection
        if (defaultSelection > 0) {
            radioGroup.setSelected(defaultSelection);
        } else if (!tags.isEmpty()) {
            radioGroup.setSelected(1);
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
                public void DO() {
                    int selected = radioGroup.getSelected();
                    if (selected > 0 && selected <= tags.size()) {
                        closeOk(tags.get(selected - 1));
                    } else {
                        closeCancel();
                    }
                }
            });

            if (!options.isNoCancel()) {
                addButton(cancelLabel, startX + okWidth + 2, buttonY, new TAction() {
                    public void DO() {
                        closeCancel();
                    }
                });
            }
        } else if (!options.isNoCancel()) {
            int startX = (getWidth() - cancelWidth) / 2;
            addButton(cancelLabel, startX, buttonY, new TAction() {
                public void DO() {
                    closeCancel();
                }
            });
        }

        // Focus on the radio group
        activate(radioGroup);
    }
}
