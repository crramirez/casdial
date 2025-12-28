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
import casciian.TField;

/**
 * InputBoxDialog displays a text input field with OK and Cancel buttons.
 */
public class InputBoxDialog extends BaseDialog {

    /**
     * The input field.
     */
    private final TField inputField;

    /**
     * Construct a new input box dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     * @param isPassword true for password input (hidden characters)
     */
    @SuppressWarnings("this-escape")
    public InputBoxDialog(final TApplication application,
                          final DialogOptions options,
                          final DialogRunner runner,
                          final boolean isPassword) {
        super(application, options, runner);

        // Add the message text
        String text = options.getText();
        String[] lines = text.split("\n");

        int y = 1;
        for (String line : lines) {
            if (y < getHeight() - 6) {
                addLabel(line, 1, y);
                y++;
            }
        }

        // Add input field
        int fieldY = getHeight() - 6;
        int fieldWidth = getWidth() - 4;

        if (isPassword && !options.isInsecure()) {
            inputField = addPasswordField(1, fieldY, fieldWidth, false,
                    options.getInitialValue(), new TAction() {
                        @Override
                        public void DO() {
                            closeOk(inputField.getText());
                        }
                    }, null);
        } else {
            inputField = addField(1, fieldY, fieldWidth, false,
                    options.getInitialValue(), new TAction() {
                        @Override
                        public void DO() {
                            closeOk(inputField.getText());
                        }
                    }, null);
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
                    closeOk(inputField.getText());
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

        // Focus on the input field
        activate(inputField);
    }
}
