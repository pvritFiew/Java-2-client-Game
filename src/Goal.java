import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class Goal {
    private double x ,y, size = 30;
    private Color color = Color.GREEN;

    public Goal(double a ,double b){
        x =  a;
        y =  b;
    }
    public void drawGoal(Graphics2D g2d){
    	RoundRectangle2D.Double circle =new RoundRectangle2D.Double(x,y,size,size,size,size);
        g2d.setColor(color);
        g2d.fill(circle);
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