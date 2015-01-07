package com.mobile_test.models;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class Test {
	
	private int id;
    private String testData;
	
	public Test(int id, String testData) {
		this.id = id;
		this.testData = testData;
	}
	
	public Test() { }
	
	public String getData() {
		return testData;
	}
	
	public void setData(String testData) {
		this.testData = testData;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
}