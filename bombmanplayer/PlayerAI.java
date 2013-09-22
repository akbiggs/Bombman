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
	
	// The brain stores the overall goals of the current player.
	Brain brain;
	
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
    	this.brain = new Brain();
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
    	// yoloJustBecauseWeCan();
    	// Need to stop yolo-ing to make room for more configuration.
    	
    	// Collect the state. 
    	MapState state = new MapState(map, bombLocations, powerUpLocations, players, playerIndex, explosionLocations);
    	
    	// Update the goal.
    	brain.updateGoals(state);

    	// Delegate the move to the planner.
    	MovePlanner planner = new MovePlanner(state, this.brain);
        return planner.planMove();
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
