import java.awt.Point;
import java.util.HashMap;
import java.util.List;

import com.orbischallenge.bombman.api.game.MapItems;
import com.orbischallenge.bombman.api.game.PowerUps;


public class MapState {

    public List<Point> allBlocks;

    public MapItems[][] map;
    public HashMap<Point, Bomb> bombLocations;
    public HashMap<Point, PowerUps> powerUpLocations;
    public Bomber[] players;
    public List<Point> explosionLocations;
    public int playerIndex;
    public int moveNumber;

    public MapState(MapItems[][] map, HashMap<Point, Bomb> bombLocations, HashMap<Point, PowerUps> powerUpLocations, Bomber[] players, List<Point> explosionLocations) {
    	this.map = map;
    	this.bombLocations = bombLocations;
    	this.powerUpLocations = powerUpLocations;
    	this.players = players;
    	this.explosionLocations = explosionLocations;
    }
    
    public MapState clone() {
    	return new MapState((MapItems[][])map.clone(), new HashMap<Point, Bomb>(bombLocations), new HashMap<Point, PowerUps>(powerUpLocations), (Bomber[])players.clone(), (List<Point>)explosionLocations);
    }
}