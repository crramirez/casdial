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
import casciian.TCalendar;

import java.util.Calendar;

/**
 * CalendarDialog displays a calendar for date selection.
 * 
 * The calendar outputs dates in DD/MM/YYYY format to match the standard
 * Linux dialog command behavior.
 */
public class CalendarDialog extends BaseDialog {

    /**
     * The calendar widget.
     */
    private final TCalendar calendar;

    /**
     * Construct a new calendar dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     */
    @SuppressWarnings("this-escape")
    public CalendarDialog(final TApplication application,
                          final DialogOptions options,
                          final DialogRunner runner) {
        super(application, options, runner);

        // Add the message text
        String text = options.getText();
        if (!text.isEmpty()) {
            addLabel(text, 1, 1);
        }

        // Calculate calendar position
        int calX = (getWidth() - 28) / 2;
        if (calX < 1) {
            calX = 1;
        }
        int calY = 3;

        // Add calendar widget
        calendar = addCalendar(calX, calY, new TAction() {
            @Override
            public void DO() {
                // Date selected - close dialog
                Calendar cal = calendar.getValue();
                String result = String.format("%02d/%02d/%04d",
                        cal.get(Calendar.DAY_OF_MONTH),
                        cal.get(Calendar.MONTH) + 1,
                        cal.get(Calendar.YEAR));
                closeOk(result);
            }
        });

        // Set initial date if specified
        if (options.getYear() > 0) {
            Calendar cal = Calendar.getInstance();
            int year = options.getYear();
            int month = options.getMonth() > 0 ? options.getMonth() - 1 : cal.get(Calendar.MONTH);
            int day = options.getDay() > 0 ? options.getDay() : cal.get(Calendar.DAY_OF_MONTH);
            cal.set(year, month, day);
            calendar.setValue(cal);
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
                    Calendar cal = calendar.getValue();
                    String result = String.format("%02d/%02d/%04d",
                            cal.get(Calendar.DAY_OF_MONTH),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.YEAR));
                    closeOk(result);
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

        // Focus on the calendar
        activate(calendar);
    }
}
