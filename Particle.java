/**
 * 
 */
package ringmind1;
import processing.core.PApplet;

import shiffman.box2d.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;


import static processing.core.PConstants.PI;

import java.lang.Math;
/**
 * @author Alex
 *
 */
public class Particle
{
	// We need to keep track of a Body and a radius
	Body body;
	float r;
	float density;
	float m;
	float rockPercent;
  
	int col;
	static float defaultMass = (float)(0.3*PI);//initial mass of a particle
	boolean isRemoved = false;
	float pixeld;
	float pixelrockd;
	static Box2DProcessing box2d;
	static PApplet parent;
  
	Particle(){}
	
	Particle(float x, float y, float m_, float rp, float rD, float iD, Box2DProcessing box, PApplet par) 
	{
		this.box2d = box;
		this.parent = par;
		m = m_;
		rockPercent = rp;
		setDensity(rockPercent, m, rD, iD);

		makeBody(x, y, r,box);
		body.setUserData(this);
		//col = color(255,255,255);
		//println(r);
		col = (int)r;
		//println(body.getMass());
		//println(body.getMass()/r/r/PI);
		pixeld = box2d.scalarWorldToPixels(r)*2;
		pixelrockd = (float)Math.sqrt(rockPercent*box2d.scalarWorldToPixels(r)*2);
	}
	
	Particle(float x, float y, float rD, float iD, Box2DProcessing box, PApplet par)
	{
		//this(x,y,0.62832f);
		this(x,y,defaultMass, (float)Math.random(), rD, iD, box, par);
	}
  
	public void setOrbitVelocity(Planet pn, float G, float planetMass)
	{
		Vec2 dis = pn.worldPosition.sub(body.getPosition());
		float R = dis.length();
		float _t = G*planetMass/R;
		float v = (float)Math.sqrt(_t);
		dis.normalize();
		dis = new Vec2(-dis.y, dis.x);
		dis.mulLocal(v);
		body.setLinearVelocity(dis);
	}
  
	void setDensity(float rock, float totalmass, float rockDensity, float iceDensity)
	{
		density = rock*rockDensity+(1-rock)*iceDensity;
		r = (float)Math.sqrt(totalmass/density/PI);
	}
  // 
  /*
  void roche_update(){
    Vec2 pos = box2d.coordWorldToPixels(body.getPosition());
    
    float r = (float)Math.sqrt(this.m/density/PI);
    r = box2d.scalarWorldToPixels(r);
    float bstprob = sigmoid(r, burstSize, burstSizeRange);
    //println(r);
    if(!roche_check(pos)||random(1)<bstprob){
      new Burst(this);
    }
  }*/
  
	void display(int rockColor) 
	{
		// We look at each body and get its screen position
		Vec2 pos = box2d.getBodyPixelCoord(body);
		parent.fill(rockColor);
		parent.noStroke();
		parent.ellipse(pos.x, pos.y, pixeld, pixeld);
		parent.fill(rockColor);
		parent.ellipse(pos.x, pos.y, pixelrockd, pixelrockd);
		//println(dia);
	}
	
	void displayAround(Vec2 center, float radians, int rockColor)
	{
		parent.fill(col);
		parent.noStroke();
		parent.pushMatrix();
		Vec2 displayedpos = box2d.getBodyPixelCoord(body).sub(center);
		parent.rotate(radians);
		parent.ellipse(displayedpos.x, displayedpos.y, pixeld, pixeld);
		parent.fill(rockColor);
		parent.ellipse(displayedpos.x, displayedpos.y, pixelrockd, pixelrockd);
		parent.popMatrix();
	}

	void makeBody(float x, float y, float r, Box2DProcessing box) 
	{
		this.box2d = box;
		// Define a body
		BodyDef bd = new BodyDef();
		//bd.allowSleep = true;
		//bd.awake = true;
		bd.fixedRotation = true;
		// Set its position
		bd.position = box2d.coordPixelsToWorld(x, y);
		bd.type = BodyType.DYNAMIC;
		body = box2d.createBody(bd);

		// Make the body's shape a circle
		CircleShape cs = new CircleShape();
		cs.m_radius = r;

		FixtureDef fd = new FixtureDef();
		fd.shape = cs;
		// Parameters that affect physics
		fd.density = density;
		fd.friction = (float)0.01;
		fd.restitution = (float)0.3;

 	  // Attach fixture to body
		body.createFixture(fd);

	}
}
