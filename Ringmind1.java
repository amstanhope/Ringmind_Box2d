package ringmind1;

import processing.core.PApplet;
import controlP5.*;
import shiffman.box2d.*;
//import org.jbox2d.collision.shapes.*;
import org.jbox2d.common.*;
//import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.*;
import java.util.*;

public class Ringmind1 extends PApplet 
{

	ArrayList<Particle> pars;
	ArrayList<Mun> muns = new ArrayList<Mun>();
	ArrayList<Merge> toaddnremove = new ArrayList<Merge>();
	ControlP5 cp5;
	ArrayList<Putton> puttons = new ArrayList<Putton>();

	public Box2DProcessing box2d;
	Planet planet;
	Camera camera;

	//adjustable parameters

	//setup
	float G = 6.674e-11f;//gravity constant
	int partis = 600;//number of initial particals
	float inner_ringdius = 300f;//inner radius of the ring
	float outter_ringdius = 430f;//outter radius of the ring
	//play
	float moon_min = 8.0f;//minimum mass of a moon
	int burstNum = 8;//number of particles generated after a burst
	float rockDensity = 5.0f;//density of rock(black part)
	float iceDensity = 1.0f;//density of ice(non-black part)
	float defaultMass = 0.3f*PI;//initial mass of a particle
	float planetForceRange = 500f;//planetary gravity's maximum range(the big circle)
	float planetMass = 1e14f;
	float planetMinPixelsDistance = 70f;//planetary gravity's minimum range(the small circle)
	float moonForceRange = 200f;
	int burst_check_frequency = 5;
	float burstAccel = 2f;//change of speed for bursted particles
	float minimumMass = 0.1f*PI;
	//probabilities
	float relativeSize = 0.1f;
	float relativeSizeRange = 0.5f;
	float combinedSize = 5.0f;
	float combinedSizeRange = 2.0f;
	float rocheLimit = 350f;
	float rocheLimitRange = 50f;
	float burstSize = 15;
	float burstSizeRange = 5;

	//colors
	int backColor;
	int gravColor;
	int planetColor;
	int rockColor;
	
	//others
	boolean previousM = false;
	boolean currentM = false;
	Vec2[] burstVel;
	int burst_freq_helper = 0;
	boolean spacePressed = false;
	boolean cPressed = false;
	boolean pPressed = true;


	public void settings()
	{
		size(1600,1000);
	}
	public void setup() 
	{

		//frameRate(30);

		init();
		initControls();
	}
	
	private void init()
	{
		
		backColor = color(27, 38, 44);
		gravColor = color(0,183,194);
		planetColor = color(5, 219, 242);
		rockColor = color(0);
		
		box2d = new Box2DProcessing(this);  
		box2d.createWorld();
		box2d.setGravity(0,0);
		box2d.listenForCollisions();

		pars = new ArrayList<Particle>();
		muns = new ArrayList<Mun>();
		toaddnremove = new ArrayList<Merge>();
		puttons = new ArrayList<Putton>();
		planet = new Planet(box2d,this);
  
		for(int i=0;i<partis;i++)
		{
			float radians = rdbt(0,2*PI);
			float this_ringdius = (rdbt(inner_ringdius, outter_ringdius));
			float x = this_ringdius*(float)Math.cos(radians)+planet.pixelPosition.x;
			float y = this_ringdius*(float)Math.sin(radians)+planet.pixelPosition.y;
			Particle p = new Particle(x, y, rockDensity, iceDensity,box2d,this);
			p.setOrbitVelocity(planet, G, planetMass);
			pars.add(p);
		}

		camera = new Camera(inner_ringdius, outter_ringdius, planet,box2d,this);
		camera.setOrbitVelocity(planet, G, planetMass);

		Mun mu = new Mun(camera.pos.x, camera.pos.y, rockDensity, iceDensity);
		mu.setOrbitVelocity(planet, G, planetMass);
		//print(mu.body.getMass());
		muns.add(mu);
		calc_burst();
  
		//println(box2d.scalarPixelsToWorld(1));
  
	}
	
	private float rdbt(float min, float max)
	{
		return (float)random(1)*(max-min)+min;
	}
	
