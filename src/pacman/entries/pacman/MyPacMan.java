package pacman.entries.pacman;

import java.util.ArrayList;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getAction() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.pacman.mypackage).
 */
public class MyPacMan extends Controller<MOVE>
{
	private MOVE myMove=MOVE.NEUTRAL;
	private static final int MIN_DISTANCE = 15;
	private static final int EDIBILITY_TIME = 15;
	
	float runAwayPriority = 1.0f;
	float eatPillPriority = 1.0f;		
	float eatPowerPillPriority = 1.0f;
	float eatGhostPriority = 1.0f;

	public MOVE getMove(Game game, long timeDue) 
	{
		
		System.out.print("runAwayPri:"+runAwayPriority+"\n");
		System.out.print("eatPillPri:"+eatPillPriority +"\n");
		System.out.print("eatPowerPi:"+eatPowerPillPriority +"\n");
		System.out.print("eatGhostPr:"+eatGhostPriority+"\n");
		
		// Current PacMan Position
		int current=game.getPacmanCurrentNodeIndex();

		// Current Possible Nodes 
		MOVE[] possibleMoves = null;

		// GhostTooClose
		boolean isAnActiveGhostTooClose = false;
		for(GHOST ghost : GHOST.values()) {
			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0) {
				int distance = game.getShortestPathDistance(current, game.getGhostCurrentNodeIndex(ghost));
				if (distance < MIN_DISTANCE) {
					isAnActiveGhostTooClose = true;
					break;
				}
			}			
		}

		// PossibleMoves depending if isAGhostTooClose
		if(isAnActiveGhostTooClose) {	
			eatPillPriority = 1.0f;
			runAwayPriority = 1.0f;
			eatPowerPillPriority = 1.0f;			
			possibleMoves = game.getPossibleMoves(current);
		} else {
			eatPillPriority = 1.0f;
			runAwayPriority = 0.0f;
			eatGhostPriority = 50.0f;
			// When there are no ghosts close, we don't want to turn back
			possibleMoves = game.getPossibleMoves(current,game.getPacmanLastMoveMade());
		}
		
		boolean edibleGhostExist = false;
		for(GHOST ghost : GHOST.values()){
			if(game.isGhostEdible(ghost)){
				edibleGhostExist = true;
				break;
			}
		}
		
		if(edibleGhostExist){
			eatGhostPriority = 50.0f;
			eatPowerPillPriority = 0.0f;
		}


		MOVE nextMove = null;
		float nextMoveValue = 0;
		//int furthestClosestGhostDistance = 0;		

		for(MOVE possibleMove : possibleMoves) {
			int node = game.getNeighbour(current, possibleMove);
			float currentValue = evaluateNode(node, game);
			if(currentValue > nextMoveValue){
				nextMoveValue = currentValue;
				nextMove = possibleMove;
			}
		}
		return nextMove;

		/*

			int possibleNode = game.getNeighbour(current, possibleMove);
			int possibleNodeValue = 0;

			int closestGhostDistance = Integer.MAX_VALUE;

			

			if(closestGhostDistance > furthestClosestGhostDistance){
				furthestClosestGhostDistance = closestGhostDistance;
				nextMove = possibleMove;
			}

		 */

	}

	//		for(GHOST ghost : GHOST.values())
	//			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
	//				
	////				if( ghost == GHOST.BLINKY) {
	////					currentGhostsPositions[0] = game.get
	////				}
	////				System.out.println(ghost.name()+": "+game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost)));
	////				if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<MIN_DISTANCE)
	////					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);
	//
	//		return myMove;

	private float evaluateNode(int node , Game game){
		float closestGhostScore = 0;
		float closestPillScore = 0;
		float closestPowerPillScore = 0;
		float closestEdibleGhostScore = 0;
		
		closestGhostScore = closestGhostDistance(node, game);
		closestPillScore = 10/(0.00001f+closestPillDistance(node, game));
		closestPowerPillScore = 50/(0.00001f+closestPowerPillDistance(node, game));
		closestEdibleGhostScore = 50/(0.00001f+closestEdibleGhostDistance(node, game));
		
		
		float result = runAwayPriority*closestGhostScore;
		result += eatPillPriority*closestPillScore;
		result += eatPowerPillPriority*closestPowerPillScore;
		result += eatGhostPriority*closestGhostScore;		
		
		return result;
	}
	
	private int closestGhostDistance(int node, Game game){
		
		int closestGhostDistance = Integer.MAX_VALUE;		
		
		for(GHOST ghost : GHOST.values()){
			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0) {
				//int distance = game.getShortestPathDistance(node, game.getGhostCurrentNodeIndex(ghost));
				int distance = game.getManhattanDistance(node, game.getGhostCurrentNodeIndex(ghost));				
				if(distance < closestGhostDistance){
					closestGhostDistance = distance;
				}					
			}
		}
		return closestGhostDistance;
		
	} 
	
	private int closestPillDistance(int node, Game game){
		int[] activePills = game.getActivePillsIndices();		
		int closestActivePillsNode = game.getClosestNodeIndexFromNodeIndex(node, activePills, DM.MANHATTAN);
		return game.getShortestPathDistance(node, closestActivePillsNode);		
	}
	
	private int closestEdibleGhostDistance(int node, Game game){
		
		int distance = Integer.MAX_VALUE;
		
		for(GHOST ghost : GHOST.values()){
			if(game.isGhostEdible(ghost) && game.getGhostEdibleTime(ghost)>EDIBILITY_TIME){
				int ghostNode = game.getGhostCurrentNodeIndex(ghost);
				int ghostDistance = game.getManhattanDistance(node, ghostNode);
				
				if(ghostDistance < distance){
					distance = ghostDistance;
				}
			}			
		}
		
		return distance;
	}

	private int closestPowerPillDistance(int node, Game game){
		int[] activePowerPills = game.getActivePowerPillsIndices();		
		int closestActivePowerPillsNode = game.getClosestNodeIndexFromNodeIndex(node, activePowerPills, DM.PATH);
		if(activePowerPills.length > 0){
			return game.getShortestPathDistance(node, closestActivePowerPillsNode);			
		}
		return Integer.MAX_VALUE;
					
	}
	

}

