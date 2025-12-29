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

/**
 * CasDial provides a clone of the Linux 'dialog' command using casciian
 * as the TUI backend. This enables bash scripts to display Turbo Vision-like
 * dialog boxes for user interaction.
 *
 * <p>Example usage:</p>
 * <pre>
 * casDial --title "Hello" --msgbox "Welcome to CasDial!" 10 40
 * casDial --yesno "Do you want to continue?" 10 40
 * result=$(casDial --inputbox "Enter your name:" 10 40 2&gt;&amp;1)
 * </pre>
 *
 * <p>Supported dialog types:</p>
 * <ul>
 * <li>msgbox - Display a message</li>
 * <li>yesno - Yes/No confirmation</li>
 * <li>infobox - Brief information display</li>
 * <li>inputbox - Text input</li>
 * <li>passwordbox - Password input</li>
 * <li>menu - Menu selection</li>
 * <li>checklist - Multiple selection with checkboxes</li>
 * <li>radiolist - Single selection with radio buttons</li>
 * <li>gauge - Progress bar</li>
 * <li>fselect - File selection</li>
 * <li>dselect - Directory selection</li>
 * <li>calendar - Date selection</li>
 * <li>textbox - Display text file contents</li>
 * </ul>
 */
package io.github.crramirez.casdial;
