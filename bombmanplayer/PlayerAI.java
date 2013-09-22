import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

import com.orbischallenge.bombman.api.game.MapItems;
import com.orbischallenge.bombman.api.game.PlayerAction;
import com.orbischallenge.bombman.api.game.PowerUps;
import com.orbischallenge.bombman.protocol.BomberManProtocol.PlayerMessageOrBuilder;

/**
 * @author c.sham
 */
public class PlayerAI implements Player {
	
    /**
     * Gets called every time a new game starts.
     *
     * @param map The map.
     * @param blocks All the blocks on the map.
     * @param players Current position` bomb range, and bomb count for both Bombers.
     * @param playerIndex Your player index.
     */
    @Override
    public void newGame(MapItems[][] map, List<Point> blocks, Bomber[] players, int playerIndex) {
    	// Do nothing on game setup.

    	// new MapState(map, new HashMap<Point, Bomb>(), new HashMap<Point, PowerUps>(), players, new LinkedList<Point>());
    }

    /**
     * Gets called every time a move is requested from the game server.
     *
     * Provided is a very random and not smart AI which random moves without checking for
     * explosions, and places bombs whenever bombs can be used to destroy blocks.
     *
     * @param map The current map
     * @param bombLocations Bombs currently on the map and it's range, owner and time left Exploding
     * bombs are excluded.
     * @param powerUpLocations Power-ups current on the map and it's type
     * @param players Current position, bomb range, and bomb count for both Bombers
     * @param explosionLocations Explosions currently on the map.
     * @param playerIndex Your player index.
     * @param moveNumber The current move number.
     * @return the PlayerAction you want your Bomber to perform.
     */
    @Override
    public PlayerAction getMove(MapItems[][] map, HashMap<Point, Bomb> bombLocations, HashMap<Point, PowerUps> powerUpLocations, Bomber[] players, List<Point> explosionLocations, int playerIndex, int moveNumber) {
    	
    	// Keep track of yolos.
    	yoloJustBecauseWeCan();
    	
    	// Collect the state. 
    	MapState state = new MapState(map, bombLocations, powerUpLocations, players, explosionLocations);
    	Bomber curPlayer = state.getPlayer(playerIndex);
    	Point curPosition = curPlayer.position;
    	
    	MovePlan plan = new MovePlan(state);
    	
    	// Build a state analyzer.
    	MapAnalyzer analyzer = new MapAnalyzer(state);
    	
        boolean bombMove = false;
        
        /**
         * Find which neighbors of Bomber's current position are currently unoccupied, so that I
         * can move into. Also counts how many blocks are neighbors.
         */
        List<Move.Direction> validMoves = new LinkedList<>();
        LinkedList<Move.Direction> neighboringDestructableBlocks = new LinkedList<>();

        for (Move.Direction move : Move.getAllMovingMoves()) {
            int x = curPosition.x + move.dx;
            int y = curPosition.y + move.dy;

            if (map[x][y].isWalkable() && analyzer.isSafeToMoveToPosition(new Point(x, y))) {
                validMoves.add(move);
            }
            
            if (state.getMapItem(new Point(x, y)) == MapItems.BLOCK) {
                neighboringDestructableBlocks.add(move);
            }
        }

        /**
         * If there are blocks around and it's safe, I should place a bomb in my current square.
         */
        if (!neighboringDestructableBlocks.isEmpty() && state.getBombs(curPlayer.playerIndex).size() == 0) {
        	
        	// Get a set of the neighboring open tiles.
        	SearchSet set = new SearchSet(curPosition, map, 5);
        	
        	// Look through all the tiles to see if any are safe with new bomb.
        	List<SearchNode> safeNodes = new LinkedList<>();
        	for (SearchNode node : set.nodes) {
        		
            	// Create a theoretical bomb to consider when checking for safety. 
            	List<MockBomb> theoreticalBombs = new LinkedList<MockBomb>();
            	theoreticalBombs.add(new MockBomb(curPlayer));
        		
            	// Check if we have a safe spot. 
        		if (analyzer.isSafeToMoveToPosition(node.position, theoreticalBombs)) {
        			safeNodes.add(node);
        		}
        	}
        	
        	if (safeNodes.size() > 0) {
        		// If there are safe nodes, then throw a bomb and run in the right direction!
	        	List<Move.Direction> runFromBombDirections = new LinkedList<>();
	        	for (SearchNode node : safeNodes) {
	        		List<SearchNode> path = node.buildPathList();
	        		if (path.size() > 1) {
	        			SearchNode firstNode = path.get(0);
	        			SearchNode secondNode = path.get(1);
	        			Point diff = PointHelper.sub(secondNode.position, firstNode.position);
	        			Move.Direction direction = Move.getDirection(diff.x, diff.y);
	        			runFromBombDirections.add(direction);
	        		}
	        	}
	        	
	        	List<Move.Direction> finalValidMoves = new LinkedList<>();
	        	for (Move.Direction move : Move.getAllMovingMoves()) {
	        		if (validMoves.contains(move) && runFromBombDirections.contains(move))
	        			finalValidMoves.add(move);
	        	}
	        	
	        	validMoves = finalValidMoves;
	            bombMove = true;
        	}
        }

        /**
         * There's no place to go, I'm stuck. :(
         */
        if (validMoves.isEmpty()) {
            return Move.still.action;
        }

        /**
         * There is some place I could go, so I randomly choose one direction and go off in that
         * direction.
         */
        Move.Direction move = validMoves.get((int) (Math.random() * validMoves.size()));

        if (bombMove) {
            return move.bombaction;
        }
        
        return move.action;
    }

	private void yoloJustBecauseWeCan() {
		PrintWriter writer;
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter("lolyolosrslyplz.txt", true)));
			writer.println("YOLO-ing all night long! YOLO Timestamp: " + new Date().toString());
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.print("GOD! YOLO!");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.print("LOL! YOLO!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.print("I can't stop the YOLO!");
			e.printStackTrace();
		}
	}
}
