import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import sun.security.action.GetBooleanAction;

import com.orbischallenge.bombman.api.game.BombState;
import com.orbischallenge.bombman.api.game.MapItems;
import com.orbischallenge.bombman.api.game.PowerUps;


public class MapState {

    public MapItems[][] map;
    public HashMap<Point, Bomb> bombLocations;
    public HashMap<Point, PowerUps> powerUpLocations;
    public Bomber[] players;
    public List<Point> explosionLocations;

    public MapState(MapItems[][] map, HashMap<Point, Bomb> bombLocations, HashMap<Point, PowerUps> powerUpLocations, Bomber[] players, List<Point> explosionLocations) {
    	this.map = map;
    	this.bombLocations = bombLocations;
    	this.powerUpLocations = powerUpLocations;
    	this.players = players;
    	this.explosionLocations = explosionLocations;
    }
    
    public Bomber getPlayer(int playerIndex) {
    	return this.players[playerIndex];
    }
    
    public List<MockBomb> getBombs(int playerIndex) {
    	LinkedList<MockBomb> list = new LinkedList<MockBomb>();
    	for (Entry<Point, Bomb> entry : bombLocations.entrySet())
    		list.add(new MockBomb(entry.getKey(), entry.getValue()));
    	return list;
    }
    
    public MapItems getMapItem(Point point) {
    	return this.map[point.x][point.y];
    }
    
    public MapState clone() {
    	return new MapState((MapItems[][])map.clone(), new HashMap<Point, Bomb>(bombLocations), new HashMap<Point, PowerUps>(powerUpLocations), (Bomber[])players.clone(), (List<Point>)explosionLocations);
    }
}