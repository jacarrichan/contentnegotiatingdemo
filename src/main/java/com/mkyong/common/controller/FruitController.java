package com.mkyong.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.mkyong.common.model.Fruit;


@Controller
@RequestMapping("/fruit")
public class FruitController {

    @RequestMapping(value = "{fruitName}", method = RequestMethod.GET)
    public String getFruit(@PathVariable String fruitName, ModelAndView model) {
        Fruit fruit = new Fruit(fruitName, 1000);
        model.addObject("model", fruit);
        return "list";
    }

}