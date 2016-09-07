package com.gmail.youknowjoejoe.partphys;

public class AttractionForce implements Force {
	private PhysicsBoundObject o1;
	private PhysicsBoundObject o2;
	private float m1;
	private float m2;
	private float attractionConstant;
	private float distanceImportance;
	
	public AttractionForce(PhysicsBoundObject o1, PhysicsBoundObject o2, float attractionConstant, float distanceImportance){
		this.o1 = o1;
		this.o2 = o2;
		this.m1 = 1.0f/o1.getInverseMass();
		this.m2 = 1.0f/o2.getInverseMass();
		this.attractionConstant = attractionConstant;
		this.distanceImportance = distanceImportance;
	}

	@Override
	public Vector2f calcForce() {
		Vector2f o1ToO2 = this.o2.getPos().minus(this.o1.getPos());
		float distance = o1ToO2.getMagnitude();
		
		Vector2f dir = o1ToO2.scaledBy(1/distance);
		
		return dir.scaledBy(attractionConstant*m1*m2*((float) (1.0f/Math.pow(distance, this.distanceImportance))));
	}
	
	
}
