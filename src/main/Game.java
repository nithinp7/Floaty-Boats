
package main;

import entities.Boat;
import fluid.Water;
import java.util.ArrayList;
import static main.Main.*;
import static entities.Boat.*;

/**
 *
 * @author Nithin Pranesh
 */
public final class Game {
    
    static boolean topDown = false;
    
    public static Water water;
    public static ArrayList<Boat> boats = new ArrayList<>();
    
    public static Boat userBoat;
    
    public static void init() {
        water = new Water(400, c.width, c.height);
        userBoat = new Boat(c.width/2,c.height/2);
        boats.add(userBoat);
        for(int i=7;i<7;i++) {
            Boat bt = new Boat(c.width*0.1f*i, c.height*0.75f);
            bt.prop(12+0.5f*i);
            boats.add(bt);
        } 
    }
    
    public static void tick() {
        water.updateWave();
        updateBoats();
        checkCollisions();
//        water.updateRain();
        water.stepSim(1f/FPS);
    }
    
    public static void render() {
        c.background(0);
        c.translate(c.width/2, c.height/2);
        float z=1;
        if(!topDown) c.translate(0,0,z*200);
        c.pushMatrix();
            if(!topDown) c.rotateX(PI/3);
            c.directionalLight(200,200,200,-1,-1,-1);
            if(topDown)
                c.translate(-water.W/2,-water.H/2);
            else
                c.translate(-userBoat.px(),-userBoat.py());
            float t=c.millis()/1000f-shakeTime;
            c.translate(-2*shakeX*cos(2*PI*t)*exp(-t),-2*shakeY*cos(2*PI*t)*exp(-t));
            water.draw();
            drawBoats();
            c.translate(water.W*c.mouseX/c.width,water.H*c.mouseY/c.height, 8);
            c.stroke(255,0,0);
            c.noFill();
            c.ellipse(0, 0, 20,20);
            c.noStroke();
        c.popMatrix();
    }
    
    
    //camera shake
    private static float shakeX=0, shakeY=0, shakeTime=0;
    public static void shakeCamera(float dx, float dy) {
        shakeX=dx;
        shakeY=dy;
        shakeTime=c.millis()/1000f;
    }
}
