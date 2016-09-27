package com.gmail.youknowjoejoe.partphys;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.awt.Dimension;
import java.awt.Color;

@SuppressWarnings("serial")
public class GraphicsPanel extends JPanel implements Runnable{
    
    private final int WIDTH = 640;
    private final int HEIGHT = 480;
    
    private Color clearScreen = new Color(255,255,255);
    private float dt = 1.0f/180.0f;
    private double oldTime;
    private double currentTime;
    private double accumulatedTime = 0;
    private Double timePassed = 0.0;
    private double timeScale = 2f;
    
    private boolean running = true;
    
    private List<CollideableParticle> particles;
    
    public GraphicsPanel(){
        this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        
        particles = new ArrayList<CollideableParticle>();
        
        /*for(int x = 0; x < 22; x++){
        	for(int y = 0; y < 16; y++){
        		particles.add(new CollideableParticle(new Vector2f(50+x*20,50+y*20),new Vector2f(0,0),0.1f,5f));
        	}
        }*/
        
        particles.add(new CollideableParticle(new Vector2f(325,150),new Vector2f(0,0),0.1f,50f,0.0f));
        particles.add(new CollideableParticle(new Vector2f(300,300),new Vector2f(0,-10),0.01f,5f,0.0f));
        particles.get(1).setColor(Color.red);
        Force gravity = new Force(){
            Vector2f force = new Vector2f(0,9.8f);
            
            @Override
            public Vector2f calcForce(){
                return force;
            }
        };
        //Force p1ToP2 = new AttractionForce(particles.get(0), particles.get(1), 10000000.0f, 2.0f);
        //Force p2ToP1 = new AttractionForce(particles.get(1), particles.get(0), 10000000.0f, 2.0f);
        //particles.get(0).getForces().add(gravity);
        //particles.get(0).getForces().add(p1ToP2);
        //particles.get(1).getForces().add(p2ToP1);
        /*for(int rep = 0; rep < particles.size(); rep++){
        	for(int rep2 = 0; rep2 < particles.size(); rep2++){
        		if(rep != rep2){
        			particles.get(rep).getForces().add(new AttractionForce(particles.get(rep), particles.get(rep2), 1000000.0f, 2.0f));
        		}
        	}
        }*/
    }
    
    @Override
    public void run(){
    	currentTime = System.nanoTime()/1000000000.0;
    	oldTime = currentTime;
    	
        while(running){
            this.cycle();
        }
    }
    
    public void cycle(){
    	currentTime = System.nanoTime()/1000000000.0;
    	accumulatedTime += (currentTime-oldTime)*timeScale;
    	
    	while(accumulatedTime > dt){
    		this.logic();
    		accumulatedTime-=dt;
    		timePassed+=dt;
    	}
        this.repaint();
        
        this.oldTime = currentTime;
    }
    
    public void logic(){
    	for(int rep = 0; rep < particles.size(); rep++){
    		CollideableParticle p = particles.get(rep);
    		p.applyForces(dt);
    		if(rep < particles.size()-1){
    			for(int rep2 = rep+1; rep2 < particles.size(); rep2++){
    				p.solvePossibleCollisionWith(particles.get(rep2));
    			}
    		}
        	p.applyVelocity(dt);
    	}
    	
    	System.out.println(particles.get(0).getMomentum().plus(particles.get(1).getMomentum()).getMagnitude());
    }
    
    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(clearScreen);
        g2d.fillRect(0,0,WIDTH,HEIGHT);
        
        AffineTransform save = g2d.getTransform();
        
        for(Particle p: particles){
        	p.draw(g2d);
        }
        
        g2d.setTransform(save);
    }
    
    public static void main(String[] args){
        JFrame window = new JFrame("Physics Derp1");
        GraphicsPanel pane = new GraphicsPanel();
        window.add(pane);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
        (new Thread(pane)).start();
    }
}