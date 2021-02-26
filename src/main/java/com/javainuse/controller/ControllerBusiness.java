package com.javainuse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ControllerBusiness {
	@RequestMapping("/hello")
	public String helloWorld() {
		return "Hello World";
	}
	@RequestMapping("/customers")
	public String getAllCustomers() {
		return "";
	}
}
