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

import casciian.TApplication;

/**
 * InfoBoxDialog displays a message without buttons (auto-close after display).
 */
public class InfoBoxDialog extends BaseDialog {

    /**
     * Construct a new info box dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public InfoBoxDialog(final TApplication application,
                         final DialogOptions options,
                         final DialogRunner runner) {
        super(application, options, runner);

        // Add the message text
        String text = options.getText();
        String[] lines = text.split("\n");

        int contentHeight = getHeight() - 4;
        int y = 1;
        for (String line : lines) {
            if (y <= contentHeight) {
                addLabel(line, 1, y);
                y++;
            }
        }

        // Auto-close after a short delay (like dialog's infobox)
        // The infobox is meant to be displayed briefly
        application.addTimer(500, false, new casciian.TAction() {
            public void DO() {
                closeOk("");
            }
        });
    }
}
