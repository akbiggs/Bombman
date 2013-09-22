
import java.awt.Point;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

// Note: Sort of hacky - any node that has the same position is considered equal.
// Thus, each search node should be contained within a single a map state.

public class PathNode {
	
	public Point position;
	public PathNode previous;
	public int distance;

	public PathNode(Point position, int distance, PathNode previous) {
		this.position = position;
		this.previous = previous;
		this.distance = distance;
	}
	
	public PathNode(Point position) {
		this(position, 0, null);
	}
	
	public List<PathNode> buildPathList() {
		List<PathNode> _return = new LinkedList<PathNode>();
		
		
		PathNode curNode = this;
		while (curNode != null) {
			_return.add(0, curNode);
			curNode = curNode.previous;
		}
		
		return _return;
	}
	
	// NOTE: This is a little hack
	@Override
	public boolean equals(Object obj) {
		return this.position.equals(((PathNode)obj).position);
	}
	
	// NOTE: This is a little hack, two nodes with the same position are considered equal
	@Override
	public int hashCode() {
		return this.position.hashCode();
	}
	
	@Override
	public String toString() {
		return position.toString();
	}

	public static class DistanceCompare implements Comparator<PathNode> {
	    @Override
	    public int compare(PathNode o1, PathNode o2) {
	        return o1.distance - o2.distance;
	    }
	}
}