import java.awt.Point;

import com.orbischallenge.bombman.api.game.MapItems;

/**
 * Mocks a bomb that hasn't been placed yet.
 * Also gives convenient helpers for existing bombs.
 */
public class MockBomb {
	
	static final int DETONATION_TIME = 15; 
	
	public Point position;
	public int range;
	public int timeLeft;
	
	private boolean isExploded;
	
	public MockBomb(Bomber bomber) {
		this(bomber, bomber.position);
	}
	
	public MockBomb(Bomber bomber, Point position) {
		this.position = bomber.position;
		this.range = bomber.bombRange;
		this.timeLeft = DETONATION_TIME;
		this.isExploded = false;
	}
	
	public MockBomb(Point position, Bomb bomb) {
		this.position = position;
		this.range = bomb.getRange();
		this.timeLeft = bomb.getTimeleft();
		this.isExploded = false;
	}
	
	public boolean isPositionWithinRange(Point position, MapItems[][] map) {
		Point positionDelta = new Point(Math.abs(position.x
			- this.position.x), Math.abs(position.y - this.position.y));
	
		if (positionDelta.x > 0 && positionDelta.y > 0) {
			return false;
		} else if (positionDelta.x > 0) {
			if (this.range >= positionDelta.x) {
				return true;
			}
		} else if (positionDelta.y > 0) {
			if (this.range >= positionDelta.y) {
				return true;
			}
		} else {
			return true;
		}
		
		return false;
	}
	
	private boolean isWallBetweenX(MapItems[][] map, int x1, int x2, int y) {
		int min, max;
		if (x1 < x2) {
			min = x1;
			max = x2;
		} else {
			min = x2;
			max = x1;
		}
		
		for (int i = min + 1; i < max; i++) {
			MapItems item = map[i][y]; 
			if (item == MapItems.BLOCK || item == MapItems.WALL)
				return true;
		}
		return false;
	}
	
	private boolean isWallBetweenY(MapItems[][] map, int y1, int y2, int x) {
		int min, max;
		if (y1 < y2) {
			min = y1;
			max = y2;
		} else {
			min = y2;
			max = y1;
		}
		
		for (int i = min + 1; i < max; i++) {
			MapItems item = map[x][i]; 
			if (item == MapItems.BLOCK || item == MapItems.WALL)
				return true;
		}
		return false;
	}
	
	public void tick() {
		if (this.timeLeft == 0) {
			this.markAsExploded();
		} else {
			this.timeLeft--;
		}
	}

	private void markAsExploded() {
		this.isExploded = true;
	}
	
	public boolean checkIfExploded() {
		return this.isExploded;
	}
	
	/*  TODO: Move to a class that handles heurisitcs and balance.
	 * 
	 *  public int getPotentialToHitBomber(Bomber bomber) {
		if (PointHelper.manhattanDistance(this.position, bomber.position) > this.range) {
			return 0;
		}
		
		int potential = 0;
		return potential;
	}*/
}
