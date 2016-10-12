package com.gmail.youknowjoejoe.partphys;

public class ConstantForce implements Force {
	
	public Vector2f constantForce;
	
	public ConstantForce(Vector2f force){
		this.constantForce = force;
	}

	@Override
	public Vector2f calcForce() {
		return constantForce;
	}
}
