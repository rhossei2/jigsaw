package com.jigsaw.spring;

/**
 * @author rhosseini
 * @date 4/18/2016
 */
public class ParentPrinterTwo {

    public void init() {
        System.out.println("Parent printer TWO initializing...");
    }

    public void destroy() {
        System.out.println("Parent printer TWO destroying...");
    }

    public void print(String message) {
        System.out.println("Parent printer TWO printing " + message);
    }
}
