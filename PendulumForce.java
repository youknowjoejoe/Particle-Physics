package com.gmail.youknowjoejoe.partphys;

public class PendulumForce implements Force{
	
	private Force counter;
	private PhysicsBoundObject o;
	private Vector2f origin;
	
	public PendulumForce(Force counter, PhysicsBoundObject o, Vector2f origin){
		this.counter = counter;
		this.o = o;
		this.origin = origin;
	}

	@Override
	public Vector2f calcForce() {
		Vector2f particleToOrigin = origin.minus(o.getPos());
		//float tensionScalar = counter.scaledBy(-1.0f*particleToOrigin.getY())
		return null;
	}
}
