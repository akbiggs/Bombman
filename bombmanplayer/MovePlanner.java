import java.awt.Point;
import java.awt.datatransfer.FlavorTable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.print.attribute.IntegerSyntax;

import com.orbischallenge.bombman.api.game.MapItems;
import com.orbischallenge.bombman.api.game.PlayerAction;
import com.orbischallenge.bombman.api.game.Game.GameState;
import com.orbischallenge.bombman.protocol.BomberManProtocol.MoveResponse;

public class MovePlanner {

	private static boolean jumpingSharkEnabled = true;
	
	MapState state;
	MapAnalyzer analyzer;
	Brain brain;
	
	public MovePlanner(MapState state, Brain brain) {
		this.state = state;
		this.brain = brain;
		this.analyzer = new MapAnalyzer(state);
	}
	
	public PlayerAction planMove() {
		
		MovePlan plan = new MovePlan();
        removeInvalidMovesFromPlan(plan);
        
    	if (brain.getGoal() == Brain.Goal.EscapeDanger)
    		return handleEmergencyDanger(plan);
        
        decideIfToDropBomb(plan, brain.tryToForceBomb);
     
        if (brain.hasPath()) { 
        	if (!brain.askMovePlanToFollowPath(plan) && jumpingSharkEnabled) {
        		if (brain.tryJumpShark(state))
    				return PathHelper.GetMoveDirectionForBeginningOfPath(brain.path).action;
        	}
        }
        

        return plan.getPlayerActionFromPlan();
	}

	private PlayerAction handleEmergencyDanger(MovePlan plan) {
		if (PlayerAI.DEBUGGING) System.out.println("Handling emergency danger!");
		Move.Direction escapeWay = PathHelper.GetMoveDirectionForBeginningOfPath(brain.path);
		if (plan.safeDirections.contains(escapeWay)) {
			return escapeWay.action;
		} else {
			// The way to escape is dangerous. We break through unless spot is just about to explode.
			int bombTime = analyzer.timeUntilExplosionAtPosition(
					PointHelper.add(state.getMainPlayer().position, new Point(escapeWay.dx, escapeWay.dy))
					, state.map);
			if (bombTime > 2)
				return escapeWay.action;
			else
				return Move.still.action;
		}
	}
	
	private void removeInvalidMovesFromPlan(MovePlan plan) {
		Point curPosition = state.getMainPlayer().position;
        for (Move.Direction move : Move.getAllMovingMoves()) {
            int x = curPosition.x + move.dx;
            int y = curPosition.y + move.dy;

            if (!state.map[x][y].isWalkable())
                plan.safeDirections.remove(move);
            
            if (!analyzer.isSafeFromExplosionsAtPosition(new Point(x, y), state.map))
            	plan.safeDirections.remove(move);
        }
	}
	
	private void decideIfToDropBomb(MovePlan plan, boolean tryForce) {
		
		// Only drop a bomb if a wall is nearby to destroy.
		boolean flag = false;
		Point curPosition = state.getMainPlayer().position;
		for (Move.Direction move : Move.getAllMovingMoves()) {
            int x = curPosition.x + move.dx;
            int y = curPosition.y + move.dy;
	        if (state.getMapItem(new Point(x, y)) == MapItems.BLOCK) {
	            flag = true;
	        }
		}
		
		if (!flag && !tryForce && !analyzer.opponentWithinRange(curPosition, state))
			return;
		
		if (PlayerAI.DEBUGGING) System.out.println("Checking if it is safe to place bomb?");
		
		// Don't drop two bombs at once.
		//if (state.getBombs(state.playerIndex).size() > 0)
		//	return;

    	// Get a set of the neighboring open tiles.
    	SearchSet set = new SearchSet(curPosition, state.map, 5);
    	
    	// Look through all the tiles to see if any are safe with new bomb.
    	List<PathNode> safeNodes = new LinkedList<>();
    	for (PathNode node : set.nodes) {
    		
        	// Create a theoretical bomb to consider when checking for safety. 
        	List<MockBomb> theoreticalBombs = new LinkedList<MockBomb>();
        	theoreticalBombs.add(new MockBomb(state.getMainPlayer()));
    		
        	// Check if we have a safe spot nearby after placing the bomb.
    		if (analyzer.timeUntilExplosionAtPosition(node.position, state.map, theoreticalBombs) > MockBomb.DETONATION_TIME) {
    			safeNodes.add(node);
    		}
    	}
    	
    	if (safeNodes.size() == 0)
    		return;
    	if (PlayerAI.DEBUGGING) System.out.println("Committing to placing bomb!"); 
    		
		// If there are safe nodes, then throw a bomb and run in the right direction!
    	List<Move.Direction> runFromBombDirections = new LinkedList<>();
    	for (PathNode node : safeNodes) {
    		List<PathNode> path = node.buildPathList();
    		if (path.size() > 1) {
    			runFromBombDirections.add(PathHelper.GetMoveDirectionForBeginningOfPath(path));
    		}
    	}
    	
    	// Perform union
    	HashSet<Move.Direction> finalValidMoves = new HashSet<>();
    	for (Move.Direction move : Move.getAllMovingMoves()) {
    		if (plan.safeDirections.contains(move) && runFromBombDirections.contains(move))
    			finalValidMoves.add(move);
    	}
    	
    	plan.safeDirections = finalValidMoves;
        plan.willPutBomb = true;
	}
}
