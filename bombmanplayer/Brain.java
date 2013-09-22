import java.awt.Point;
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
		GetPowerUp
	}
	
	private Goal goal = Goal.None;
	public List<PathNode> path = null; // TODO: Make not public
	private SearchSet searchSet = null;
	
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
		if (this.searchSet == null)
			this.searchSet = new SearchSet(state.getMainPlayer().position, state.map, 7);
		return this.searchSet;
	}
	
	public void updateGoals(MapState state) {
		this.searchSet = null;
		
		clearPathIfInvalid(state);
		checkForSafety(state);
		
		// Choose next path.
		if (path == null) {
			findGoal(state);
		}
	}

	private void clearPathIfInvalid(MapState state) {
		if (path != null) {
			Point playerPos = state.getMainPlayer().position;
			if (path.size() == 0
					|| playerPos == getPathDestination()
					|| !path.get(0).position.equals(playerPos)) {
				path = null;
				goal = Goal.None;
			}
		}
	}

	private void checkForSafety(MapState state) {
		// Check for safety.
		MapAnalyzer analyzer = new MapAnalyzer(state);
		if (analyzer.isSafeFromExplosionsAtPosition(state.getMainPlayer().position, state.map)) {
			if (goal == Goal.EscapeDanger) {
				goal = Goal.None;
				path = null;
			}
		} else {
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
	
	public boolean hasPath() {
		return path != null;
	}
	
	public void findGoal(MapState state) {
		SearchSet set = new SearchSet(state.getMainPlayer().position, state.map, 8);
		
		LinkedList<PathNode> powerups = new LinkedList<PathNode>();
		for (PathNode tile : set.nodes)
			if (state.getMapItem(tile.position) == MapItems.POWERUP)
				powerups.add(tile);
		
		if (powerups.size() > 0) {
			Collections.sort(powerups, new PathNode.DistanceCompare());
			path = set.pathToPoint(powerups.get(0).position);
			goal = Goal.GetPowerUp;
		}
	}
}
