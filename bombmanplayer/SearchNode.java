import java.awt.Point;

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