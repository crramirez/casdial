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
import casciian.TButton;
import casciian.TText;

/**
 * YesNoDialog displays a question with Yes and No buttons.
 */
public class YesNoDialog extends BaseDialog {

    /**
     * Construct a new yes/no dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public YesNoDialog(final TApplication application,
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

        // Add Yes and No buttons at the bottom
        String yesLabel = options.getYesLabel();
        String noLabel = options.getNoLabel();

        int yesWidth = yesLabel.length() + 4;
        int noWidth = noLabel.length() + 4;
        int totalWidth = yesWidth + noWidth + 4;
        int startX = (getWidth() - totalWidth) / 2;
        int buttonY = getHeight() - 4;

        TButton yesButton = addButton(yesLabel, startX, buttonY, new TAction() {
            @Override
            public void DO() {
                closeOk("");
            }
        });

        addButton(noLabel, startX + yesWidth + 2, buttonY, new TAction() {
            @Override
            public void DO() {
                closeCancel();
            }
        });

        // Set default button focus. When "no" is the default, rely on tab order for No.
        if (!"no".equalsIgnoreCase(options.getDefaultButton())) {
            activate(yesButton);
        }
    }
}
