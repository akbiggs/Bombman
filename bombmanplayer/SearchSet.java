
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.print.attribute.standard.Destination;

import com.orbischallenge.bombman.api.game.MapItems;

public class SearchSet {

	// The nodes that can be reached from this search.
	public List<PathNode> nodes;
	public HashMap<Point, PathNode> map;
	public Point destination;

	public SearchSet(Point start, MapItems[][] map, int searchDistance) {
		this(start, map, searchDistance, null);
	}
	
	public SearchSet(Point start, MapItems[][] map, int searchDistance, Point destination) {
		this.destination = destination;
		this.nodes = buildSet(start, map, searchDistance);
		
		this.map = new HashMap<Point, PathNode>();
		for (PathNode node : nodes)
			this.map.put(node.position, node);
	}
	
	public List<PathNode> pathToDestination() {
		return pathToPoint(this.destination);
	}
	
	public List<PathNode> pathToPoint(Point point) {
		if (point == null)
			return null;
		if (!map.containsKey(point))
			return null;
		return map.get(point).buildPathList();
			
	}

	/**
	 * Returns a set of points that represent the walkable area around starting
	 * spot.
	 * 
	 * @param start
	 *            The starting point
	 * @param map
	 *            The map to analyze for points
	 */
	private List<PathNode> buildSet(Point start, MapItems[][] map, int searchDistance) {

		// Keeps track of the nodes we accept into our set
		ArrayList<PathNode> accepted = new ArrayList<PathNode>();

		// Keeps track of points we have to evaluate
		Queue<PathNode> open = new LinkedList<>();

		// Keeps track of points we have already seen and should ignore
		HashSet<PathNode> visited = new HashSet<>();

		// Throw the first point onto the queue to consider
		PathNode startingNode = new PathNode(start, 0, null);
		open.add(startingNode);
		visited.add(startingNode);

		// Evaluate each node in the open queue one by one
		while (!open.isEmpty()) {

			PathNode curNode = open.remove();

			// Check if the current node is acceptable
			if (map[curNode.position.x][curNode.position.y].isWalkable())
				accepted.add(curNode);
			
			if (destination != null && curNode.position.equals(destination))
				return accepted;
			
			// Consider all the neighbors of the current point in question
			for (Move.Direction direction : Move.getAllMovingMoves()) {
				int x = curNode.position.x + direction.dx;
				int y = curNode.position.y + direction.dy;

				PathNode neighbour = new PathNode(new Point(x, y),
						curNode.distance + 1, curNode);

				// if we have already visited this point, we skip it
				if (visited.contains(neighbour)) {
					continue;
				}

				// if bombers can walk onto this point, then we add it to the
				// list of points we should check
				if (neighbour.distance <= searchDistance && map[x][y].isWalkable()) {
					open.add(neighbour);
				}

				// add to visited so we don't check it again
				visited.add(neighbour);
			}
		}

		return accepted;
	}
}
