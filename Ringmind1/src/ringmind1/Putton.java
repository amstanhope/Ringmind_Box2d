/**
 * 
 */
package ringmind1;
import processing.core.PApplet;
import controlP5.*;
import shiffman.box2d.*;


/**
 * @author Alex
 *
 */
public class Putton extends PApplet
{
	int posx, posy;
	int sizex=200,sizey=19;
	int padx=10,pady=7;
	int backcolor = color(229,243,255, 122);
	Box2DProcessing box2d;
  
	Putton(String varName, int x, int y, float mid, float range, ControlP5 cp5)
	{
		this(varName, x, y, mid-range, mid+range, true, cp5);
	}
  
	Putton(String varName, int x, int y, float min, float max, boolean b, ControlP5 cp5)
	{
		cp5.addSlider(varName)
			.setPosition(x,y)
			.setRange(min, max)
			.setSize(sizex,sizey)
			.setValue((min+max)/2.0f)
			.setFont(createFont("Arial",14))
			;
		posx = x;
		posy = y;
    
	}
  
	void display()
	{
		noStroke();
		fill(backcolor);
		rect(posx-padx, posy-pady, sizex+padx*2, sizey+pady*2);
    
	}
  
}



	
