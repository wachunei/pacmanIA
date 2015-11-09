package pacman.entries.ghosts;

import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import static pacman.game.Constants.*;

/*
 * Versión modificada del ejemplo incluido: The Class Legacy2TheReckoning.
 */
public class MyGhosts extends Controller<EnumMap<GHOST,MOVE>>
{
	public static final int CROWDED_DISTANCE=30;
	public static final int PACMAN_DISTANCE=10;
    public static final int PILL_PROXIMITY=15;
    private final EnumMap<GHOST,MOVE> myMoves=new EnumMap<GHOST,MOVE>(GHOST.class);
    private final EnumMap<GHOST,Integer> cornerAllocation=new EnumMap<GHOST,Integer>(GHOST.class);
    
    /**
     * Instantiates a new legacy2 the reckoning.
     */
    public MyGhosts()
    {
    	cornerAllocation.put(GHOST.BLINKY,0);
    	cornerAllocation.put(GHOST.INKY,1);
    	cornerAllocation.put(GHOST.PINKY,2);
    	cornerAllocation.put(GHOST.SUE,3);
    }
    
    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    public EnumMap<GHOST,MOVE> getMove(Game game,long timeDue)
    {
		int pacmanIndex=game.getPacmanCurrentNodeIndex();
    	
        for(GHOST ghost : GHOST.values())      
        {
        	
        		int currentIndex=game.getGhostCurrentNodeIndex(ghost);
        		
        		//if ghosts are all in close proximity and not near Ms Pac-Man, disperse
        		if(isCrowded(game) && !closeToMsPacMan(game,currentIndex))
        			myMoves.put(ghost,getRetreatActions(game,ghost));                          				//go towards the power pill locations
        		//if edible or Ms Pac-Man is close to power pill, move away from Ms Pac-Man
        		// Queremos movernos hacia el fantasma no edible más cercano o si no simplemente huir de PacMan
        		else if(game.getGhostEdibleTime(ghost)>0 || closeToPower(game)){
        			if(nonEdibleGhostsExist(game)){
        				// Moverme al nonedible más cercano (esperando que éste se coma a PacMan)
        				MOVE currentMove = moveTowardsClosestNonEdibleGhost(currentIndex, game, ghost);
        				// Si ese movimiento no me acerca a Pacman
        				if(currentMove != game.getNextMoveTowardsTarget(currentIndex, pacmanIndex,  game.getGhostLastMoveMade(ghost), DM.PATH)){
        					myMoves.put(ghost, currentMove);        					
        				}
        				else{
        					myMoves.put(ghost,game.getNextMoveAwayFromTarget(currentIndex,pacmanIndex, game.getGhostLastMoveMade(ghost), DM.PATH));        					
        				}
        				        				
        			}
        			else{
        				myMoves.put(ghost,game.getNextMoveAwayFromTarget(currentIndex,pacmanIndex,DM.PATH));        				
        			}
        			     			
        			
        		}
        			
        		//else go towards Ms Pac-Man
        		else{
        			if(game.isJunction(currentIndex) && otherGhostsNear(ghost, game)){
        				Random r = new Random();
        				double p = r.nextDouble();
        				if(p<0.3){
        					double d = r.nextDouble();
        					if(d< 0.25){
        						myMoves.put(ghost, MOVE.UP);       						
        					}
        					else if(d<0.50){
        						myMoves.put(ghost, MOVE.LEFT);
        					}
        					else if(d<0.75){
        						myMoves.put(ghost, MOVE.DOWN);
        					}
        					else{
        						myMoves.put(ghost, MOVE.RIGHT);
        					}
        					        					
        				}
        			}
        			
        			if(ghost == GHOST.BLINKY){
        				myMoves.put(ghost,game.getNextMoveTowardsTarget(currentIndex,pacmanIndex,DM.PATH));        				
        			}
        			else if(ghost == GHOST.INKY){
        				myMoves.put(ghost,game.getNextMoveTowardsTarget(currentIndex,pacmanIndex,DM.PATH));
        			}
        			else if(ghost == GHOST.PINKY){
        				myMoves.put(ghost,game.getNextMoveTowardsTarget(currentIndex,pacmanIndex,DM.EUCLID));
        			}
        			else if(ghost == GHOST.SUE){
        				myMoves.put(ghost,game.getNextMoveTowardsTarget(currentIndex,pacmanIndex,DM.MANHATTAN));
        			}
        			
        			        			
        		}        		
        			
        	
        }
        
        return myMoves;
    }

