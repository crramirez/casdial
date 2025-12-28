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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * TextBoxDialog displays the contents of a text file.
 */
public class TextBoxDialog extends BaseDialog {

    /**
     * Construct a new text box dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public TextBoxDialog(final TApplication application,
                         final DialogOptions options,
                         final DialogRunner runner) {
        super(application, options, runner);

        // Read the file content
        String content = "";
        String filePath = options.getFilePath();
        if (filePath != null && !filePath.isEmpty()) {
            try {
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    content = new String(Files.readAllBytes(file.toPath()));
                } else {
                    content = "Error: File not found: " + filePath;
                }
            } catch (IOException e) {
                content = "Error reading file: " + e.getMessage();
            }
        }

        // Add text widget
        int textWidth = getWidth() - 2;
        int textHeight = getHeight() - 6;

        /**
         * The text widget.
         */
        addText(content, 1, 1, textWidth, textHeight);

        // Add Exit button at the bottom
        String exitLabel = "Exit";
        int buttonWidth = exitLabel.length() + 4;
        int buttonX = (getWidth() - buttonWidth) / 2 - 1;
        int buttonY = getHeight() - 4;

        addButton(exitLabel, buttonX, buttonY, new TAction() {
            public void DO() {
                closeOk("");
            }
        });
    }
}
