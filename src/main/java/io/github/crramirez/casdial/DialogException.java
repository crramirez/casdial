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

import java.io.Serial;

/**
 * Exception thrown when there is an error in dialog processing.
 */
public class DialogException extends Exception {

    /**
     * Serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Construct with a message.
     *
     * @param message the error message
     */
    public DialogException(final String message) {
        super(message);
    }

    /**
     * Construct with a message and cause.
     *
     * @param message the error message
     * @param cause the underlying cause
     */
    public DialogException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
