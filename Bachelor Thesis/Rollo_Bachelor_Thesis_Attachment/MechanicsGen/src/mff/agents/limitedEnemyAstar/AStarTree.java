package mff.agents.limitedEnemyAstar;

import mff.agents.astarHelper.CompareByCost;
import mff.agents.astarHelper.Helper;
import mff.agents.astarHelper.MarioAction;
import mff.agents.astarHelper.SearchNode;
import mff.agents.common.MarioTimerSlim;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;
import mff.forwardmodel.slim.core.MarioSpriteSlim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class AStarTree {
    public SearchNode furthestNode;
    public float furthestNodeDistance;

    float marioXStart;
    int searchSteps;

    private boolean winFound = false;
    private final float maxMarioSpeedX = 10.91f;
    static float exitTileX;
    public int nodesEvaluated = 0;
    public int mostBacktrackedNodes = 0;
    private int farthestReachedX;
    private int nodesBeforeNewFarthestX = 0;

    PriorityQueue<SearchNode> opened = new PriorityQueue<>(new CompareByCost());
    /**
     * INT STATE -> STATE COST
     */
    HashMap<Integer, Float> visitedStates = new HashMap<>();
    
    public AStarTree(MarioForwardModelSlim startState, int searchSteps) {
    	this.searchSteps = searchSteps;

    	marioXStart = startState.getMarioX();

    	furthestNode = getStartNode(startState);
    	furthestNode.cost = calculateCost(startState, furthestNode.nodeDepth);
    	furthestNodeDistance = furthestNode.state.getMarioX();

        farthestReachedX = (int) furthestNode.state.getMarioX();

    	opened.add(furthestNode);
    }
    
    private int getIntState(MarioForwardModelSlim model) {
    	return getIntState((int) model.getMarioX(), (int) model.getMarioY());
    }
    
    private int getIntState(int x, int y) {
    	return (x << 16) | y;
    }
    
    private SearchNode getStartNode(MarioForwardModelSlim state) {
    	// TODO: pooling
    	return new SearchNode(state);
    }
    
    private SearchNode getNewNode(MarioForwardModelSlim state, SearchNode parent, float cost, MarioAction action) {
    	// TODO: pooling
    	return new SearchNode(state, parent, cost, action);
    }
    
    private float calculateCost(MarioForwardModelSlim nextState, int nodeDepth) {
        float timeToFinish = (exitTileX - nextState.getMarioX()) / maxMarioSpeedX;
        timeToFinish *= 1.2;
        return (4*nodeDepth) + timeToFinish;
	}
    
    public ArrayList<boolean[]> search(MarioTimerSlim timer) {
        while (opened.size() > 0 && timer.getRemainingTime() > 0) {
            SearchNode current = opened.remove();
            nodesEvaluated++;

            if ((int) current.state.getMarioX() > farthestReachedX) {
                mostBacktrackedNodes = Math.max(nodesBeforeNewFarthestX, mostBacktrackedNodes);
                farthestReachedX = (int) current.state.getMarioX();
                nodesBeforeNewFarthestX = 0;
            } else {
                nodesBeforeNewFarthestX++;
            }

            if (current.state.getMarioX() > furthestNodeDistance) {
                furthestNode = current;
                furthestNodeDistance = current.state.getMarioX();
            }

            if (current.state.getGameStatusCode() == 1) {
                furthestNode = current;
                //System.out.print("WIN FOUND ");
                winFound = true;
                break;
            }

            ArrayList<MarioAction> actions = Helper.getPossibleActions(current.state);
            for (MarioAction action : actions) {
                MarioForwardModelSlim newState = current.state.clone();

                HashMap<MarioSpriteSlim, float[]> enemyStates = new HashMap<>();
                ArrayList<MarioSpriteSlim> enemies = newState.getCurWorld().getEnemies();
                for (MarioSpriteSlim enemy : enemies) {
                    enemyStates.put(enemy, new float[] { enemy.x, enemy.y, enemy.alive ? 1f : 0f });
                    // move enemy far away
                    enemy.x = 1000.0f;
                    enemy.y = 1000.0f;
                    enemy.alive = true;
                }

                // sim without enemies
                for (int i = 0; i < searchSteps; i++) {
                    newState.advance(action.value);
                }

                if (!newState.getWorld().mario.alive)
                    continue;

                // restore enemies
                for (MarioSpriteSlim enemy : enemyStates.keySet()) {
                    float[] state = enemyStates.get(enemy);
                    enemy.x = state[0];
                    enemy.y = state[1];
                    enemy.alive = state[2] == 1f;
                }

                float newStateCost = calculateCost(newState, current.nodeDepth + 1);

                int newStateCode = getIntState(newState);
                float newStateOldScore = visitedStates.getOrDefault(newStateCode, -1.0f);
                if (newStateOldScore >= 0 && newStateCost >= newStateOldScore)
                    continue;

                visitedStates.put(newStateCode, newStateCost);
                opened.add(getNewNode(newState, current, newStateCost, action));
            }
        }

        ArrayList<boolean[]> actionsList = new ArrayList<>();

        SearchNode curr = furthestNode;

        while (curr.parent != null) {
            for (int i = 0; i < searchSteps; i++) {
                actionsList.add(curr.marioAction.value);
            }
            curr = curr.parent;
        }

//        System.out.println("ITERATIONS: " + iterations + " | Best X: " + furthestNode.state.getMarioX()
//            + " | Number of actions: " + actionsList.size());

        return actionsList;
    }

    public boolean isWinFound() {
        return winFound;
    }
}
