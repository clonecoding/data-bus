package com.jdddata.middleware.databus.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;

@Controller
public class ManagerController {

    private static final String START = "start";
    private static final String STOP = "stop";

    private static final String SEP = File.separator;
    public static final String CANAL_COTEXT_DIRECTORY =
            SEP + "data" + SEP + "work" + SEP + "data-bus" + SEP + "cotext";


    @RequestMapping(value = "canal/{operate}", method = RequestMethod.POST)
    public String startCanal(@PathVariable String operate) {

        File configFile = new File("");


        return null;
    }

}
