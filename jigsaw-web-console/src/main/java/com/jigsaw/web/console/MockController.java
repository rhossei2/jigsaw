package com.jigsaw.web.console;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author rhosseini
 * @date 4/28/2016
 */
@Controller
@RequestMapping("/")
public class MockController {

    @Autowired
    private MainPrinter mainPrinter;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public void hello(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        resp.getWriter().append("Hello World!!");
        mainPrinter.sayHello();
    }

    public void setMainPrinter(MainPrinter mainPrinter) {
        this.mainPrinter = mainPrinter;
    }
}
