
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

// Note: Sort of hacky - only compare nodes within the same search set.

public class SearchNode {
	
	public Point position;
	public SearchNode previous;
	public int distance;

	public SearchNode(Point position, int distance, SearchNode previous) {
		this.position = position;
		this.previous = previous;
		this.distance = distance;
	}
	
	public SearchNode(Point position) {
		this(position, 0, null);
	}
	
	public List<SearchNode> buildPathList() {
		List<SearchNode> _return = new LinkedList<SearchNode>();
		
		SearchNode curNode = this;
		while (curNode != null) {
			_return.add(0, curNode);
			curNode = curNode.previous;
		}
		
		return _return;
	}
	
	// NOTE: This is a little hack
	@Override
	public boolean equals(Object obj) {
		return this.position.equals(((SearchNode)obj).position);
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
}