    private boolean otherGhostsNear(GHOST currentGhost, Game game) {
		for(GHOST ghost : GHOST.values()){
			int distance = game.getShortestPathDistance(game.getGhostCurrentNodeIndex(currentGhost), game.getGhostCurrentNodeIndex(ghost));
			if(distance<10 && ghost != currentGhost){
				return true;
			}
		}
		return false;
	}

	private MOVE moveTowardsClosestNonEdibleGhost(int currentIndex, Game game, GHOST currentGhost) {
		int closestDistance = Integer.MAX_VALUE;
		int closestGhostIndex = currentIndex;
    	
		for(GHOST ghost : GHOST.values()){
    		if(!game.isGhostEdible(ghost)){
    			int ghostIndex = game.getGhostCurrentNodeIndex(ghost);
    			int distance = game.getShortestPathDistance(currentIndex, ghostIndex);
    			if(distance < closestDistance){
    				closestDistance = distance;
    				closestGhostIndex = ghostIndex;
    			}
    		}			
		}
		
		return game.getNextMoveTowardsTarget(currentIndex, closestGhostIndex, DM.PATH);
	}

	private boolean nonEdibleGhostsExist(Game game) {
		for(GHOST ghost : GHOST.values()){
			if(!game.isGhostEdible(ghost)){
				return true;
			}
		}
		return false;
	}

	/**
     * Close to power.
     *
     * @param game the game
     * @return true, if successful
     */
    private boolean closeToPower(Game game)
    {
    	int pacmanIndex=game.getPacmanCurrentNodeIndex();
    	int[] powerPillIndices=game.getActivePowerPillsIndices();
    	
    	for(int i=0;i<powerPillIndices.length;i++)
    		if(game.getShortestPathDistance(powerPillIndices[i],pacmanIndex)<PILL_PROXIMITY)
    			return true;

        return false;
    }

    /**
     * Close to ms pac man.
     *
     * @param game the game
     * @param location the location
     * @return true, if successful
     */
    private boolean closeToMsPacMan(Game game,int location)
    {
    	if(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),location)<PACMAN_DISTANCE)
    		return true;

    	return false;
    }

    /**
     * Checks if is crowded.
     *
     * @param game the game
     * @return true, if is crowded
     */
    private boolean isCrowded(Game game)
    {
    	GHOST[] ghosts=GHOST.values();
        float distance=0;
        
        for (int i=0;i<ghosts.length-1;i++)
            for(int j=i+1;j<ghosts.length;j++)
                distance+=game.getShortestPathDistance(game.getGhostCurrentNodeIndex(ghosts[i]),game.getGhostCurrentNodeIndex(ghosts[j]));
        
        return (distance/6)<CROWDED_DISTANCE ? true : false;
    }

    /**
     * Gets the retreat actions.
     *
     * @param game the game
     * @param ghost the ghost
     * @return the retreat actions
     */
    private MOVE getRetreatActions(Game game,GHOST ghost)
    {
    	int currentIndex=game.getGhostCurrentNodeIndex(ghost);
    	int pacManIndex=game.getPacmanCurrentNodeIndex();
    	
        if(game.getGhostEdibleTime(ghost)==0 && game.getShortestPathDistance(currentIndex,pacManIndex)<PACMAN_DISTANCE)
            return game.getApproximateNextMoveTowardsTarget(currentIndex,pacManIndex,game.getGhostLastMoveMade(ghost),DM.PATH);
        else
            return game.getApproximateNextMoveTowardsTarget(currentIndex,game.getPowerPillIndices()[cornerAllocation.get(ghost)],game.getGhostLastMoveMade(ghost),DM.PATH);
    }
}