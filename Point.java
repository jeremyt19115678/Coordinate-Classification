import java.awt.Color;

public class Point{
    public double x, y;
    public boolean top;
    public boolean neutral;
    public boolean sample;

    public Point(double a, double b){
        x = a;
        y = b;
        top = false;
        neutral = true;
        sample = true;
    }

    public void draw(){
    	if (sample) {
	    	if (neutral)
	    		StdDraw.setPenColor(StdDraw.BLACK);
	    	else if (top)
	            StdDraw.setPenColor(StdDraw.RED);
	        else    
	            StdDraw.setPenColor(StdDraw.GREEN);
    	}else {
    		if (top)
    			StdDraw.setPenColor(StdDraw.PINK);
    		else
    			StdDraw.setPenColor(new Color(87,249,203));
    	}
        StdDraw.filledCircle(x,y,3);
    }
    
    public void draw(Color color) {
    	StdDraw.setPenColor(color);
    	StdDraw.filledCircle(x,y,3);
    }
    
    public void setTop() {
    	top = true;
    	neutral = false;
    }
}