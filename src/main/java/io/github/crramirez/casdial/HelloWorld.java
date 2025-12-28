/*
 * Casdial - Dialog command compatible based on casciian
 *
 * Copyright 2025 Carlos Rafael Ramirez
 *
 * Licensed under the MIT License
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
