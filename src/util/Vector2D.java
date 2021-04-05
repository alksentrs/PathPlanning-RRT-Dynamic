package util;

public class Vector2D extends Point2D {

    public Vector2D() {
    }

    public Vector2D(double x, double y) {
        super(x,y);
    }

    public void rotate(double rad){
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        setLocation(x*cos - y*sin, x*sin + y*cos);
    }

    public Vector2D scale(double scale) {
        return new Vector2D(x*scale, y*scale);
    }

    public void selfScale(double scale) {
        x *= scale;
        y *= scale;
    }


    public static Vector2D subtract(Point2D a, Point2D b) {
        return new Vector2D(a.getX()- b.getX(),a.getY()- b.getY());
    }

    public Vector2D unitVector() {
        double dist = Math.sqrt(x*x+y*y);
        return new Vector2D(x/dist,y/dist);
    }

    public void normalize() {
        double dist = Math.sqrt(x*x+y*y);
        x /= dist;
        y /= dist;
    }
}
