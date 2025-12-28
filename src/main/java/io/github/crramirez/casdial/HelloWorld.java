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
import casciian.TLabel;

/**
 * Simple Hello World application using Casciian TUI library.
 */
public class HelloWorld extends TApplication {

    /**
     * Constructor.
     *
     * @throws Exception if any error occurs
     */
    public HelloWorld() throws Exception {
        super(BackendType.XTERM);
        
        // Create a window
        TWindow window = addWindow("Hello World", 0, 0, 50, 10,
                TWindow.CENTERED | TWindow.MODAL);
        
        // Add a label with the hello world message
        window.addLabel("Hello World from Casciian!", 2, 2);
        window.addLabel("", 2, 3);
        window.addLabel("This is a simple TUI application", 2, 4);
        window.addLabel("built with the Casciian library.", 2, 5);
        
        // Add a button to exit
        window.addButton("&Exit", 2, 7, () -> { 
            this.exit(); 
        });
    }

    /**
     * Main entry point.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            HelloWorld app = new HelloWorld();
            (new Thread(app)).start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
