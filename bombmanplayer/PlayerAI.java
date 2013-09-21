import static com.orbischallenge.bombman.api.game.MapItems.*;
import Helpers.ExtraEntry;
import Helpers.PointHelper;
import Pathfinding.SearchNode;
import Pathfinding.SearchSet;

import com.orbischallenge.bombman.api.game.MapItems;
import com.orbischallenge.bombman.api.game.PlayerAction;
import com.orbischallenge.bombman.api.game.PowerUps;
import com.orbischallenge.bombman.protocol.BomberManProtocol.Position;
import com.orbischallenge.bombman.protocol.BomberManProtocol.PowerUp;
import com.sun.org.apache.xml.internal.security.keys.content.KeyValue;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import org.junit.experimental.theories.Theories.TheoryAnchor;

import sun.awt.image.ImageWatched.Link;

/**
 *
 * @author c.sham
 */
public class PlayerAI implements Player {

    MapState curState;
    Bomber curPlayer;
    
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
    	this.curState = new MapState(map, new HashMap<Point, Bomb>(), new HashMap<Point, PowerUps>(), players, new LinkedList<Point>());
    	this.curState.allBlocks = blocks;
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
    	List<Point> allBlocks = this.curState.allBlocks;
    	this.curState = new MapState(map, bombLocations, powerUpLocations, players, explosionLocations);
    	this.curState.allBlocks = allBlocks;
    	
        boolean bombMove = false;
        
        this.curPlayer = players[playerIndex];

        /**
         * Get Bomber's current position
         */
        Point curPosition = curPlayer.position;

        /**
         * Keep track of which blocks are destroyed
         */
        for (Point explosions : explosionLocations) {
            if (this.curState.allBlocks.contains(explosions)) {
                this.curState.allBlocks.remove(explosions);
            }
        }

        /**
         * Find which neighbors of Bomber's current position are currently unoccupied, so that I
         * can move into. Also counts how many blocks are neighbors.
         */
        List<Move.Direction> validMoves = new LinkedList<>();
        LinkedList<Move.Direction> neighborBlocks = new LinkedList<>();

        for (Move.Direction move : Move.getAllMovingMoves()) {
            int x = curPosition.x + move.dx;
            int y = curPosition.y + move.dy;

            if (map[x][y].isWalkable() && this.isSafeToMoveToPosition(new Point(x, y))) {
                validMoves.add(move);
            }
            
            if (this.curState.allBlocks.contains(new Point(x, y))) {
                neighborBlocks.add(move);
            }
        }

