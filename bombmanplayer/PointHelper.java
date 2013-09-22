
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
	
    /**
     * Returns the Manhattan Distance between the two points.
     *
     * @param start the starting point
     * @param end the end point
     * @return the Manhattan Distance between the two points.
     */
    public static int manhattanDistance(Point start, Point end) {
        return (Math.abs(start.x - end.x) + Math.abs(start.y - end.y));
    }
}
