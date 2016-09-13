package com.gmail.youknowjoejoe.partphys;

import java.awt.Graphics2D;
import java.awt.Color;

public class Particle extends PhysicsBoundObject{
    float radius;
    
    public Particle(Vector2f pos, Vector2f vel, float mass, float radius){
        super(pos, vel, mass);
        
        this.radius = radius;
    }
    
    public void draw(Graphics2D g2d){
        g2d.setColor(Color.blue);
        g2d.fillArc((int) (this.getPos().getX()-radius),(int) (this.getPos().getY()-radius),(int) (2*radius),(int) (2*radius),0,360);
    }
    
    public float getRadius(){
    	return radius;
    }
}