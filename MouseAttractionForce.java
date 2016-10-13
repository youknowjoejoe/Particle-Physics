package com.gmail.youknowjoejoe.partphys;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class MouseAttractionForce implements Force, MouseInfoReceiver{
	
	private boolean leftMouseDown = false;
	private boolean rightMouseDown = false;
	private Vector2f mousePos = new Vector2f(0,0);
	private float mouseMass;
	private PhysicsBoundObject o;
	private float attractionConstant;
	private float distanceImportance;
	private float m1;
	private float cap;
	
	public MouseAttractionForce(PhysicsBoundObject o, float mouseMass, float attractionConstant, float distanceImportance, float cap){
		this.o = o;
		this.mouseMass = mouseMass;
		this.attractionConstant = attractionConstant;
		this.distanceImportance = distanceImportance;
		this.cap = mouseMass*cap;
		this.m1 = 1f/o.getInverseMass();
		
	}
	
	@Override
	public Vector2f calcForce() {
		if(leftMouseDown || rightMouseDown){
			Vector2f o1ToMouse = mousePos.minus(this.o.getPos());
			float distance = o1ToMouse.getMagnitude();
			
			Vector2f dir = o1ToMouse.scaledBy(1/distance);
			
			return dir.scaledBy((leftMouseDown ? 1.0f : -1.0f) * Math.min(attractionConstant*m1*mouseMass*((float) (1.0f/Math.pow(distance, this.distanceImportance))),cap));
		}
		return new Vector2f(0,0);
	}

	@Override
	public void giveMouseInfo(Vector2f mousePos, boolean leftMouseDown, boolean rightMouseDown) {
		this.mousePos = mousePos;
		this.leftMouseDown = leftMouseDown;
		this.rightMouseDown = rightMouseDown;
	}

}
