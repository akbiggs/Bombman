import java.awt.Point;
import java.util.List;

public class PathHelper {
	public static Move.Direction GetMoveDirectionForBeginningOfPath(List<PathNode> path) {
		if (path.size() <= 1)
			return Move.still;
		PathNode firstNode = path.get(0);
		PathNode secondNode = path.get(1);
		Point diff = PointHelper.sub(secondNode.position, firstNode.position);
		return Move.getDirection(diff.x, diff.y);
	}
}
