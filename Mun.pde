class Mun extends Particle{
  
  
  public Mun(float x, float y, float m_, float rp) {
    m = m_;
    rockPercent = rp;
    setDensity(rp, m_);
    makeBody(x, y, r);
    body.setUserData(this);
    //setOrbitVelocity(planet);
    col = getColor(r);
    //    print(body.getLinearVelocity().x);
    //print(body.getLinearVelocity().y);
    //println(body.getMass());
    pixeld = box2d.scalarWorldToPixels(r)*2;
    pixelrockd = (float)Math.sqrt(rockPercent*box2d.scalarWorldToPixels(r)*2);
    
    //println(rockPercent);
    
  }
  Mun(float x, float y){
    this(x,y,defaultMass*10, random(1));
  }
  
  void applyG(Particle p){
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
    if(p instanceof Mun){
    }else{
      body.applyForce(f, body.getWorldCenter());
    }
   
  }
  
  void display(){
    Vec2 pos = box2d.coordWorldToPixels(body.getPosition());
    //println("mass"+body.getMass());
    noStroke();
    fill(col);
    ellipse(pos.x, pos.y, pixeld, pixeld);
    fill(rockColor);
    ellipse(pos.x, pos.y, pixelrockd, pixelrockd);

    noFill();
    stroke(grav, 70);
    ellipse(pos.x, pos.y,moonForceRange*2, moonForceRange*2);
  }
  void displayAround(Vec2 center, float radians){
      noStroke();
      fill(col);
    pushMatrix();
      Vec2 pixelPosition = box2d.coordWorldToPixels(body.getPosition());
      Vec2 displayedpos = pixelPosition.sub(center);
      rotate(radians);
      ellipse(displayedpos.x, displayedpos.y, pixeld, pixeld);
      fill(rockColor);
      ellipse(displayedpos.x, displayedpos.y, pixelrockd, pixelrockd);
      
      noFill();
      stroke(grav, 70);
      ellipse(displayedpos.x, displayedpos.y, moonForceRange*2, moonForceRange*2);
    
    popMatrix();
  }
  
}
