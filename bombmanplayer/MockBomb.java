import java.awt.Point;

/**
 * Mocks a bomb that hasn't been placed yet.
 * Also gives convenient helpers for existing bombs.
 */
public class MockBomb {
	
	public Point position;
	public int range;
	public int timeLeft;
	
	private boolean isExploded;
	
	public MockBomb(Bomber bomber) {
		this.position = bomber.position;
		this.range = bomber.bombCount;
		this.timeLeft = 15;
		this.isExploded = false;
	}
	
	public MockBomb(Point position, Bomb bomb) {
		this.position = position;
		this.range = bomb.getRange();
		this.timeLeft = bomb.getTimeleft();
		this.isExploded = false;
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
	
	public boolean isHittingPosition(Point bomberPosition) {
		Point positionDelta = new Point(Math.abs(bomberPosition.x
					- this.position.x), Math.abs(bomberPosition.y - this.position.y));
	
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
		} 
		
		return false;
	}
	
	public int getPotentialToHitBomber(Bomber bomber) {
		if (PointHelper.manhattenDistance(this.position, bomber.position) > this.range) {
			return 0;
		}
		
		int potential = 0;
		
		return potential;
		
	}
}
