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
import casciian.TLabel;
import casciian.TProgressBar;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * GaugeDialog displays a progress bar that can be updated from stdin.
 */
public class GaugeDialog extends BaseDialog {

    /**
     * The progress bar widget.
     */
    private final TProgressBar progressBar;

    /**
     * Current percentage.
     */
    private int percent;

    /**
     * Whether to keep reading from stdin.
     */
    private volatile boolean running = true;

    /**
     * The reader thread for stdin updates.
     */
    private Thread readerThread;

    /**
     * Construct a new gauge dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public GaugeDialog(final TApplication application,
                       final DialogOptions options,
                       final DialogRunner runner) {
        super(application, options, runner);

        // Add the message text
        String text = options.getText();
        // The label showing the text.
        addLabel(text, 1, 1);

        // Add progress bar
        int barY = getHeight() - 5;
        int barWidth = getWidth() - 4;
        percent = options.getPercentValue();

        progressBar = addProgressBar(1, barY, barWidth, percent);

        // The percentage label.
        addLabel(percent + "%", (getWidth() - 4) / 2, barY + 1);

        // Start a thread to read from stdin for updates
        readerThread = new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                String line;
                while (running && (line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("XXX")) {
                        // Start of new text block
                        StringBuilder newText = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            if (line.trim().equals("XXX")) {
                                break;
                            }
                            if (newText.length() > 0) {
                                newText.append("\n");
                            }
                            newText.append(line);
                        }
                        // Update text label (would need to be on UI thread)
                        // For simplicity, we just ignore text updates
                    } else {
                        try {
                            int newPercent = Integer.parseInt(line);
                            if (newPercent >= 0 && newPercent <= 100) {
                                percent = newPercent;
                                progressBar.setValue(percent);
                                if (percent >= 100) {
                                    // Auto-close when complete
                                    application.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            closeOk("");
                                        }
                                    });
                                    break;
                                }
                            }
                        } catch (NumberFormatException e) {
                            // Ignore non-numeric input
                        }
                    }
                }
            } catch (Exception e) {
                // Reader closed or error
            } finally {
                // Note: We don't close the reader since it wraps System.in
                // which should not be closed
            }
        });
        readerThread.setDaemon(true);
        readerThread.start();
    }

    /**
     * Called when the window is closed.
     */
    @Override
    public void close() {
        running = false;
        // Interrupt the reader thread to unblock readLine()
        if (readerThread != null && readerThread.isAlive()) {
            readerThread.interrupt();
        }
        super.close();
    }
}
