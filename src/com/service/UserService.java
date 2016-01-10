package com.service;

import MyAnnotation.Controller;

import com.Dao.IUserService;

@Controller(id = "userService")
public class UserService implements IUserService{

	@Override
	public String getName() {
		return "wcb";
	}

}