        /**
         * If there are blocks around and it's safe, I should place a bomb in my current square.
         */
        if (!neighborBlocks.isEmpty() && this.isSafeToPlaceBomb(curPosition)) {
        	
        	// Get a set of the neighboring open tiles.
        	SearchSet set = new SearchSet(curPosition, map, 5);
        	
        	// Look through all the tiles to see if any are safe with new bomb.
        	List<SearchNode> safeNodes = new LinkedList<>();
        	for (SearchNode node : set.nodes) {
            	// Create a theoretical bomb to consider when checking for safety. 
            	List<Entry<Point, Bomb>> theoreticalBombs = new LinkedList<Entry<Point, Bomb>>();
            	theoreticalBombs.add(new ExtraEntry<Point, Bomb>(curPlayer.position, new Bomb(curPlayer.playerIndex, curPlayer.bombRange, 15)));
        		
            	// Check if we have a safe spot. 
        		if (isSafeToMoveToPosition(node.position, theoreticalBombs)) {
        			safeNodes.add(node);
        		}
        	}
        	
        	if (safeNodes.size() > 0) {
        	
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

    private boolean isSafeToPlaceBomb(Point position) {
    	return this.curState.bombLocations.keySet().size() == 0;
	}
    
    private boolean isSafeToMoveToPosition(Point position) {
    	return isSafeToMoveToPosition(position, new LinkedList<Entry<Point, Bomb>>());
    }
    
    private boolean isSafeToMoveToPosition(Point position, List<Entry<Point, Bomb>> theoreticalBombs) {
    	if (this.curState.explosionLocations.contains(position)) {
    		return false;
    	}
    	
    	// Append real bombs to the theoretical bombs.
    	theoreticalBombs.addAll(this.curState.bombLocations.entrySet());
    	
    	for (Entry<Point, Bomb> pair : theoreticalBombs) {
    		MockBomb bomb = new MockBomb(pair.getKey(), pair.getValue());
    		
    		if (bomb.isAboutToHitPosition(position)) {
    			return false;
    		}
    	}

    	return true;
    }
    
    private int howManyBlocksWillBombDestroy(Point position, int bombRange) {
    	int numDestroyed = 0;
    	boolean hitsLeft = false;
    	boolean hitsRight = false;
    	boolean hitsUp = false;
    	boolean hitsDown = false;
    	for (int i = 1; i <= bombRange; i++) {
    		Point leftPosition = new Point(position.x - i, position.y);
    		Point rightPosition = new Point(position.x + i, position.y);
    		Point upPosition = new Point(position.x, position.y - i);
    		Point downPosition = new Point(position.x + i, position.y + i);
    		
    		if (!hitsLeft) {
    			if (this.curState.allBlocks.contains(leftPosition)) {
    				hitsLeft = true;
    				numDestroyed++;
    			}
    		}
    		
    		if (!hitsRight) {
    			if (this.curState.allBlocks.contains(rightPosition)) {
    				hitsRight = true;
    				numDestroyed++;
    			}
    		}
    		
    		if (!hitsUp) {
    			if (this.curState.allBlocks.contains(upPosition)) {
    				hitsUp = true;
    				numDestroyed++;
    			}
    		}
    		
    		if (!hitsDown) {
    			if (this.curState.allBlocks.contains(downPosition)) {
    				hitsDown = true;
    				numDestroyed++;
    			}
    		}
    	}
    
    	System.out.println("Bomb will destroy this many blocks: " + numDestroyed);
    	return numDestroyed;
    }

	/**
     * Uses Breadth First Search to find if a walkable path from point A to point B exists.
     *
     * This method does not consider the if tiles are dangerous or not. As long as all the tiles in
     * are walkable.
     *
     * @param start The starting point
     * @param end The end point
     * @param map The map use to check if a path exists between point A and point B
     * @return True if there is a walkable path between point A and point B, False otherwise.
     */
	public boolean isThereAPath(Point start, Point end, MapItems[][] map) {
        //Keeps track of points we have to check
        Queue<Point> open = new LinkedList<>();

        //Keeps track of points we have already visited
        List<Point> visited = new LinkedList<>();

        open.add(start);
        while (!open.isEmpty()) {
            Point curPoint = open.remove();

            //Check all the neighbours of the current point in question
            for (Move.Direction direction : Move.getAllMovingMoves()) {
                int x = curPoint.x + direction.dx;
                int y = curPoint.y + direction.dy;

                Point neighbour = new Point(x, y);

                // if the point is the destination, we are done
                if (end.equals(neighbour)) {
                    return true;
                }

                // if we have already visited this point, we skip it
                if (visited.contains(neighbour)) {
                    continue;
                }
                
                // if the point isn't safe, we skip it
                if (!this.isSafeToMoveToPosition(neighbour)) {
                	continue;
                }

                // if bombers can walk onto this point, then we add it to the list of points we should check
                if (map[x][y].isWalkable()) {
                    open.add(neighbour);
                }

                // add to visited so we don't check it again
                visited.add(neighbour);
            }
        }
        return false;
    }

    /**
     * Returns the Manhattan Distance between the two points.
     *
     * @param start the starting point
     * @param end the end point
     * @return the Manhattan Distance between the two points.
     */
    public int manhattanDistance(Point start, Point end) {
        return (Math.abs(start.x - end.x) + Math.abs(start.y - end.y));
    }
}
