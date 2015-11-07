package pacman.entries.pacman;

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
	private static final int EDIBILITY_TIME = 5;
	private static final int REWARD_DIRECCTION_BONUS = 10000;

	float runAwayPriority = 1.0f;
	float eatPillPriority = 1.0f;		
	float eatPowerPillPriority = 1.0f;
	float eatGhostPriority = 1.0f;

	boolean rewardUp = false;
	boolean rewardLeft = false;
	boolean rewardDown = false;
	boolean rewardRight = false;



	public MOVE getMove(Game game, long timeDue) 
	{

		// Current PacMan Position
		int current=game.getPacmanCurrentNodeIndex();
		
		for(GHOST ghost : GHOST.values())
			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0)
				if(game.getShortestPathDistance(current,game.getGhostCurrentNodeIndex(ghost))<7)
					return game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),game.getGhostCurrentNodeIndex(ghost),DM.PATH);

		// Resert variables		
		rewardUp = false;
		rewardLeft = false;
		rewardDown = false;
		rewardRight = false;


		// Current Possible Nodes 
		MOVE[] possibleMoves = null;

		// GhostTooClose
		boolean isAnActiveGhostTooClose = false;
		for(GHOST ghost : GHOST.values()) {
			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0) {
				int ghostNode = game.getGhostCurrentNodeIndex(ghost);
				int distance = game.getShortestPathDistance(current, ghostNode);
				//				int distance = game.getManhattanDistance(current, ghostNode)
				if (distance < MIN_DISTANCE) {
					isAnActiveGhostTooClose = true;
					
					int currentX =  game.getNodeXCood(current);
					int currentY = game.getNodeYCood(current);

					int ghostX = game.getNodeXCood(ghostNode);
					int ghostY = game.getNodeYCood(ghostNode);

					if(currentX == ghostX){
						System.out.println("IGUALES X - Valor X:"+currentX);
						System.out.println("IGUALES X - Valor Y current:"+currentY);
						System.out.println("IGUALES X - Valor Y ghost  :"+ghostY);
						if(currentY < ghostY){
							rewardUp = true;							
						}
						else{
							rewardDown = true;							
						}
					}
					else if(currentY == ghostY){						
						if(currentX < ghostX){
							rewardLeft = true;
						}
						else{
							rewardRight = true;
						}

					}
				}
				
				
			}			
		}
		
		if(rewardUp && rewardDown){
			rewardUp = false;
			rewardDown = false;
			rewardLeft = true;
			rewardRight = true;
		}
		
		if(rewardLeft && rewardRight){
			rewardUp = true;
			rewardDown = true;
			rewardLeft = false;
			rewardRight = false;
		}
		
		
		// EdibleGhostExists
		boolean edibleGhostExists = false;
		for(GHOST ghost : GHOST.values()){
			if(game.isGhostEdible(ghost)){
				edibleGhostExists = true;
				break;
			}
		}

		// PossibleMoves depending if isAGhostTooClose
		if(isAnActiveGhostTooClose) {	
			if(edibleGhostExists){
				eatPillPriority = 0.0f;
				runAwayPriority = 2.0f;
				eatPowerPillPriority = 0.0f;
				eatGhostPriority = 1.9f;
			} else {				
				runAwayPriority = 2.0f;				
				if(game.getNumberOfActivePowerPills() > 0){
					eatPowerPillPriority = 2.0f;
					eatPillPriority = 0.3f;
				}
				else{
					eatPowerPillPriority = 0.0f;
					eatPillPriority = 2.0f;

				}
				
				eatGhostPriority = 0.5f;
			}
			possibleMoves = game.getPossibleMoves(current);
		} else {
			if(edibleGhostExists){
				eatPillPriority = 1.0f;
				runAwayPriority = 0.0f;
				eatPowerPillPriority = 0.0f;
				eatGhostPriority = 10.0f;
				possibleMoves = game.getPossibleMoves(current);
			} else {
				eatPillPriority = 1.0f;
				runAwayPriority = 0.0f;
				eatPowerPillPriority = 0.0f;
				eatGhostPriority = 0.0f;
				possibleMoves = game.getPossibleMoves(current,game.getPacmanLastMoveMade());
			}
		}

		MOVE nextMove = null;
		float nextMoveValue = 0;

		for(MOVE possibleMove : possibleMoves) {
			int node = game.getNeighbour(current, possibleMove);
			float currentValue = evaluateNode(node, game, possibleMove);
			if(currentValue > nextMoveValue){
				nextMoveValue = currentValue;
				nextMove = possibleMove;
			}
		}
		//System.out.println("MOVE:"+nextMove+" VALUE:"+nextMoveValue);
		return nextMove;


	}

	private float evaluateNode(int node , Game game, MOVE move){
		float closestGhostScore = 0;
		float closestPillScore = 0;
		float closestPowerPillScore = 0;
		float closestEdibleGhostScore = 0;

		float directionBonus = 0;


		closestGhostScore = closestGhostDistance(node, game);
		closestPillScore = 10/(0.00001f+closestPillDistance(node, game));
		closestPowerPillScore = 50/(0.00001f+closestPowerPillDistance(node, game));
		closestEdibleGhostScore = 50/(0.00001f+closestEdibleGhostDistance(node, game));

		directionBonus = calculateDirectionBonus(move);



		float result = runAwayPriority*closestGhostScore;
		result += eatPillPriority*closestPillScore;
		result += eatPowerPillPriority*closestPowerPillScore;
		result += eatGhostPriority*closestEdibleGhostScore;		

		result += directionBonus;

		return result;
	}

	private float calculateDirectionBonus(MOVE move){
		switch (move) {
		case UP:
			if(rewardUp){
				return REWARD_DIRECCTION_BONUS;
			}
			break;
		case LEFT:
			if(rewardLeft){
				return REWARD_DIRECCTION_BONUS;
			}
			break;
		case DOWN:
			if(rewardDown){
				return REWARD_DIRECCTION_BONUS;
			}
			break;
		case RIGHT:
			if(rewardRight){
				return REWARD_DIRECCTION_BONUS;
			}
			break;
		default:
			return 0;
		}	
		return 0;
	}

	private int closestGhostDistance(int node, Game game){

		int closestGhostDistance = Integer.MAX_VALUE;		

		for(GHOST ghost : GHOST.values()){
			if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0) {
				int distance = game.getShortestPathDistance(node, game.getGhostCurrentNodeIndex(ghost));
				//				int distance = game.getManhattanDistance(node, game.getGhostCurrentNodeIndex(ghost));				
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
		//		return game.getShortestPathDistance(node, closestActivePillsNode);	
		return game.getManhattanDistance(node, closestActivePillsNode);
	}

	private int closestEdibleGhostDistance(int node, Game game){

		int distance = Integer.MAX_VALUE;

		for(GHOST ghost : GHOST.values()){
			if(game.isGhostEdible(ghost) && game.getGhostEdibleTime(ghost)>EDIBILITY_TIME){
				int ghostNode = game.getGhostCurrentNodeIndex(ghost);
				//				int ghostDistance = game.getManhattanDistance(node, ghostNode);
				int ghostDistance = game.getShortestPathDistance(node, ghostNode);

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
			//			return game.getShortestPathDistance(node, closestActivePowerPillsNode);
			return game.getManhattanDistance(node, closestActivePowerPillsNode);
		}
		return Integer.MAX_VALUE;

	}


}

