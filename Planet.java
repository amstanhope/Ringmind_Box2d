/**
 * 
 */
package ringmind1;
import processing.core.PApplet;

import shiffman.box2d.*;

import org.jbox2d.common.*;

/**
 * @author Alex
 *
 */
public class Planet
{
	Box2DProcessing box2d;
	Vec2 pixelPosition;
	Vec2 worldPosition;
	PApplet parent;
	

	public Planet(Box2DProcessing box, PApplet par) 
	{
		this.box2d = box;
		this.parent = par;
		pixelPosition = new Vec2(parent.width/2, parent.height/2);
		worldPosition = box2d.coordPixelsToWorld(pixelPosition);
	}
	void applyG(Particle p, float G, float planetForceRange, float planetMinPixelsDistance, float planetMass)
	{
		//Vec2 mypos = worldPosition;
		Vec2 ppos = p.body.getPosition();
		Vec2 f = worldPosition.sub(ppos);
		float distance = f.length();
		//if(distance > box2d.scalarPixelsToWorld(500)){return;}
		float minDistance = box2d.scalarPixelsToWorld(planetMinPixelsDistance);
		distance = Math.min(box2d.scalarPixelsToWorld(planetForceRange), distance);
		distance = Math.max(minDistance, distance);
		f.normalize();
		f = f.mulLocal(G*((planetMass*p.body.getMass())/(distance*distance)));
		p.body.applyForce(f, p.body.getWorldCenter());
	}

	void applyG(Camera c, float planetMinPixelsDistance, float G, float planetMass)
	{
		Vec2 ppos = c.worldpos;
		Vec2 f = box2d.coordPixelsToWorld(pixelPosition).sub(ppos);
		float distance = f.length();
		float minDistance = box2d.scalarPixelsToWorld(planetMinPixelsDistance);
		distance = Math.max(minDistance, distance);
		f.normalize();
		f = f.mulLocal(G*((planetMass/(distance*distance))));
		c.addvel(f);
	}

	void display(int planetColor, float planetMinPixelsDistance, int grav, float planetForceRange)
	{
		parent.noFill();
		parent.stroke(planetColor);
		//fill(planetColor);
		parent.ellipse(pixelPosition.x,pixelPosition.y, planetMinPixelsDistance*2, planetMinPixelsDistance*2);
    
		parent.noFill();
		parent.stroke(grav);
		parent.ellipse(pixelPosition.x,pixelPosition.y, planetForceRange*2, planetForceRange*2);
		//drawGradient(pixelPosition.x, pixelPosition.y, planetForceRange*2, grav);
	}

	void displayAround(Vec2 center, float radians, int planetColor, int grav, float planetMinPixelsDistance, float planetForceRange)
	{
		parent.noFill();
		parent.stroke(planetColor);
		//fill(planetColor);
		parent.pushMatrix();
		Vec2 displayedpos = pixelPosition.sub(center);
		parent.rotate(radians);
		parent.ellipse(displayedpos.x, displayedpos.y, planetMinPixelsDistance*2, planetMinPixelsDistance*2);
      
		parent.noFill();
		parent.stroke(grav);
		parent.ellipse(displayedpos.x,displayedpos.y, planetForceRange*2, planetForceRange*2);
		parent.popMatrix();
    }
    
	void drawGradient(float x, float y, float range, int c, int backColor) 
	{
		float radius = range;
		parent.fill(c);
		parent.ellipse(x,y,radius, radius);
		float h = 255;
		for (float r = radius; r > 0; --r) 
		{
			parent.fill(backColor, h);
			parent.ellipse(x, y, r, r);
			h = 255-r/radius*255;
		}
	}
}
