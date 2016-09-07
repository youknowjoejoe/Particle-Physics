package com.gmail.youknowjoejoe.partphys;

import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
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
    
    private boolean running = true;
    
    private List<Particle> particles;
    
    public GraphicsPanel(){
        this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
        
        particles = new ArrayList<Particle>();
        particles.add(new Particle(new Vector2f(120,160),new Vector2f(30,0),0.1f,10));
        particles.add(new Particle(new Vector2f(320,360),new Vector2f(-30,0),0.1f,10));
        Force gravity = new Force(){
            Vector2f force = new Vector2f(0,9.8f);
            
            @Override
            public Vector2f calcForce(){
                return force;
            }
        };
        Force p1ToP2 = new AttractionForce(particles.get(0), particles.get(1), 10000000.0f, 2.0f);
        Force p2ToP1 = new AttractionForce(particles.get(1), particles.get(0), 10000000.0f, 2.0f);
        //particles.get(0).getForces().add(gravity);
        particles.get(0).getForces().add(p1ToP2);
        particles.get(1).getForces().add(p2ToP1);
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
    	accumulatedTime += currentTime-oldTime;
    	
    	while(accumulatedTime > dt){
    		this.logic();
    		accumulatedTime-=dt;
    		timePassed+=dt;
    	}
        this.repaint();
        
        this.oldTime = currentTime;
    }
    
    public void logic(){
    	for(Particle p: particles){
    		p.applyForces(dt);
        	p.applyVelocity(dt);
    	}
    }
    
    @Override
    public void paintComponent(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(clearScreen);
        g2d.fillRect(0,0,WIDTH,HEIGHT);
        
        for(Particle p: particles){
        	p.draw(g2d);
        }
    }
    
    public static void main(String[] args){
        JFrame window = new JFrame("Physics Derp1");
        GraphicsPanel pane = new GraphicsPanel();
        window.add(pane);
        window.pack();
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        (new Thread(pane)).start();
    }
}