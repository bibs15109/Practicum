package cs4620.gl;

import java.util.Random;

import egl.math.Color;
import egl.math.Vector3;
import egl.math.Vector3d;

public class Particle {
	public Vector3d pos, vel;
	public Color color;
	public double weight, radius, lifetime;
	private static int MAX = 10, MIN = 0;
	
	Particle() {
		this.pos = new Vector3d(randDouble(MAX, MIN), randDouble(MAX, MIN),randDouble(MAX, MIN));
		this.vel = new Vector3d(0);
		this.color = new Color(randInt(0,255), randInt(0,255), randInt(0,255));
		this.weight = 1;
		this.radius = 1;
		this.lifetime = randDouble(0,10);
	}
	
	Particle(Vector3d pos, Vector3d vel, Color color, double weight, double radius, double lifetime) {
		this.pos=pos;
		this.vel=vel;
		this.color=color;
		this.weight = weight;
		this.radius = radius;
		this.lifetime = lifetime;
	}
	
	public void move(double dt) {
		this.vel.add(this.vel.clone().mul(dt));
	}
	
	public boolean collide(Particle other) {
		if (this.pos.equalsApprox(other.pos, 1)) return true;
		return false;
	}
	
	private static double randDouble(int min, int max) {
		Random rand = new Random();
		return min + (max- min) * rand.nextDouble();
	}
	
	private static int randInt(int min, int max) {
	    Random rand = new Random();
	    return rand.nextInt((max - min) + 1) + min;
	}
	
	public void draw() {
	        
	}
	
}
