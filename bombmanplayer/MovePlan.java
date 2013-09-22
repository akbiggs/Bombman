import java.util.List;

import com.orbischallenge.bombman.api.game.PlayerAction;

public class MovePlan {
	
	public boolean willPutBomb;
    public List<Move.Direction> safeDirections;
    
	public MovePlan() {
		this.willPutBomb = false;
		this.safeDirections = Move.getAllMovingMoves();
	}
	
	public PlayerAction getPlayerActionFromPlan() {
        if (safeDirections.isEmpty()) {
            return Move.still.action;
        }
        
		Move.Direction move = safeDirections.get((int) (Math.random() * safeDirections.size()));
		if (willPutBomb) {
			return move.bombaction;
		} else {
			return move.action;
		}
	}
}