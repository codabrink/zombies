package com.zombies.src.zombies;

public class BodData {

	private String type;
	private Object o;
	
	public BodData(String type, Object o) {
		
		this.type = type;
		this.o = o;
		
	}

	public String getType() {
		return type;
	}
	
	public Object getObject() {
		return o;
	}
	
}
