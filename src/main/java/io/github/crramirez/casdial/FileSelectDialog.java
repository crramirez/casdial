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

import casciian.*;

import java.io.File;
import java.io.IOException;

/**
 * FileSelectDialog displays a file or directory selection dialog.
 */
public class FileSelectDialog extends BaseDialog {

    /**
     * The directory tree view.
     */
    private final TTreeViewScrollable treeView;

    /**
     * The directory list.
     */
    private final TDirectoryList directoryList;

    /**
     * The path entry field.
     */
    private final TField pathField;

    /**
     * Whether this is a directory-only selection.
     */
    private final boolean directoryOnly;

    /**
     * Construct a new file select dialog.
     *
     * @param application the application
     * @param options the dialog options
     * @param runner the dialog runner
     * @param directoryOnly true if only directories can be selected
     */
    @SuppressWarnings("this-escape")
    public FileSelectDialog(final TApplication application,
                            final DialogOptions options,
                            final DialogRunner runner,
                            final boolean directoryOnly) {
        super(application, options, runner);

        this.directoryOnly = directoryOnly;

        String startPath = options.getFilePath();
        if (startPath == null || startPath.isEmpty()) {
            startPath = System.getProperty("user.dir");
        }

        // Canonicalize the path
        try {
            startPath = new File(startPath).getCanonicalPath();
        } catch (IOException e) {
            startPath = System.getProperty("user.dir");
        }

        // Add path entry field at the top
        pathField = addField(1, 1, getWidth() - 4, false, startPath,
                new TAction() {
                    public void DO() {
                        checkPath();
                    }
                }, null);

        // Calculate dimensions for tree and list
        int treeWidth = (getWidth() - 4) / 2;
        int listWidth = getWidth() - 4 - treeWidth - 1;
        int contentHeight = getHeight() - 8;

        // Add directory tree view
        final String initialPath = startPath;
        treeView = addTreeViewWidget(1, 3, treeWidth, contentHeight,
                new TAction() {
                    public void DO() {
                        TTreeItem item = treeView.getSelected();
                        if (item instanceof TDirectoryTreeItem) {
                            File selectedDir = ((TDirectoryTreeItem) item).getFile();
                            try {
                                String path = selectedDir.getCanonicalPath();
                                pathField.setText(path);
                                directoryList.setPath(path);
                            } catch (IOException e) {
                                // Ignore
                            }
                        }
                    }
                });

        try {
            new TDirectoryTreeItem(treeView, initialPath, true);
        } catch (Exception e) {
            // Failed to create tree - use current directory
            try {
                new TDirectoryTreeItem(treeView, System.getProperty("user.dir"), true);
            } catch (Exception e2) {
                // Ignore
            }
        }

        // Add directory list
        directoryList = addDirectoryList(initialPath, treeWidth + 2, 3,
                listWidth, contentHeight,
                new TAction() {
                    public void DO() {
                        // Double-click - select the file/directory
                        try {
                            File selected = directoryList.getPath();
                            if (selected != null) {
                                if (directoryOnly && !selected.isDirectory()) {
                                    // Change to that directory's parent
                                    pathField.setText(selected.getParent());
                                } else {
                                    pathField.setText(selected.getCanonicalPath());
                                    checkPath();
                                }
                            }
                        } catch (IOException e) {
                            // Ignore
                        }
                    }
                },
                new TAction() {
                    public void DO() {
                        // Single-click - update path field
                        try {
                            File selected = directoryList.getPath();
                            if (selected != null) {
                                pathField.setText(selected.getCanonicalPath());
                            }
                        } catch (IOException e) {
                            // Ignore
                        }
                    }
                }, null);

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
                public void DO() {
                    checkPath();
                }
            });

            if (!options.isNoCancel()) {
                addButton(cancelLabel, startX + okWidth + 2, buttonY, new TAction() {
                    public void DO() {
                        closeCancel();
                    }
                });
            }
        } else if (!options.isNoCancel()) {
            int startX = (getWidth() - cancelWidth) / 2;
            addButton(cancelLabel, startX, buttonY, new TAction() {
                public void DO() {
                    closeCancel();
                }
            });
        }

        // Focus on the directory list
        activate(directoryList);
    }

    /**
     * Check the current path and close if valid.
     */
    private void checkPath() {
        String path = pathField.getText();
        File file = new File(path);

        if (directoryOnly) {
            // Must be a directory
            if (file.isDirectory()) {
                closeOk(path);
            } else if (file.exists()) {
                // It's a file - use its parent directory
                directoryList.setPath(file.getParent());
                pathField.setText(file.getParent());
            }
        } else {
            // Can be file or directory
            if (file.exists()) {
                if (file.isFile()) {
                    closeOk(path);
                } else {
                    // It's a directory - navigate into it
                    directoryList.setPath(path);
                }
            } else {
                // Path doesn't exist - assume new file
                closeOk(path);
            }
        }
    }
}
