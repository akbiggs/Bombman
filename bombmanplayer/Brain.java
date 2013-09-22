import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.orbischallenge.bombman.api.game.MapItems;

public class Brain {
	
	public enum Goal {
		None,
		EscapeDanger,
		GetPowerUp,
		BreakBricks
	}
	
	private Goal goal = Goal.None;
	public List<PathNode> path = null; // TODO: Make not public
	private SearchSet searchSet = null;
	public boolean tryToForceBomb = false;
	private boolean jumpBridgeOnNextTurn = false;
	
	public Brain() {

	}
	
	public Goal getGoal() {
		return goal;
	}
	
	public Point getPathDestination() {
		if (path == null || path.size() == 0)
			return null;
		return path.get(path.size()-1).position;
	}
	
	public SearchSet getSearchSetLazily(MapState state) {
		if (this.searchSet == null) {
			this.searchSet = new SearchSet(state.getMainPlayer().position, state.map, 8);
		}
		return this.searchSet;
	}
	
	public void updateGoals(MapState state) {
		// Reset some running variables.
		this.searchSet = null;
		tryToForceBomb = false;
		
		clearPathIfInvalid(state);
		
		// Checks if the player is standing on a dangerous spot.
		// If so, we cancel the current goal and execute an escape plan.
		checkForDangerousSituations(state);
		
		// Choose next path.
		if (path == null) {
			findPowerUps(state);
		}
		
		if (path == null) {
			findBlocksToDestroy(state);
		}
	}

	private void clearPathIfInvalid(MapState state) {
		if (path != null) {
			Point playerPos = state.getMainPlayer().position;
			if (path.size() == 0 || playerPos == getPathDestination()) {
				runEndOfPathCommand(goal);
				path = null;
				goal = Goal.None;
			}
			else if (!path.get(0).position.equals(playerPos)) {
				// Path was lost for whatever reason.
				path = null;
				goal = Goal.None;
			}
		}
	}
	
	private void runEndOfPathCommand(Goal goal) {
		if (goal == Goal.BreakBricks) {
			tryToForceBomb = true;
			if (PlayerAI.DEBUGGING) System.out.println("Try to force explosion!!!");
		}
	}

	private void checkForDangerousSituations(MapState state) {
		// Handle a special case where we jump recklessly into dangerous territory!
		if (jumpBridgeOnNextTurn) {
			jumpBridgeOnNextTurn = false;
			return;
		}
		
		// Check for safety.
		MapAnalyzer analyzer = new MapAnalyzer(state);
		if (analyzer.isSafeFromExplosionsAtPosition(state.getMainPlayer().position, state.map)) {
			if (goal == Goal.EscapeDanger) {
				goal = Goal.None;
				path = null;
			}
		} else {
			if (PlayerAI.DEBUGGING) System.out.println("Escape from danger!!!");
			// Find less dangerous spots.
			PathNode closestFreeSpot = null;
			PathNode bestDangerousSpot = null;
			int bestDangerousSpotTime = 0;
			for (PathNode node : getSearchSetLazily(state).nodes) {
				int time = analyzer.timeUntilExplosionAtPosition(node.position, state.map);
				if (time == Integer.MAX_VALUE) {
					if (closestFreeSpot == null || node.distance < closestFreeSpot.distance)
						closestFreeSpot = node;
				} else if (bestDangerousSpot == null || time > bestDangerousSpotTime) {
					bestDangerousSpot = node;
					bestDangerousSpotTime = time;
				}
			}
			
			PathNode destination;
			if (closestFreeSpot != null) {
				destination = closestFreeSpot;
			} else {
				destination = bestDangerousSpot;
			}
			
			if (destination != null) {
				if (!destination.position.equals(getPathDestination()))
					this.path = destination.buildPathList();
				this.goal = Goal.EscapeDanger;
			}
		}
	}
	
	public void findPowerUps(MapState state) {
		if (PlayerAI.DEBUGGING) System.out.println("Finding powerups!!");
		SearchSet set = getSearchSetLazily(state);
		
		ArrayList<PathNode> powerups = new ArrayList<PathNode>();
		for (PathNode tile : set.nodes)
			if (state.getMapItem(tile.position) == MapItems.POWERUP)
				powerups.add(tile);
		
		if (powerups.size() > 0) {
			Collections.sort(powerups, new PathNode.DistanceCompare());
			path = set.pathToPoint(powerups.get(0).position);
			goal = Goal.GetPowerUp;
		}
	}
	
	public void findBlocksToDestroy(MapState state) {
    	if (PlayerAI.DEBUGGING) System.out.println("Breaking Bricks!!");
		SearchSet set = getSearchSetLazily(state);
		MapAnalyzer analyzer = new MapAnalyzer(state);
		
		ArrayList<PathNodeBundle> blockSpots = new ArrayList<PathNodeBundle>();
		for (PathNode tile : set.nodes) {
			int blocks = analyzer.numberOfBlocksBombWillDestroy(tile.position, state.getMainPlayer().bombRange, state.map);
			if (blocks > 0)
				blockSpots.add(new PathNodeBundle(tile, blocks));
		}
		
		if (blockSpots.size() > 0) {
			Collections.sort(blockSpots);
			int pickFrom = Math.min(blockSpots.size(), 5);
			int randomValue = (int)(Math.random() * pickFrom);
			path = set.pathToPoint(blockSpots.get(randomValue).node.position);
			goal = Goal.BreakBricks;
		}
	}
	
	public boolean askMovePlanToFollowPath(MovePlan plan) {
		Move.Direction direction = PathHelper.GetMoveDirectionForBeginningOfPath(path);
		if (plan.safeDirections.contains(direction)) {
			plan.safeDirections.clear();
			plan.safeDirections.add(direction);
			path.remove(0);
			return true;
		}
		return false;
	}
	
	public boolean tryJumpShark(MapState state) {
		if (PlayerAI.DEBUGGING) System.out.println("Trying to jump shark!!!");
		MapAnalyzer analyzer = new MapAnalyzer(state);
		
		// Handle special case where we take a risky jump into bomb territory.
		if (path.size() >= 3) {
			PathNode theBridge = path.get(1);
			PathNode acrossTheBridge = path.get(2);
			int bridgeTime = analyzer.timeUntilExplosionAtPosition(theBridge.position, state.map);
			int acrossTheBridgeTime = analyzer.timeUntilExplosionAtPosition(acrossTheBridge.position, state.map);
			if (bridgeTime > 9 && acrossTheBridgeTime > MockBomb.DETONATION_TIME) {
				jumpBridgeOnNextTurn = true;
				return true;
			}
		}
		return false;
	}
	
	public boolean hasPath() {
		return path != null;
	}
	
	private class PathNodeBundle implements Comparable<PathNodeBundle> {
		public PathNode node;
		public int value;
		
		public PathNodeBundle(PathNode node, int value) {
			this.node = node;
			this.value = value;
		}
		
		@Override
		public int compareTo(PathNodeBundle o) {
			return this.value != o.value ? o.value - this.value : this.node.distance - o.node.distance;
		}
	}
}
