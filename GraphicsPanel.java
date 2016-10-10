package com.gmail.youknowjoejoe.partphys;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.awt.Dimension;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;

@SuppressWarnings("serial")
public class GraphicsPanel extends JPanel implements Runnable{
    
    private final int WIDTH = 1600;
    private final int HEIGHT = 900;
    
    private Color clearScreen = new Color(0,0,0);
    private Color transparency = new Color(0,0,0,0);
    private float fadeFactor;
    private BufferedImage frameRender = null;
    private BufferedImage display = null;
    public float dt;
    private double oldTime;
    private double currentTime;
    private double accumulatedTime = 0;
    private Double timePassed = 0.0;
    private double timeScale;
    
    private boolean running = true;
    
    private List<CollideableParticle> particles;
    
    public GraphicsPanel(float dt, float timeScale, float fadeFactor, float coefOfRestitution){
    	this.dt = dt;
    	this.timeScale = timeScale;
    	this.fadeFactor = fadeFactor;
    	
        this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        
        particles = new ArrayList<CollideableParticle>();
        
        for(int x = 0; x < 22; x++){
        	for(int y = 0; y < 11; y++){
        		double ran = Math.random();
        		float mass = ((int)Math.ceil((10*ran)))*10;
        		System.out.println(1f/mass + " " + mass/10f);
        		float radius = mass/10f;
        		particles.add(new CollideableParticle(new Vector2f(50+x*(WIDTH/50),50+y*(HEIGHT/40)),new Vector2f(0,0),1f/mass,radius,coefOfRestitution));
        	}
        }
        
        for(int x = 0; x < 22; x++){
        	for(int y = 0; y < 11; y++){
        		particles.add(new CollideableParticle(new Vector2f(1000+x*(WIDTH/50),600+y*(HEIGHT/40)),new Vector2f(0,0),0.01f,5f,coefOfRestitution));
        	}
        }
        
        /*Force gravity = new Force(){
            Vector2f force = new Vector2f(0,9.8f);
            
            @Override
            public Vector2f calcForce(){
                return force;
            }
        };*/
        
        for(int rep = 0; rep < particles.size(); rep++){
        	for(int rep2 = 0; rep2 < particles.size(); rep2++){
        		if(rep != rep2){
        			particles.get(rep).getForces().add(new AttractionForce(particles.get(rep), particles.get(rep2), 10.0f, 2.0f));
        		}
        	}
        }
        
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
    }
    
    @Override
    public void paintComponent(Graphics g){
    	
    	frameRender = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    	
    	//save a normal frame onto frameRender
    	{
	        Graphics2D g2d = frameRender.createGraphics();
	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        
	        g2d.setColor(transparency);
	        g2d.fillRect(0,0,WIDTH,HEIGHT);
	        
	        AffineTransform save = g2d.getTransform();
	        
	        for(Particle p: particles){
	        	p.draw(g2d);
	        }
	        
	        g2d.setTransform(save);
	        
	        g2d.dispose();
    	}
        
    	//save display as display (from last frame) darkened with render drawn on top. 
    	
    	if(fadeFactor != 0){
    		BufferedImage displayTemp = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
    		
    		if(display == null){
	        	display = frameRender;
	        }
    		Graphics2D g2d = displayTemp.createGraphics();
    		
    		/*g2d.setColor(darken);
    		g2d.fillRect(0, 0, WIDTH, HEIGHT);
    		
    		g2d.drawImage(frameRender, 0, 0, null);*/
    		
    		g2d.setColor(clearScreen);
    		g2d.fillRect(0, 0, WIDTH, HEIGHT);
    		
    		Composite compSave = g2d.getComposite();
            
            int rule = AlphaComposite.SRC_OVER;
            Composite comp = AlphaComposite.getInstance(rule, fadeFactor);
            g2d.setComposite(comp);
            g2d.drawImage(display,0,0,null);
            
            g2d.setComposite(compSave);
            g2d.drawImage(frameRender,0,0,null);
            
            g2d.dispose();
            
            display = displayTemp;
    	} else {
    		display = frameRender;
    	}
    	
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setColor(clearScreen);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);
        
        g2d.drawImage(display, 0, 0, null);
    }
    
    public static void main(String[] args){
        JFrame window = new JFrame("Particle Physics");
        GraphicsPanel pane = new GraphicsPanel(1.0f/180.0f,1.0f,0.9f,0.9f);
        window.add(pane);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.pack();
        window.setVisible(true);
        (new Thread(pane)).start();
    }
}