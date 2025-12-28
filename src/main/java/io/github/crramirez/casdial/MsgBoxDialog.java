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
import casciian.TText;

/**
 * MsgBoxDialog displays a message with an OK button.
 */
public class MsgBoxDialog extends BaseDialog {

    /**
     * Construct a new message box dialog.
     *
     * @param application the application
     * @param options     the dialog options
     * @param runner      the dialog runner
     */
    @SuppressWarnings("this-escape")
    public MsgBoxDialog(final TApplication application,
                        final DialogOptions options,
                        final DialogRunner runner) {
        super(application, options, runner);

        // Add the message text
        String text = options.getText();

        // Calculate content area
        int contentWidth = getWidth() - 3;
        int contentHeight = getHeight() - 4;

        TText tText = addText(text, 1, 0, contentWidth, contentHeight, "twindow.background.modal");
        tText.getHorizontalScroller().setVisible(false);
        tText.getVerticalScroller().setVisible(false);

        // Add OK button at the bottom center
        String okLabel = options.getOkLabel();
        int buttonWidth = okLabel.length() + 4;
        int buttonX = (getWidth() - buttonWidth) / 2 - 1;
        int buttonY = getHeight() - 4;

        addButton(okLabel, buttonX, buttonY, new TAction() {
            public void DO() {
                closeOk("");
            }
        });
    }
}
