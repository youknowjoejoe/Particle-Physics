package com.gmail.youknowjoejoe.partphys;

public class CollideableParticle extends Particle {
	
	public CollideableParticle(Vector2f pos, Vector2f vel, float mass, float radius) {
		super(pos, vel, mass, radius);
	}
	
	public void solvePossibleCollisionWith(CollideableParticle cp){
		Vector2f thisToCP = this.getPos().minus(cp.getPos());
		float distanceSquared = thisToCP.getMagnitudeSquared();
		
		if(distanceSquared > (this.radius + cp.getRadius())*(this.radius + cp.getRadius())){
			float distance = thisToCP.getMagnitude();
			float overlap = this.radius+cp.getRadius()-distance;
			Vector2f dir = thisToCP.scaledBy(1/distance);
			
			
		}
	}
}
