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
import casciian.TDesktop;
import casciian.bits.CellAttributes;
import casciian.bits.Color;

/**
 * DialogApplication is the TApplication that hosts the dialog windows.
 */
public class DialogApplication extends TApplication {

    /**
     * The dialog options.
     */
    private final DialogOptions options;

    /**
     * The dialog runner to report results to.
     */
    private final DialogRunner runner;

    /**
     * Whether the application has finished.
     */
    private volatile boolean finished = false;

    /**
     * Construct with options.
     *
     * @param options the dialog options
     * @param runner the dialog runner
     * @throws Exception if there's an error creating the application
     */
    @SuppressWarnings("this-escape")
    public DialogApplication(final DialogOptions options, final DialogRunner runner)
            throws Exception {
        super(BackendType.XTERM);

        this.options = options;
        this.runner = runner;

        // Remove default desktop for cleaner look
        var desktop = new TDesktop(this);
        desktop.setBackgroundCell(null);
        setDesktop(desktop);

        // Hide menu bar and status bar for dialog mode
        setHideMenuBar(true);
        setHideStatusBar(true);

        // Set backtitle if provided
        if (options.getBacktitle() != null && !options.getBacktitle().isEmpty()) {
            getBackend().setTitle(options.getBacktitle());
        }

        // Create the appropriate dialog based on type
        createDialog();
    }

    /**
     * Create the dialog window based on the dialog type.
     *
     * @throws Exception if there's an error creating the dialog
     */
    private void createDialog() throws Exception {
        switch (options.getDialogType()) {
        case MSGBOX:
            new MsgBoxDialog(this, options, runner);
            break;

        case YESNO:
            new YesNoDialog(this, options, runner);
            break;

        case INFOBOX:
            new InfoBoxDialog(this, options, runner);
            break;

        case INPUTBOX:
            new InputBoxDialog(this, options, runner, false);
            break;

        case PASSWORDBOX:
            new InputBoxDialog(this, options, runner, true);
            break;

        case MENU:
            new MenuDialog(this, options, runner);
            break;

        case CHECKLIST:
            new ChecklistDialog(this, options, runner);
            break;

        case RADIOLIST:
            new RadiolistDialog(this, options, runner);
            break;

        case GAUGE:
            new GaugeDialog(this, options, runner);
            break;

        case FSELECT:
            new FileSelectDialog(this, options, runner, false);
            break;

        case DSELECT:
            new FileSelectDialog(this, options, runner, true);
            break;

        case CALENDAR:
            new CalendarDialog(this, options, runner);
            break;

        case TEXTBOX:
            new TextBoxDialog(this, options, runner);
            break;

        default:
            throw new DialogException("Unsupported dialog type: " + options.getDialogType());
        }
    }

    /**
     * Check if the application has finished.
     *
     * @return true if finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Called when we are done with the application.
     */
    @Override
    public void onExit() {
        finished = true;
        synchronized (runner) {
            runner.notifyAll();
        }

        restoreConsole();
    }

    /**
     * Draw the background with the backtitle if set.
     */
    @Override
    protected void onPreDraw() {
        super.onPreDraw();

        // Draw backtitle at the top if specified
        if (options.getBacktitle() != null && !options.getBacktitle().isEmpty()) {
            CellAttributes attr = new CellAttributes();
            attr.setForeColor(Color.WHITE);
            attr.setBackColor(Color.BLUE);
            attr.setBold(true);

            String bt = options.getBacktitle();
            int x = (getScreen().getWidth() - bt.length()) / 2;
            if (x < 0) {
                x = 0;
            }
            getScreen().putStringXY(x, 0, bt, attr);
        }
    }

    /**
     * Get the dialog options.
     *
     * @return the options
     */
    public DialogOptions getDialogOptions() {
        return options;
    }

    /**
     * Get the dialog runner.
     *
     * @return the runner
     */
    public DialogRunner getDialogRunner() {
        return runner;
    }
}
