package com.harsh.easylife;

public class Test{
	public void print() {
		MyInterface m=(a,b)->(a+b);
	}
	interface MyInterface{
		String merge(String a,String b);
	}
}