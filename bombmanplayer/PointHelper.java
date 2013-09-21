import java.awt.Point;


public class PointHelper {
	private PointHelper() {
		
	}
	
	public static Point add(Point p1, Point p2) {
		return new Point(p1.x + p2.x, p1.y + p2.y);
	}
	
	public static Point sub(Point p1, Point p2) {
		return new Point(p1.x - p2.x, p1.y - p2.y);
	}

	public static Point mul(Point p1, Point p2) {
		return new Point(p1.x * p2.x, p1.y * p2.y);
	}
	
	public static int manhattenDistance(Point p1, Point p2) {
		return (Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y));
		
	}
}