	public void draw() 
	{
		if(pPressed)
		{
			background(backColor);
			//println(frameRate);
			box2d.step(1f/60f,10,8);
    
			//particles and bodies to be added and removed
			for(Merge tp:toaddnremove)
				tp.reborn(moon_min, muns, pars);
			toaddnremove.clear();
 /* 
    //bursts due to roche limit
    if(burst_freq_helper==0){
      for(int i=pars.size()-1;i>=0;i--){
        pars.get(i).roche_update();
      }
      for(int i=muns.size()-1;i>=0;i--){
        muns.get(i).roche_update();
      }
      burst_freq_helper = burst_check_frequency;
      //println(roche_check(new Vec2(mouseX, mouseY)));
    }else{
      burst_freq_helper--;
    } */
			
			//apply gravity
			for (int i = pars.size()-1; i >= 0; i--) 
			{
				Particle p = pars.get(i);   
				planet.applyG(p,G,planetForceRange,planetMinPixelsDistance,planetMass);
				for(Mun mo:muns)
					mo.applyG(p,G,moonForceRange);
			}
			
			for(Mun mo:muns)
			{
				for(Mun m2:muns)
				{
					if(mo!=m2){mo.applyG(m2,G,moonForceRange);}
				}
				planet.applyG(mo,G,planetForceRange,planetMinPixelsDistance,planetMass);
			}
			planet.applyG(camera,planetMinPixelsDistance, G, planetMass);
			camera.update();
			//camera.display();
  
			//display
			if(cPressed){
				displayAll();
			}else{
				displayNormal();
			}
    
    //handle inputs
    // When the mouse is clicked, add a new particle
    //if(!previousM && mousePressed){
    //  Particle p = new Particle(mouseX, mouseY);
    //  p.setOrbitVelocity(planet);
    //  pars.add(p);
    //}
			if(mousePressed){
				previousM = true;
			}else{
				previousM = false;
			}
    //if(spacePressed){
    //  spacePressed = fa
    //}
    
			//updateControls();
		}
	}

	public void keyPressed()
	{
		switch(key)
		{
			case ' ':
				spacePressed = true;
				init();
				break;
			case 'c':
				cPressed = !cPressed;
				break;
			case 'p':
				pPressed = !pPressed;
			default:
				break;
    
		}
	}


	public void beginContact(Contact cp)
	{
		new Merge(cp, box2d, muns,pars,toaddnremove, relativeSize, relativeSizeRange, combinedSize, combinedSizeRange,rockDensity,iceDensity,this);
	}
	public void endContact(Contact cp) 
	{
	}
	

	void displayNormal()
	{
		for(Particle p:pars)
			p.display(rockColor);
		
		planet.display(planetColor, planetMinPixelsDistance, gravColor, planetForceRange);
		for(Mun mo:muns)
			mo.display(rockColor,gravColor,moonForceRange);
		
		stroke(122);
		noFill();
		ellipse(width/2, height/2, rocheLimit*2,rocheLimit*2);
	}
	void displayAll()
	{
  
		pushMatrix();
		Vec2 moonpos = box2d.coordWorldToPixels(camera.worldpos);
		//Vec2 moonpos = moon.pixelPosition;
		translate(width/2,height/2-365);
    
		Vec2 disrotate = planet.pixelPosition.sub(moonpos);
		float disatan = disrotate.y/disrotate.x;
		//println("before"+disatan);
    
		disatan = (PI/2-atan(disatan));
		
		if(disrotate.x<0)
			disatan = disatan+PI;
		
		//println("after"+disatan/PI*180);
		//println(disrotate.x+" "+disrotate.y);
		for(Particle p:pars)
			p.displayAround(moonpos, disatan, rockColor);
		
		planet.displayAround(moonpos, disatan, planetColor, gravColor, planetMinPixelsDistance, planetForceRange);
	   for(Mun mo:muns)
		   mo.displayAround(moonpos, disatan, rockColor, gravColor, moonForceRange);
	   
	   //moon.displayAround(moonpos, disatan);
	   popMatrix();

	}

