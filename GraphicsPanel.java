package com.gmail.youknowjoejoe.partphys;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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
public class GraphicsPanel extends JPanel implements Runnable, MouseListener, MouseMotionListener{
    
    private final int WIDTH = 1024;
    private final int HEIGHT = 768;
    
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
    
    private Vector2f mousePos = new Vector2f(0,0);
    private boolean leftMouseDown = false;
    private boolean rightMouseDown = false;
    private List<MouseInfoReceiver> mReceivers;
    
    private List<CollideableParticle> particles;
    
    public GraphicsPanel(float dt, float timeScale, float fadeFactor, float coefOfRestitution, float attractionForce){
    	this.dt = dt;
    	this.timeScale = timeScale;
    	this.fadeFactor = fadeFactor;
    	
    	this.setFocusable(true);
    	this.requestFocus();
    	this.addMouseListener(this);
    	this.addMouseMotionListener(this);
        this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        
        mReceivers = new ArrayList<MouseInfoReceiver>();
        
        particles = new ArrayList<CollideableParticle>();
        
        for(int x = 0; x < 11; x++){
        	for(int y = 0; y < 11; y++){
        		double ran = Math.random();
        		float mass = ((int)Math.ceil((10*ran)))*10;
        		float radius = mass/10f;
        		//float mass = 100f;
        		//float radius = 5f;
        		particles.add(new CollideableParticle(new Vector2f(50+x*(WIDTH/50),50+y*(HEIGHT/40)),new Vector2f(0,0),1f/mass,radius,coefOfRestitution));
        	}
        }
        
        for(int x = 11; x < 22; x++){
        	for(int y = 0; y < 11; y++){
        		particles.add(new CollideableParticle(new Vector2f(500+x*(WIDTH/50),500+y*(HEIGHT/40)),new Vector2f(0,0),0.01f,5f,coefOfRestitution));
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
        			particles.get(rep).getForces().add(new AttractionForce(particles.get(rep), particles.get(rep2), attractionForce, 2.0f));
        			MouseAttractionForce m = new MouseAttractionForce(particles.get(rep), 100f, attractionForce, 2f,10f);
        			mReceivers.add(m);
        			particles.get(rep).getForces().add(m);
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
    	
    	this.updateInput();
    	
    	while(accumulatedTime > dt){
    		this.logic();
    		accumulatedTime-=dt;
    		timePassed+=dt;
    	}
        this.repaint();
        
        this.oldTime = currentTime;
    }
    
    public void updateInput(){
    	for(MouseInfoReceiver mr: mReceivers){
    		mr.giveMouseInfo(mousePos, leftMouseDown, rightMouseDown);
    	}
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
    	System.setProperty("sun.java2d.opengl","True");
        JFrame window = new JFrame("Particle Physics");
        GraphicsPanel pane = new GraphicsPanel(1.0f/720.0f,0.4f,0.98f,0.9f,1000.0f);
        window.add(pane);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.pack();
        (new Thread(pane)).start();
    }

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == 1){
			leftMouseDown = true;
		}
		if(e.getButton() == 3){
			rightMouseDown = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 1){
			leftMouseDown = false;
		}
		if(e.getButton() == 3){
			rightMouseDown = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mousePos = new Vector2f(e.getX(),e.getY());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePos = new Vector2f(e.getX(),e.getY());
	}
}