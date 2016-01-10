package com.controller;

public class getClassTest {
 public static void main(String[] args) throws Exception{
	 UserController u = new UserController();
     System.out.println("1,"+u.getClass().getAnnotations().length);
     Class<?> c = Class.forName("com.controller.UserController").newInstance().getClass();
     System.out.println("2,"+c.getAnnotations().length);
     
 }
}
