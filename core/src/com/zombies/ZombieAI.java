package com.zombies;

public class ZombieAI {

	private String mode = "calm";
	private Unit u;
	
	public ZombieAI(Unit u) {
		this.u = u;
	}
	
	public void update() {
		if (mode == "calm") {
			this.calm();
		}
	}
	
	
	
	
	
	private void calm() {
		
	}
	
}
