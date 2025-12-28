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
import casciian.TWindow;
import casciian.event.TKeypressEvent;

import static casciian.TKeypress.*;

/**
 * BaseDialog is the base class for all casDial dialog windows.
 */
public abstract class BaseDialog extends TWindow {

    /**
     * The dialog options.
     */
    protected final DialogOptions options;

    /**
     * The dialog runner to report results to.
     */
    protected final DialogRunner runner;

    /**
     * Construct a new dialog window.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    protected BaseDialog(final TApplication application,
                         final DialogOptions options,
                         final DialogRunner runner) {
        super(application, options.getTitle(),
              0, 0,
              calculateWidth(options, application),
              calculateHeight(options, application),
              CENTERED | MODAL | NOCLOSEBOX);

        this.options = options;
        this.runner = runner;
    }

    /**
     * Calculate the dialog width.
     *
     * @param options the dialog options
     * @param app the application
     * @return the width
     */
    protected static int calculateWidth(final DialogOptions options,
                                        final TApplication app) {
        int width = options.getWidth();
        if (width <= 0) {
            // Auto width: use 80% of screen width
            width = (app.getScreen().getWidth() * 80) / 100;
        }
        // Ensure minimum width
        if (width < 20) {
            width = 20;
        }
        // Cap at screen width
        if (width > app.getScreen().getWidth() - 2) {
            width = app.getScreen().getWidth() - 2;
        }
        return width;
    }

    /**
     * Calculate the dialog height.
     *
     * @param options the dialog options
     * @param app the application
     * @return the height
     */
    protected static int calculateHeight(final DialogOptions options,
                                         final TApplication app) {
        int height = options.getHeight();
        if (height <= 0) {
            // Auto height: use 80% of screen height
            height = (app.getScreen().getHeight() * 80) / 100;
        }
        // Ensure minimum height
        if (height < 8) {
            height = 8;
        }
        // Cap at screen height
        if (height > app.getScreen().getHeight() - 2) {
            height = app.getScreen().getHeight() - 2;
        }
        return height;
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {
        if (keypress.equals(kbEsc)) {
            // ESC pressed - cancel
            runner.setExitCode(DialogRunner.EXIT_ESC);
            getApplication().closeWindow(this);
            getApplication().exit();
            return;
        }
        super.onKeypress(keypress);
    }

    /**
     * Close with OK result.
     *
     * @param result the result string
     */
    protected void closeOk(final String result) {
        runner.setResult(result);
        runner.setExitCode(DialogRunner.EXIT_OK);
        getApplication().closeWindow(this);
        getApplication().exit();
    }

    /**
     * Close with Cancel result.
     */
    protected void closeCancel() {
        runner.setExitCode(DialogRunner.EXIT_CANCEL);
        getApplication().closeWindow(this);
        getApplication().exit();
    }

    /**
     * Close with ESC/error result.
     */
    protected void closeEsc() {
        runner.setExitCode(DialogRunner.EXIT_ESC);
        getApplication().closeWindow(this);
        getApplication().exit();
    }
}
