import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Player {
    private double x ,y, size;
    private Color color;

    public Player(double a ,double b ,double s,Color c){
        x =a;
        y=b;
        size =s;
        color =c;
    }
    public void drawSprite(Graphics2D g2d){
    	RoundRectangle2D.Double circle =new RoundRectangle2D.Double(x,y,size,size,size,size);
        g2d.setColor(color);
        g2d.fill(circle);
    }
    public void moveH(double n){
        x+=n;
    }
    public void moveV(double n){
        y+=n;
    }
    public void setX(double n){
        x=n;
    }
    public void setY(double n){
        y=n;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
}