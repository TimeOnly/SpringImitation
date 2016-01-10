package com.controller;

import MyAnnotation.Column;
import MyAnnotation.Controller;
import MyAnnotation.RequestMapper;

import com.Dao.IUserService;

@Controller(id = "userController")
public class UserController {

	private String name;
	
	@Column(id="userService")
	private IUserService userService;

	@RequestMapper(id="getName")
	public void getName(){
	  String name = userService.getName();
	  System.out.println("拿到的用户名称是:"+name);
	}
	
	public void getName2(){
		System.out.println("ABC");
	}
	
	@RequestMapper(id="getName2")
	public void test(String name,int id){
		
	}
	
	public IUserService getUserService() {
		return userService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}
}
