
public class MovePlan {
	
	public MapState mapState;
	
	public boolean willPutBomb;
	
	public MovePlan(MapState mapState) {
		this.mapState = mapState;
		
		this.willPutBomb = false;
	}
}
