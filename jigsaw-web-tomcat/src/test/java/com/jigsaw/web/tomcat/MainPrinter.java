package com.jigsaw.web.tomcat;

/**
 * @author rhosseini
 * @date 4/18/2016
 */
public class MainPrinter {

    private ParentPrinterOne parentPrinterOne;

    private ParentPrinterTwo parentPrinterTwo;

    public void init() {
        parentPrinterOne.print("Initializing...");
        parentPrinterTwo.print("Initializing...");
    }

    public void sayHello() {
        System.out.println("Hello world!");
    }

    public void destroy() {
        System.out.println("Destroying main printer...");
    }

    public void setParentPrinterOne(ParentPrinterOne parentPrinterOne) {
        this.parentPrinterOne = parentPrinterOne;
    }

    public void setParentPrinterTwo(ParentPrinterTwo parentPrinterTwo) {
        this.parentPrinterTwo = parentPrinterTwo;
    }
}
