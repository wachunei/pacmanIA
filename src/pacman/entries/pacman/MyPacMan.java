package pacman.entries.pacman;

import java.util.ArrayList;

import pacman.controllers.Controller;
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

	public MOVE getMove(Game game, long timeDue) 
	{
		// TODO: game logic here to play the game as Ms Pac-Man
		
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
		
		// possibleMoves depending if isAGhostTooClose
		if(isAnActiveGhostTooClose) {
			possibleMoves = game.getPossibleMoves(current);
		} else {
			possibleMoves = game.getPossibleMoves(current,game.getPacmanLastMoveMade());
			MOVE nextMove = null;
			int nextMoveValue = 0;
			
			for(MOVE possibleMove : possibleMoves) {
				int possibleNode = game.getNeighbour(current, possibleMove);
				int possibleNodeValue = 0;

				for(GHOST ghost : GHOST.values()){
					if(game.getGhostEdibleTime(ghost)==0 && game.getGhostLairTime(ghost)==0) {
//						int distance = game.getShortestPathDistance(possibleNode, game.getGhostCurrentNodeIndex(ghost));
						int distance = game.getManhattanDistance(possibleNode, game.getGhostCurrentNodeIndex(ghost));
						possibleNodeValue +=  distance < MIN_DISTANCE ? distance/10000: distance;
					}
				}
				
				if(nextMoveValue < possibleNodeValue) {
					nextMoveValue = possibleNodeValue;
					nextMove = possibleMove;
				}
			}
			return nextMove;
		}
		
		return myMove;
		
		
		
		
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
	}
	
}