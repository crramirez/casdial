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

/**
 * DialogRunner executes the dialog based on the parsed options.
 */
public class DialogRunner {

    /**
     * Exit code for OK/Yes.
     */
    public static final int EXIT_OK = 0;

    /**
     * Exit code for Cancel/No.
     */
    public static final int EXIT_CANCEL = 1;

    /**
     * Exit code for Help.
     */
    public static final int EXIT_HELP = 2;

    /**
     * Exit code for Extra button.
     */
    public static final int EXIT_EXTRA = 3;

    /**
     * Exit code for ESC or error.
     */
    public static final int EXIT_ESC = 255;

    /**
     * The dialog options.
     */
    private final DialogOptions options;

    /**
     * The result from the dialog.
     */
    private String result = "";

    /**
     * The exit code.
     */
    private int exitCode = EXIT_ESC;

    /**
     * Construct with options.
     *
     * @param options the dialog options
     */
    public DialogRunner(final DialogOptions options) {
        this.options = options;
    }

    /**
     * Run the dialog.
     *
     * @return the exit code
     * @throws Exception if there's an error running the dialog
     */
    public int run() throws Exception {
        try {
            DialogApplication app = new DialogApplication(options, this);
            (new Thread(app)).start();

            // Wait for the application to finish using proper wait/notify
            synchronized (this) {
                while (!app.isFinished()) {
                    wait();
                }
            }

            app.restoreConsole();

            // Output the result to the configured output stream
            if (!result.isEmpty()) {
                options.getOutput().print(result);
            }

            return exitCode;

        } catch (Exception e) {
            throw new DialogException("Error running dialog: " + e.getMessage(), e);
        }
    }

    /**
     * Set the result.
     *
     * @param result the result string
     */
    public void setResult(final String result) {
        this.result = result;
    }

    /**
     * Get the result.
     *
     * @return the result string
     */
    public String getResult() {
        return result;
    }

    /**
     * Set the exit code.
     *
     * @param exitCode the exit code
     */
    public void setExitCode(final int exitCode) {
        this.exitCode = exitCode;
    }

    /**
     * Get the exit code.
     *
     * @return the exit code
     */
    public int getExitCode() {
        return exitCode;
    }
}
