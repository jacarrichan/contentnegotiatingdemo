package com.mkyong.common.controller;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mkyong.common.model.Fruit;

@Controller
@RequestMapping("/color")
public class ColorController {

	@RequestMapping(value = "/red", method = RequestMethod.GET)
	public void getFruit(@RequestParam(required = false) String fruitName,
			HttpServletRequest request, ModelMap model) {
		Fruit fruit = new Fruit(fruitName, 1000);
		model.addAttribute("model", fruit);
	}

	@Autowired
	private HttpSession session;

	@RequestMapping(value = "/book", method = RequestMethod.GET)
	public void book(@RequestParam(required = false) String fruitName,
			HttpServletRequest request, ModelMap model) {
		System.out.println(Proxy.getInvocationHandler(session));
		System.out.println(session.getId());
	}

	@RequestMapping(value = "/yellow", method = RequestMethod.GET)
	public void yellow(@RequestParam(required = false) String fruitName,
			HttpServletRequest request, ModelMap model) {
		List<String> res = new ArrayList<>();
		res.add("ddddd");
		res.add("ddddddd44d");
		res.add("ddddd666");
		res.add("ddddd000");
		res.add(null);
		model.addAttribute(res);
	}
}