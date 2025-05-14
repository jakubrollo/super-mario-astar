package agents.robinBaumgarten;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;
import engine.helper.MarioActions;
import mff.agents.astarHelper.MarioAction;
import mff.agents.benchmark.IAgentBenchmark;
import mff.agents.benchmark.IAgentBenchmarkBacktrack;

/**
 * @author RobinBaumgarten
 */
public class Agent implements MarioAgent, IAgentBenchmark, IAgentBenchmarkBacktrack {
    private boolean[] action;
    private AStarTree tree;
    private int totalSearchCalls = 0;

    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
        this.action = new boolean[MarioActions.numberOfActions()];
        this.tree = new AStarTree();
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
        action = this.tree.optimise(model, timer);
        totalSearchCalls++;
      /*
        System.out.printf(
                "Call #%d - Actions (left, right, down, jump, speed): (%b, %b, %b, %b, %b)%n",
                totalSearchCalls,
                action[0], action[1], action[2], action[3], action[4]
        );
        */
        return action;
    }

    @Override
    public int getSearchCalls() {
        return totalSearchCalls;
    }

    @Override
    public int getNodesEvaluated() {
        return tree.nodesEvaluated;
    }

    @Override
    public String getAgentName() {
        return "RobinBaumgartenAgent";
    }

    @Override
    public int getMostBacktrackedNodes() {
        return tree.mostBacktrackedNodes;
    }
}
