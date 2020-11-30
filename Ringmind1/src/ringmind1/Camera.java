package ringmind1;

import processing.core.PApplet;
//import controlP5.*;
import shiffman.box2d.*;
//import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
//import org.jbox2d.dynamics.*;
//import org.jbox2d.dynamics.contacts.*;
//import java.util.*;

public class Camera extends PApplet
{
	Vec2 pos;
	Vec2 worldpos;
	Vec2 vel;
	Box2DProcessing box2d;
	  
	Camera(float inner_ringdius, float outer_ringdius, Planet planet, Box2DProcessing box)
	{
		this.box2d = box;
		float radians = rdbt(0,2*PI);
	    float this_ringdius = (inner_ringdius+outer_ringdius)/2;
	    float x = this_ringdius*(float)Math.cos(radians)+planet.pixelPosition.x;
	    float y = this_ringdius*(float)Math.sin(radians)+planet.pixelPosition.y;
	    pos = new Vec2(x,y);
	    worldpos = box2d.coordPixelsToWorld(pos);
	    vel = new Vec2(0,0);
	  }
	  void update()
	  {
	    worldpos.addLocal(vel.mul(0.01666666f));

	  }
	  
	  void addvel(Vec2 acceleration)
	  {
	    vel.addLocal(acceleration.mulLocal(0.01666667f));
	  }
	  
	  public void setOrbitVelocity(Planet pn,float G, float planetMass)
	  {
		  Vec2 dis = pn.worldPosition.sub(worldpos);
		  float R = dis.length();
		  float _t = G*planetMass/R;
		  float v = (float)Math.sqrt(_t);
		  dis.normalize();
		  dis = new Vec2(-dis.y, dis.x);
		  dis.mulLocal(v);

		  vel = dis;
		  //println(vel.x);
		  //println(vel.y);
	  }
	  
	  void display()
	  {
		  pos = box2d.coordWorldToPixels(worldpos);
		  fill(100);
		  ellipse(pos.x, pos.y, 10,10);
	  }
	  
	  private float rdbt(float min, float max)
		{
			return (float)random(1)*(max-min)+min;
		}
}
