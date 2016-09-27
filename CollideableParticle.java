package com.gmail.youknowjoejoe.partphys;

public class CollideableParticle extends Particle {
	
	private float e;
	
	public CollideableParticle(Vector2f pos, Vector2f vel, float mass, float radius, float restitution) {
		super(pos, vel, mass, radius);
		this.e = restitution;
	}
	
	public void solvePossibleCollisionWith(CollideableParticle cp){
		Vector2f thisToCP = this.getPos().minus(cp.getPos());
		float distanceSquared = thisToCP.getMagnitudeSquared();
		
		if(distanceSquared < (this.radius + cp.getRadius())*(this.radius + cp.getRadius())){
			float distance = thisToCP.getMagnitude();
			float overlap = this.radius+cp.getRadius()-distance;
			Vector2f dir = thisToCP.scaledBy(1.0f/distance);
			Vector2f mtv = dir.scaledBy(overlap);
			
			
			
			this.applyDisplacement(mtv.scaledBy(0.6f));
			cp.applyDisplacement(mtv.scaledBy(-0.6f));
			
			Vector2f impulse = dir.scaledBy(cp.getMomentum().minus(this.getMomentum()).dot(dir)*(1.0f+Math.min(this.getE(),cp.getE())));
			//Vector2f impulse = cp.getMomentum().minus(this.getMomentum()).scaledBy(0.5f);
			//Vector2f impulse = new Vector2f(0,0);
			
			this.applyImpulse(impulse);
			cp.applyImpulse(impulse.scaledBy(-1.0f));
			
			System.out.println(this.getPos().minus(cp.getPos()).getMagnitudeSquared() < (this.radius + cp.getRadius())*(this.radius + cp.getRadius()));
		}
	}

	public float getE() {
		return e;
	}

	public void setE(float e) {
		this.e = e;
	}
}
