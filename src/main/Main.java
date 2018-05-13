
package main;

import static main.Game.*;
import processing.core.PApplet;
import processing.event.MouseEvent;

/**
 *
 * @author Nithin Pranesh
 */
public final class Main extends PApplet {

    public static PApplet c;
    public static final int WIDTH=800, HEIGHT=600, FPS=30;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PApplet.main("main.Main");
    }
    
    @Override
    public void settings() {
        size(WIDTH,HEIGHT,P3D);
    }
    
    @Override
    public void setup() {
        frameRate(FPS);
        noStroke();
        c = this;
        init();
    }
    
    @Override
    public void draw() {
        tick();
        render();
    }
    
    //TODO: move forward all input to Input class
    @Override
    public void mouseWheel(MouseEvent e) {
        userBoat.addProp(-e.getCount());
    }
    
    @Override
    public void mousePressed() {
        water.explosion(mouseX, mouseY, 15, 15);
    }
    
    @Override
    public void keyPressed() {
        if(key=='t')
            topDown = !topDown;
    }
}
