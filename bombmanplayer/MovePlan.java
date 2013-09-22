import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.orbischallenge.bombman.api.game.PlayerAction;

public class MovePlan {
	
	public boolean willPutBomb;
    public HashSet<Move.Direction> safeDirections;
    
	public MovePlan() {
		this.willPutBomb = false;
		this.safeDirections = new HashSet<Move.Direction>(Move.getAllMovingMoves());
	}
	
	public PlayerAction getPlayerActionFromPlan() {
        if (safeDirections.isEmpty()) {
            return Move.still.action;
        }
        
        // TODO: could add heurisitic here to get out of sticky situations
		Move.Direction move = new ArrayList<Move.Direction>(safeDirections).get((int) (Math.random() * safeDirections.size()));
		if (willPutBomb) {
			return move.bombaction;
		} else {
			return move.action;
		}
	}
}