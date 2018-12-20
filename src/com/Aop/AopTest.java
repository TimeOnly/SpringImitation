package com.Aop;

public class AopTest {
 public void Before(){
	 System.out.println("Before被调用,加了一点东西");
 }
 
 public void After(){
	 System.out.println("After被调用");
 }
}
