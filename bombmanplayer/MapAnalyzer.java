import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import com.orbischallenge.bombman.api.game.MapItems;


public class MapAnalyzer {
	
	private MapState state;
	
	public MapAnalyzer(MapState state) {
		this.state = state;
	}
	
	public boolean isSafeFromExplosionsAtPosition(Point position, MapItems[][] map) {
		return timeUntilExplosionAtPosition(position, map) > 10;
	}
    
    public int timeUntilExplosionAtPosition(Point position, MapItems[][] map) {
    	return timeUntilExplosionAtPosition(position, map, new LinkedList<MockBomb>());
    }
    
    public int timeUntilExplosionAtPosition(Point position, MapItems[][] map, List<MockBomb> theoreticalBombs) {
    	if (this.state.explosionLocations.contains(position)) {
    		return 0;
    	}
    	
    	// Append real bombs to the theoretical bombs.
    	List<MockBomb> bombs = new LinkedList<MockBomb>();
    	bombs.addAll(theoreticalBombs);
    	for (Entry<Point, Bomb> pair : this.state.bombLocations.entrySet()) {
    		bombs.add(new MockBomb(pair.getKey(), pair.getValue()));    		
    	}

    	// Check any of the bombs will hit me.
    	int smallestExplosionTime = Integer.MAX_VALUE;
    	for (MockBomb bomb : bombs) {    		
    		if (bomb.isPositionWithinRange(position, map)) {
    			if (bomb.timeLeft < smallestExplosionTime)
    				smallestExplosionTime = bomb.timeLeft;
    		}
    	}

    	return smallestExplosionTime;
    }
    
    public int numberOfBlocksBombWillDestroy(Point position, int bombRange) {
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
    			if (this.state.getMapItem(leftPosition) == MapItems.BLOCK) {
    				hitsLeft = true;
    				numDestroyed++;
    			}
    		}
    		
    		if (!hitsRight) {
    			if (this.state.getMapItem(rightPosition) == MapItems.BLOCK) {
    				hitsRight = true;
    				numDestroyed++;
    			}
    		}
    		
    		if (!hitsUp) {
    			if (this.state.getMapItem(upPosition) == MapItems.BLOCK) {
    				hitsUp = true;
    				numDestroyed++;
    			}
    		}
    		
    		if (!hitsDown) {
    			if (this.state.getMapItem(downPosition) == MapItems.BLOCK) {
    				hitsDown = true;
    				numDestroyed++;
    			}
    		}
    	}
    
    	System.out.println("Bomb will destroy this many blocks: " + numDestroyed);
    	return numDestroyed;
    }
}
