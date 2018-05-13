
package fluid;

import static main.Main.c;
import static processing.core.PApplet.*;
import static processing.core.PConstants.QUADS;
import java.util.Random;

/**
 *
 * @author Nithin Pranesh
 */
public class Fluid {
    
    public final int DIVS;
    public final float W, H, dx, dy, damping, waveSpeed;
    
    private float[][] z_t1, z_t0;
    
    private final Random r = new Random();
    
    public Fluid(int dvs, float w, float h, float dmp, float ws) {
        DIVS=dvs;
        W=w;
        H=h;
        dx=W/DIVS;
        dy=H/DIVS;
        damping=dmp;
        waveSpeed=ws;
        
        z_t1 = new float[DIVS][DIVS];
        z_t0 = new float[DIVS][DIVS];
        
        for(int i=0;i<DIVS;i++)
            for(int j=0;j<DIVS;j++)
                z_t1[i][j] = z_t0[i][j] = 0;
    }
    
    public void setZ(int i, int j, float z) {
        if(i>=DIVS) 
            i = 2*DIVS-i-1;
        if(j>=DIVS)
            j = 2*DIVS-j-1;
        if(i<0)
            i *= -1;
        if(j<0)
            j *= -1;
        //if(i<0||i>=DIVS||j<0||j>=DIVS)return 0;
        z_t1[i][j] = z;
    }

    public float z(int i, int j) {
        if(i>=DIVS) 
            i = 2*DIVS-i-1;
        if(j>=DIVS)
            j = 2*DIVS-j-1;
        if(i<0)
            i *= -1;
        if(j<0)
            j *= -1;
        //if(i<0||i>=DIVS||j<0||j>=DIVS)return 0;
        return z_t1[i][j];
    }

    public float z0(int i, int j) {
        if(i>=DIVS) 
            i = 2*DIVS-i-1;
        if(j>=DIVS)
            j = 2*DIVS-j-1;
        if(i<0)
            i *= -1;
        if(j<0)
            j *= -1;
        //if(i<0||i>=DIVS||j<0||j>=DIVS)return 0;
        return z_t0[i][j];
    }
    
    public void stepSim(float dt) {
        float[][] z_=new float[DIVS][DIVS];
        for(int i=0;i<DIVS;i++)
            for(int j=0;j<DIVS;j++) {
                z_[i][j] = waveSpeed*waveSpeed*dt*dt
                          *(z(i+1,j)+z(i-1,j)+z(i,j+1)+z(i,j-1)-4*z(i,j))
                          +damping*(-z0(i,j)+2*z(i,j));


            //z_[i][j] = z(i,j) + (1-damping*dt)*(z(i,j)-z0(i,j))
            //          + pow(dt*waveSpeed/dx,2)*
            //          (4*z(i,j)-z(i+1,j)-z(i-1,j)-z(i,j+1)-z(i,j-1)
            //          +0.5*(4*z(i,j)-z(i+1,j+1)-z(i+1,j-1)-z(i-1,j+1)-z(i-1,j-1)));
          }
        z_t0 = z_t1;
        z_t1 = z_;
    }
    
    public void updateWave() {
        float s=0.01f,a=0.05f, t=c.millis()/1000f;
        for(int i=0;i<DIVS;i++)
            for(int j=0;j<DIVS;j++) {
                z_t1[i][j]+=a-2*a*round(c.noise(i*s, j*s,t));
                ///z_t0[i][j]+=0.1-0.05*noise(i*s, j*s,t);
            }
    }
    
    public void updateRain() {
        c.stroke(0, 160, 221);
        c.strokeWeight(5);
        for(int i_=0;i_<10;i_++)
            if(r.nextBoolean()) {
                int i=floor(r.nextFloat()*DIVS),
                    j=floor(r.nextFloat()*DIVS);
                float x=W*i/DIVS,
                      y=H*j/DIVS;
                setZ(i,j,0.2f-0.4f*r.nextFloat());
                c.line(x,y,r.nextFloat()*100,x,y,r.nextFloat()*100);
            }
        c.noStroke();
    }
    
    public void explosion(float x, float y, float r, float h) {
        int i0=floor(1f*x*DIVS/W),
            j0=floor(1f*y*DIVS/H);
        for(int i=i0-ceil(r);i<i0+r;i++)
            for(int j=j0-ceil(r);j<j0+r;j++)
                if(sqrt(pow(i-i0,2)+pow(j-j0,2))<r)
                    setZ(i,j,(pow(i-i0,2)+pow(j-j0,2))/r/r*h);
    }
    
    public void draw() {
        float s=1;
        c.beginShape(QUADS);
        c.fill(0, 160, 221);
        for(int i=0;i<DIVS-1;i++) {
            float x=i*W/DIVS, x1=x+W/DIVS;
            for(int j=0;j<DIVS-1;j++) {
                float y=j*H/DIVS, y1=y+H/DIVS;
                c.vertex(x, y, s*z(i,j));
                c.vertex(x1, y, s*z(i+1,j));
                c.vertex(x1, y1, s*z(i+1,j+1));
                c.vertex(x, y1, s*z(i,j+1));
            }
        }
        c.endShape();
    }
}
