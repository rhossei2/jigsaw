package com.jigsaw.web.console;

/**
 * @author rhosseini
 * @date 4/18/2016
 */
public class ParentPrinterTwoExtended extends ParentPrinterTwo {

    public void init() {
        System.out.println("Parent printer TWO Extended initializing...");
    }

    public void destroy() {
        System.out.println("Parent printer TWO Extended destroying...");
    }

    public void print(String message) {
        System.out.println("Parent printer TWO Extended printing " + message);
    }
}
