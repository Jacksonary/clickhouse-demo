package org.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author liuwg
 * @datetime 2020/9/20 19:56
 * @description
 */
@RestController
@RequestMapping("")
public class HomeController {

    @RequestMapping(value = "go", method = RequestMethod.GET)
    public String go() {
        return "go";
    }
}
