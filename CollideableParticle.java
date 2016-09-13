package com.gmail.youknowjoejoe.partphys;

public class CollideableParticle extends Particle {
	
	public CollideableParticle(Vector2f pos, Vector2f vel, float mass, float radius) {
		super(pos, vel, mass, radius);
	}
	
	public void solvePossibleCollisionWith(CollideableParticle cp){
		Vector2f thisToCP = this.getPos().minus(cp.getPos());
		float distanceSquared = thisToCP.getMagnitudeSquared();
		
		if(distanceSquared < (this.radius + cp.getRadius())*(this.radius + cp.getRadius())){
			float distance = thisToCP.getMagnitude();
			float overlap = this.radius+cp.getRadius()-distance;
			Vector2f dir = thisToCP.scaledBy(1/distance);
			Vector2f mtv = dir.scaledBy(overlap);
			
			this.applyDisplacement(mtv.scaledBy(1f));
			cp.applyDisplacement(mtv.scaledBy(-1f));
			
			Vector2f cpMomentum = cp.getMomentum().scaledBy(0.7f);
			Vector2f thisMomentum = this.getMomentum().scaledBy(0.7f);
			
			this.setMomentum(cpMomentum);
			cp.setMomentum(thisMomentum);
		}
	}
}
