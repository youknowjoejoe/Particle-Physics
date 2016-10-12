package com.gmail.youknowjoejoe.partphys;

public class CollideableParticle extends Particle {
	
	private float e;
	
	public CollideableParticle(Vector2f pos, Vector2f vel, float inverseMass, float radius, float restitution) {
		super(pos, vel, inverseMass, radius);
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
			
			/*Vector2f time = mtv.dividedBy(this.getAvgVelocity().minus(cp.getAvgVelocity()));
			float t = Math.min(time.getX(), time.getY());
			if(!Float.isFinite(t)){
				t = time.getX();
				if(!Float.isFinite(t)){
					t = time.getY();
				}
			}*/
			
			//boolean useTraceBack = (time.getX() < mtv.getX()*2 && time.getY() < mtv.getY()*2) && t < GraphicsPanel.dt;
			
			//if(useTraceBack){
			//	this.traceBack(t);
			//	cp.traceBack(t);
			//}
			
			Vector2f impulse = dir.scaledBy((this.getVelocity().minus(cp.getVelocity()).dot(dir)*(1+Math.min(this.getE(),cp.getE())))/(this.getInverseMass()+cp.getInverseMass()));
			//System.out.println(impulse.getMagnitude());
			
			this.applyImpulse(impulse.scaledBy(-1.0f));
			cp.applyImpulse(impulse);
			
			/*float percent = 1.0f;
			float slop = 0.01f;
			Vector2f displacement = mtv.scaledBy(Math.max(overlap-slop,0.0f)/((this.getInverseMass()+cp.getInverseMass()))*percent);
			
			this.applyDisplacement(displacement.scaledBy(this.getInverseMass()));
			cp.applyDisplacement(displacement.scaledBy(-1.0f*cp.getInverseMass()));*/
			
			/*if(useTraceBack){
				this.applyVelocity(-t);
				cp.applyVelocity(-t);
			} else {*/
			Vector2f displacement = mtv.scaledBy(1f/(this.getInverseMass()+cp.getInverseMass()));
			this.applyDisplacement(displacement.scaledBy(this.getInverseMass()));
			cp.applyDisplacement(displacement.scaledBy(-cp.getInverseMass()));
			//}
			
			//System.out.println(this.getPos().minus(cp.getPos()).getMagnitudeSquared() < (this.radius + cp.getRadius())*(this.radius + cp.getRadius()));
		}
	}

	public float getE() {
		return e;
	}

	public void setE(float e) {
		this.e = e;
	}
}
