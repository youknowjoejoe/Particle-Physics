package com.gmail.youknowjoejoe.partphys;

import java.util.List;
import java.util.ArrayList;

public class PhysicsBoundObject{
    private Vector2f pos, vel, oldVel, accel;
    private float inverseMass;
    private List<Force> forces;
    private List<Force> forcesToRemove;
    
    public PhysicsBoundObject(Vector2f pos, Vector2f vel,float mass){
        this.pos = pos;
        this.vel = vel;
        this.oldVel = vel;
        this.inverseMass = 1.0f/mass;
        
        this.forces = new ArrayList<Force>();
        this.forcesToRemove = new ArrayList<Force>();
    }
    
    //USE THIS METHOD TO AVOID CONCURRENT MODIFICATION EXCEPTION
    public void removeForce(Force f){
        this.forcesToRemove.add(f);
    }
    
    public List<Force> getForces(){
        return forces;
    }
    
    public void applyForces(float dt){
        forces.removeAll(forcesToRemove);
        forcesToRemove.clear();
        
        Vector2f totalForce = new Vector2f(0,0);
        
        for(Force f: forces){
            totalForce = totalForce.plus(f.calcForce());
        }
        
        this.accel = totalForce.scaledBy(inverseMass);
        
        this.vel = this.vel.plus(this.accel.scaledBy(dt));
    }
    
    public void applyImpulse(Impulse i){
        this.vel = this.vel.plus(i.calcImpulse());
    }
    
    public void applyVelocity(float dt){
        this.pos = this.pos.plus(this.vel.plus(this.oldVel).scaledBy(dt/2.0f));
        this.oldVel = this.vel;
    }
    
    public void applyDisplacement(Vector2f d){
    	this.pos = this.pos.plus(d);
    }
    
    public Vector2f getPos(){
        return this.pos;
    }
    
    public float getInverseMass(){
    	return this.inverseMass;
    }
    
    public float getRadius(){
    	return this.getRadius();
    }
}