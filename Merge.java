/**
 * 
 */
package ringmind1;

import processing.core.PApplet;
//import controlP5.*;
import shiffman.box2d.*;
//import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;

import static processing.core.PConstants.PI;

import java.util.*;

/**
 * @author Alex
 *
 */
public class Merge
{
	Vec2 pos;
	Vec2 vel;
	//float r;
	float m;
	boolean burst;
	float rockPercent;
	  
	Body b1;
	Body b2;
	Box2DProcessing box2d;
	PApplet parent;

	Merge(Contact cp, Box2DProcessing box, ArrayList<Mun> muns, ArrayList<Particle> pars, ArrayList<Merge> toaddnremove, float relativeSize, float relativeSizeRange, float combinedSize, float combinedSizeRange, float rockDensity, float iceDensity, PApplet par)
	{
		this.box2d = box;
		parent = par;
		burst = false;
	    Fixture f1 = cp.getFixtureA();
	    Fixture f2 = cp.getFixtureB();
	    Body b1 = f1.getBody();
	    Body b2 = f2.getBody();
	    Object o1 = b1.getUserData();
	    Object o2 = b2.getUserData();
	    Particle p1 = (Particle) o1;
	    Particle p2 = (Particle) o2;
	    if(p1.isRemoved || p2.isRemoved) {return;}
	    
	    this.m = b1.getMass()+b2.getMass();
	    
	    float relSize = (b1.getMass()-b2.getMass())/this.m;
	    relSize = Math.abs(relSize);
	    float prob_relSize = sigmoid(relSize, relativeSize, relativeSizeRange);
	    float prob_combined = sigmoid(m, combinedSize, combinedSizeRange);
	    
	    if(parent.random(1)<prob_relSize )
	    {
	    	//if merge
	    	//println("relsize pass");
	    	if(parent.random(1)<prob_combined)
	    	{
	    		//println("combined pass");
	    		if(p1 instanceof Mun){muns.remove(p1);}else{pars.remove(p1);}
	    		if(p2 instanceof Mun){muns.remove(p2);}else{pars.remove(p2);}
	    		p1.isRemoved = true;
	    		p2.isRemoved = true;
	        
	    		calc_properties(p1,p2,b1,b2);
	    		float density = rockPercent*rockDensity+(1-rockPercent)*iceDensity;
	    		toaddnremove.add(this);
	    	}
	    }
	}
	
	private float sigmoid(float x, float shift, float expand)
	{
		return 1.0f/(1.0f+parent.exp((-x+shift)*(4/expand)));
	}
	
	Merge(){};
	  

	  
	  
	void calc_properties(Particle p1, Particle p2, Body b1, Body b2)
	{
		this.b1 = b1;
		this.b2 = b2;
		float m1 = b1.getMass();
		float m2 = b2.getMass();
		//m1 *= m1;
		//m2 *= m2;
		float totalm = m1+m2;
		Vec2 displace = b2.getPosition().sub(b1.getPosition());
		//here calculates the new position for merged moon
		float posx = displace.x / totalm * m2;
		float posy = displace.y / totalm * m2;
		posx += b1.getPosition().x;
		posy += b1.getPosition().y;
		this.pos = box2d.coordWorldToPixels(new Vec2(posx, posy));

		Vec2 v1 = b1.getLinearVelocity().clone();
		Vec2 v2 = b2.getLinearVelocity().clone();
		v1.mulLocal(m1);
		v2.mulLocal(m2);
		v1.addLocal(v2);
		v1.mulLocal(1/totalm);
		this.vel = v1;
    
		m = totalm;
		rockPercent = (p1.rockPercent*m1+p2.rockPercent*m2)/totalm;
	}
  

  //
	void reborn(float moon_min, ArrayList<Mun> muns, ArrayList<Particle> pars)
	{
		if(this.burst)
		{
			//make_burst(this.pos, this.m, this.vel);
		}
		else if(this.m>moon_min){
			Mun m = new Mun(this.pos.x, this.pos.y, this.m, rockPercent);
			muns.add(m);
			m.body.setLinearVelocity(this.vel);
      
		}else{
			Particle newParti = new Particle(this.pos.x, this.pos.y, m, rockPercent,box2d,parent);
			pars.add(newParti);
			newParti.body.setLinearVelocity(this.vel);
		}
		box2d.destroyBody(this.b1);
		box2d.destroyBody(this.b2);
	}
	
	void burst(Particle p, ArrayList<Mun> muns, ArrayList<Particle> pars, float minimumMass, int burstNum, float burstAccel)
	{
		if(p.body.getMass()<=minimumMass)
		{
			return;    
		}
		if(p instanceof Mun){
			muns.remove(p);
		}else{
			pars.remove(p);
		}
		
		Vec2 pos = box2d.coordWorldToPixels(p.body.getPosition());
		float mass = p.body.getMass();
		make_burst(pos, mass, p.body.getLinearVelocity(), burstNum, burstAccel, pars);
		box2d.destroyBody(p.body);
	}
  
	
	
	void make_burst(Vec2 pos, float mass, Vec2 vel, int burstNum, float burstAccel, ArrayList<Particle> pars)
	{
		float newm = mass/burstNum;
		float borrowedVel = 0f;
		for(int i=burstNum-1;i>=0;i--)
		{
			float radians = 2*PI*i/burstNum;
			float radius = newm*20.0f;
			float x = radius*(float)Math.cos(radians)+pos.x;
			float y = radius*(float)Math.sin(radians)+pos.y;
			Particle newp = new Particle(x,y,newm, rockPercent,box2d,parent);
			//newp.setOrbitVelocity(planet);
			Vec2 newvel = vel.clone();
			
			if(i!=0)
			{
				float mag = newvel.length();
				float sign = parent.random(2)-1;
				// println("sign: "+sign);
				float addedVel = burstAccel*sign;
				borrowedVel -= addedVel;
				mag += addedVel;
				newvel.normalize();
				newvel.mulLocal(mag);
			}else{
				float mag = newvel.length();
				mag += borrowedVel;
				newvel.normalize();
				newvel.mulLocal(mag);
			}
			//newvel.subLocal(burstVel[i]);
			//newvel.addLocal(burstVel[i]);
			//newp.body.setLinearVelocity(newvel.add(burstVel[i]));
			newp.body.setLinearVelocity(newvel);
			pars.add(newp);
		}
	} 
}



/*boolean roche_check(Vec2 position)
{
  float distance = planet.pixelPosition.sub(position).length();
  float prob = sigmoid(distance, rocheLimit, 100f/rocheLimit);
  return random(1)<prob;
}*/
