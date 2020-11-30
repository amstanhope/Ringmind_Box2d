package ringmind1;

import processing.core.PApplet;
import controlP5.*;
import shiffman.box2d.*;
import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;
import java.util.*;

class Burst extends Merge
{
	Box2DProcessing box2d;
  
	Burst(Particle p)
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
		make_burst(pos, mass, p.body.getLinearVelocity());
		box2d.destroyBody(p.body);
	}
  
  
}