/**
 * 
 */
package ringmind1;
import shiffman.box2d.*;

import org.jbox2d.common.*;

import java.lang.Math;
/**
 * @author Alex
 *
 */
public class Mun extends Particle
{

	
	public Mun(float x, float y, float m_, float rp, float rD, float iD)
	{
		super();
		m = m_;
	    rockPercent = rp;
	    setDensity(rp, m_, rD, iD);
	    makeBody(x, y, r, box2d);
	    body.setUserData(this);
	    //setOrbitVelocity(planet);
	    col = (int)r;
	    //    print(body.getLinearVelocity().x);
	    //print(body.getLinearVelocity().y);
	    //println(body.getMass());
	    pixeld = box2d.scalarWorldToPixels(r)*2;
	    pixelrockd = (float)Math.sqrt(rockPercent*box2d.scalarWorldToPixels(r)*2);
	    
	    //println(rockPercent);
	    
	}
	
	public Mun(float x, float y, float rD, float iD)
	{
	    this(x,y,defaultMass*10, (float)Math.random(), rD, iD);
	}
	
	public void applyG(Particle p, float G, float moonForceRange)
	{
		//Vec2 mypos = worldPosition;
	    Vec2 ppos = p.body.getPosition();
	    Vec2 f = body.getPosition().sub(ppos);
	    float distance = f.length();
	    
	    if(distance > box2d.scalarPixelsToWorld(moonForceRange)){return;}
	    
	    float minDistance = box2d.scalarPixelsToWorld(r);
	    distance = Math.max(minDistance, distance);
	    
	    f.normalize();
	    f = f.mulLocal(G*((body.getMass()*p.body.getMass())/(distance*distance)));
	    p.body.applyForce(f, p.body.getWorldCenter());
	    f.mulLocal(-1);
	    
	    if(!(p instanceof Mun))
	    {
	      body.applyForce(f, body.getWorldCenter());
	    }
	   
	}
	  
	public void display(int rockColor, int grav, float moonForceRange)
	{
		Vec2 pos = box2d.coordWorldToPixels(body.getPosition());
	    //println("mass"+body.getMass());
		parent.noStroke();
		parent.fill(col);
		parent.ellipse(pos.x, pos.y, pixeld, pixeld);
		parent.fill(rockColor);
		parent.ellipse(pos.x, pos.y, pixelrockd, pixelrockd);

		parent.noFill();
		parent.stroke(grav, 70);
		parent.ellipse(pos.x, pos.y,moonForceRange*2, moonForceRange*2);
	}
	
	public void displayAround(Vec2 center, float radians, int rockColor, int grav, float moonForceRange)
	{
		parent.noStroke();
		parent.fill(col);
		parent.pushMatrix();
	    Vec2 pixelPosition = box2d.coordWorldToPixels(body.getPosition());
	    Vec2 displayedpos = pixelPosition.sub(center);
	    parent.rotate(radians);
	    parent.ellipse(displayedpos.x, displayedpos.y, pixeld, pixeld);
	    parent.fill(rockColor);
	    parent.ellipse(displayedpos.x, displayedpos.y, pixelrockd, pixelrockd);
	      
	    parent.noFill();
	    parent.stroke(grav, 70);
	    parent.ellipse(displayedpos.x, displayedpos.y, moonForceRange*2, moonForceRange*2);
	      
	    parent.popMatrix();
	}
}
