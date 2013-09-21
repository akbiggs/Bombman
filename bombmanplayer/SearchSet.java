import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.orbischallenge.bombman.api.game.MapItems;

public class SearchSet {

	public List<SearchNode> nodesInSet;

	public SearchSet(Point start, MapItems[][] map, int searchDistance) {
		this.nodesInSet = buildSet(start, map, searchDistance);
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
	public List<SearchNode> buildSet(Point start, MapItems[][] map, int searchDistance) {

		// Keeps track of the nodes we accept into our set
		ArrayList<SearchNode> accepted = new ArrayList<SearchNode>();

		// Keeps track of points we have to evaluate
		Queue<SearchNode> open = new LinkedList<>();

		// Keeps track of points we have already seen and should ignore
		HashSet<SearchNode> visited = new HashSet<>();

		// Throw the first point onto the queue to consider
		SearchNode startingNode = new SearchNode(start, 0, null);
		open.add(startingNode);
		visited.add(startingNode);

		// Evaluate each node in the open queue one by one
		while (!open.isEmpty()) {

			SearchNode curNode = open.remove();

			// Check if the current node is acceptable
			if (map[curNode.position.x][curNode.position.y].isWalkable())
				accepted.add(curNode);

			// Consider all the neighbors of the current point in question
			for (Move.Direction direction : Move.getAllMovingMoves()) {
				int x = curNode.position.x + direction.dx;
				int y = curNode.position.y + direction.dy;

				SearchNode neighbour = new SearchNode(new Point(x, y),
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
