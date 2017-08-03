package com.jigsaw.web.console;

/**
 * @author rhosseini
 * @date 4/18/2016
 */
public class ParentPrinterOne {

    public void init() {
        System.out.println("Parent printer ONE initializing...");
    }

    public void destroy() {
        System.out.println("Parent printer ONE destroying...");
    }

    public void print(String message) {
        System.out.println("Parent printer ONE printing " + message);
    }
}