	void calc_burst(){
		burstVel = new Vec2[burstNum];
		for(int i=burstNum-1;i>=0;i--)
		{
			float angle = (float)i / (float)burstNum*2.0f*PI;
			//println("angle"+angle);
			float x = burstAccel*cos(angle);
			float y = burstAccel*sin(angle);

			if(x<0.00001f&&x>-0.00001f)
				x = 0;
			if(y<0.00001f&&y>-0.00001f)
				y = 0;
			burstVel[i] = new Vec2(x,y);
			//println(burstVel[i].x+"   "+burstVel[i].y);
		}
	}

	
	
	int getColor(float mass)
	{
		float max = 2;
		if(mass<=0){
			return color(255,183, 255);
		}else if(mass>max){
			return color(0, 183, 255);
		}else{
			return color(255-mass/max*255, 183,255);
		}
    
	}
	

	
	public void initControls()
	{
		//println(PFont.list());
		cp5 = new ControlP5(this);
		int off = 0;
		cp5.addTextlabel("setup")
			.setText("SETUP")
			.setPosition(50,17)
			.setColorValue(0xffE5F3FF)
			.setFont(createFont("Arial",17))
			;
    
		puttons.add(new Putton("G", 50,50, 6.674e-11f, 3.674e-11f, cp5));
		puttons.add(new Putton("partis", 50, 70, 600, 500, cp5));
		puttons.add(new Putton("inner_ringdius", 50, 90, 300f, 200f, cp5));
		puttons.add(new Putton("outter_ringdius", 50, 110, 430f, 200f, cp5));
  
		off = 160;
		cp5.addTextlabel("play")
			.setText("PLAY")
			.setPosition(50,off-25)
			.setColorValue(0xffE5F3FF)
			.setFont(createFont("Arial",17))
			;
		
		puttons.add(new Putton("moon_min", 50, 0+off, 8, 4, cp5));
		puttons.add(new Putton("burstNum", 50, 20+off, 8, 6, cp5));
		puttons.add(new Putton("rockDensity", 50, 40+off, 5, 4, cp5));
		puttons.add(new Putton("iceDensity", 50, 60+off, 1f, .6f, cp5));
		puttons.add(new Putton("defaultMass", 50, 80+off, 0.3f*PI, 0.3f*PI, cp5));
		puttons.add(new Putton("planetForceRange", 50, 100+off, 700, 200, cp5));
		puttons.add(new Putton("planetMass", 50, 120+off, 0.5e14f, 2.0e14f, true, cp5));
		puttons.add(new Putton("planetMinPixelsDistance", 50, 140+off, 70, 60, cp5));
		puttons.add(new Putton("moonForceRange", 50, 160+off, 200, 100, cp5));
		puttons.add(new Putton("burst_check_frequency", 50, 180+off, 5, 4, cp5));
		puttons.add(new Putton("burstAccel", 50, 200+off, 2, 1, cp5));
		puttons.add(new Putton("minimumMass", 50, 220+off, 0.1f*PI, 0.1f*PI, cp5));
  
		off = 435;
		cp5.addTextlabel("prob")
			.setText("PROBABAILITIES")
			.setPosition(50,off-25)
			.setColorValue(0xffE5F3FF)
			.setFont(createFont("Arial",17))
			;
		
		puttons.add(new Putton("relativeSize", 50, 0+off, 0.1f, 0.1f, cp5));
		puttons.add(new Putton("relativeSizeRange", 50, 20+off, 0.5f, 0.5f, cp5));
		puttons.add(new Putton("combinedSize", 50, 40+off, 2.0f, 2.0f, cp5));
		puttons.add(new Putton("combinedSizeRange", 50, 60+off, 2f, 2f, cp5));
		puttons.add(new Putton("rocheLimit", 50, 80+off, 350f, 300f, cp5));
		puttons.add(new Putton("rocheLimitRange", 50, 100+off, 50, 50, cp5));
		puttons.add(new Putton("burtSize", 50, 120+off, 15, 15, cp5));
		puttons.add(new Putton("burstSizeRange", 50, 140+off, 5, 5, cp5));

  
		cp5.addButton("resetCtrls")
			.setValue(0)
			.setPosition(50,650)
			.setSize(200,19)
			.setFont(createFont("Arial",14))
			;
  
	}

	public void resetCtrls()
	{
		System.out.println("resetssss");
		init();
	}

	public static void main(String _args[]) 
	{
		PApplet.main(new String[] { ringmind1.Ringmind1.class.getName() });
	}
}
	
