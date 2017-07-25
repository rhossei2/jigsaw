package com.jigsaw.web.spring;

import com.jigsaw.core.Jigsaw;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author rhosseini
 * @date 4/18/2016
 */
public class MainPrinter {

    @Autowired
    private ParentPrinterOne parentPrinterOne;

    @Autowired
    private ParentPrinterTwo parentPrinterTwo;

    @Autowired
    private Jigsaw jigsaw;

    public void init() {
        System.out.println("There are " + jigsaw.getPieceManager().getPieces().size() + " pieces in the container");

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
