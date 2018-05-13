
package entities;

import static main.Main.c;
import static main.Game.*;
import static processing.core.PApplet.*;

/**
 *
 * @author Nithin Pranesh
 */
public class Boat {
    
    private static final float k=0.2f, drag=0.05f;
    
    //these are in mouse space
    private float ax=0, ay=0, vx=0, vy=0, px=0, py=0, theta=0, phi=0, prop=0;
    
    //TODO: Change this wtf
    float s = 0.6f;
    
    public Boat() {}
    
    public Boat(float x, float y) {
        px = x;
        py = y;
    }
    
    public void update() {
        float diffX = c.mouseX-px,//(topDown?px:width/2f), //width/2f, 
              diffY = c.mouseY-py;//(topDown?py:width/2f);//height/2f;
        //float mDist2 = diffX*diffX + diffY*diffY;
        ax = 0.02f*prop*cos(theta)/1-drag*vx;//prop*0.05f*diffX/mDist-drag*vx;
        ay = 0.02f*prop*sin(theta)/1-drag*vy;//prop*0.05f*diffY/mDist-drag*vy;

        float dTheta = atan2(diffY, diffX)%(2*PI)-theta%(2*PI), 
              v = sqrt(vx*vx+vy*vy);
        if (dTheta<0)
            dTheta = dTheta<-PI?dTheta+2*PI:dTheta;
        else
            dTheta = dTheta>PI?dTheta-2*PI:dTheta;
        phi = k*dTheta;
        if (abs(dTheta)<0.02f*v)
            theta += dTheta;
        else
            theta += 0.02f*v*Math.signum(dTheta);

        vx += ax;
        vy += ay;

        px += vx;
        py += vy;

        //disturb water
        int i0=round(water.DIVS*px/c.width), j0=round(water.DIVS*py/c.height);
        int ex=2;
        for (int i=i0-ex; i<i0+ex; i++)
          for (int j=j0-ex; j<j0+ex; j++)
            water.setZ(i, j, water.z(i,j)+0.02f*prop*s*9f);
    }
    
    public void render() {
        c.pushMatrix();
        c.translate(water.W*px/c.width, water.H*py/c.height,0);//,z(floor(px*DIVS/width),floor(py*DIVS/height)));
        orient();
        c.rotateZ(theta);
        //rotateX(-phi);
        //shape(boatModel);
        c.fill(202, 191, 230);
        c.box(s*30, s*10, s*10);
        c.fill(230, 210, 240);
        c.box(s*20, s*6, s*20);
        c.popMatrix();
    }
    
    public void prop(float p) {
        prop = constrain(p,0,20);
    }
    
    public void addProp(float dp) {
        prop(prop+dp);
    }
    
    public float px() {
        return px;
    }
    
    public float py() {
        return py;
    }
    
    //TODO: Buoyancy sim
    float ox=0,oy=0,oz=0;
    //move and rotate boat onto waves
    public void orient() {
        int i0 = floor(water.DIVS*px/c.width), j0 = floor(water.DIVS*py/c.height);
        float z = 0,
              zn = 0,
              ze = 0,
              zw = 0,
              zs = 0;
        int s = 20;
        for(int i=i0-s;i<i0;i++) 
            for(int j=j0-s;j<=j0+s;j++)
                zw += water.z(i,j);
        zw /= 2*s*s+s;

        for(int i=i0-s;i<=i0+s;i++)
            for(int j=j0+1;j<=j0+s;j++)
                zn += water.z(i,j);
        zn /= 2*s*s+s;

        for(int i=i0-s;i<=i0+s;i++)
            for(int j=j0-s;j<j0;j++)
                zs += water.z(i,j);
        zs /= 2*s*s+s;

        for(int i=i0+1;i<=i0+s;i++) 
            for(int j=j0-s;j<=j0+s;j++)
                ze += water.z(i,j);
        ze /= 2*s*s+s;

        float avgZ = (zn+ze+zw+zs)/4;
        if(abs(avgZ-oz)<0.5)
            oz=avgZ;
        else
            oz+=(avgZ-oz)*0.5;
        c.translate(0,0,oz);


        //ox*=0.99;
        //oy*=0.99;

        float ox_ = -(atan(ze)+atan(-zw))/2,
              oy_ = (atan(zn)+atan(-zs))/2,
              dox = ox_%(2*PI)-ox%(2*PI),
              doy = oy_%(2*PI)-oy%(2*PI),
              maxD = 0.05f;
        if(abs(dox)<maxD)
            ox=ox_;
        else
            ox+=maxD*Math.signum(dox);
        if(abs(doy)<maxD)
            oy=oy_;
        else
            oy+=maxD*Math.signum(doy);

        c.rotateX(-ox);
        c.rotateY(-oy*1);
    }
    
    //static boat functions
    public static void updateBoats() {
        boats.forEach(boat -> boat.update());
    }
    
    public static void drawBoats() {
        boats.forEach(boat -> boat.render());
    }
    
    public static void checkCollisions() {
        for (int i=0; i<boats.size(); i++) {
            Boat a = boats.get(i);
            for (int j=i+1; j<boats.size(); j++) {
                Boat b = boats.get(j);
                float difX = b.px-a.px, difY = b.py-a.py, 
                    dif = sqrt(difX*difX+difY*difY), 
                    difnX = difX/dif, difnY = difY/dif;
                if (dif<10) {
                    float aOnb = a.vx*difnX+a.vy*difnY, 
                          bOna = b.vx*difnX+b.vy*difnY, 
                          jxOna = bOna*difnX - aOnb*difnX, 
                          jyOna = bOna*difnY - aOnb*difnY, 
                          jMag = jxOna*jxOna+jyOna*jyOna;
                    a.vx += jxOna;
                    a.vy += jyOna;
                    b.vx -= jxOna;
                    b.vy -= jyOna;
                    if (jMag>0.9*6) water.explosion((a.px+b.px)/2, (a.py+b.py)/2, 10, jMag);

                    //shake camera if user collides
                    if (a==userBoat) 
                        shakeCamera(-jxOna,-jyOna);
                    else if(b==userBoat) 
                        shakeCamera(jxOna,jyOna);
                }
            }
        }
    }
}
