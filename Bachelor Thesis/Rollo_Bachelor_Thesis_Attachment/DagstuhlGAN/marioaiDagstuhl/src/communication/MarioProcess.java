package communication;

import ch.idsia.ai.agents.AgentsPool;
import ch.idsia.ai.agents.human.HumanKeyboardAgent;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.simulation.BasicSimulator;
import ch.idsia.mario.simulation.Simulation;
import ch.idsia.mario.simulation.SimulationOptions;
import engine.core.MarioEvent;
import engine.core.MarioGame;
import engine.core.MarioResult;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationInfo;
import ch.idsia.tools.EvaluationOptions;
import ch.idsia.tools.ToolsConfigurator;
import competition.icegic.robin.AStarAgent;
import engine.core.MarioWorld;
import mff.LevelLoader;
import mff.agents.astarGrid.AStarTree;
import mff.agents.benchmark.AgentBenchmarkGame;
import mff.agents.benchmark.AgentStats;
import mff.agents.benchmark.IAgentBenchmarkBacktrack;
import mff.agents.benchmark.OriginalAgentBenchmarkGame;
import mff.agents.common.AStarGridHelper;
import mff.agents.common.AgentMarioGame;
import mff.agents.common.IMarioAgentMFF;
import mff.agents.gridSearch.GridPathVisualizer;
import mff.agents.gridSearch.GridSearch;
import mff.agents.gridSearch.GridSearchNode;

import java.util.ArrayList;
import java.util.List;

import static mff.agents.benchmark.LevelAnalyzer.detectJumps;
import static mff.agents.benchmark.LevelAnalyzer.loadLevelFromString;

public class MarioProcess extends Comm {
    private EvaluationOptions evaluationOptions;
    private Simulation simulator;
    private Level _level;

    public MarioProcess() {
        super();
        this.threadName = "MarioProcess";
    }

    /**
     * Default mario launcher does not have any command line parameters
     */
    public void launchMario() {
        String[] options = new String[] {""};
        launchMario(options, false);
    }

    /**
     * This version of launching Mario allows for several parameters
     * @param options General command line options (currently not really used)
     * @param humanPlayer Whether a human is playing rather than a bot
     */
    public void launchMario(String[] options, boolean humanPlayer) {
        /**
         this.evaluationOptions = new CmdLineOptions(options);  // if none options mentioned, all defaults are used.
         // set agents
         createAgentsPool(humanPlayer);
         // Short time for evolution, but more for human
         if(!humanPlayer) evaluationOptions.setTimeLimit(20);
         // TODO: Make these configurable from commandline?
         evaluationOptions.setMaxFPS(!humanPlayer); // Slow for human players, fast otherwise
         evaluationOptions.setVisualization(true); // Set true to watch evaluations
         // Create Mario Component
         ToolsConfigurator.CreateMarioComponentFrame(evaluationOptions);
         evaluationOptions.setAgent(AgentsPool.getCurrentAgent());
         System.out.println(evaluationOptions.getAgent().getClass().getName());
         // set simulator
         this.simulator = new BasicSimulator(evaluationOptions.getSimulationOptionsCopy());
         */

    }

    public void setLevel(Level level) {
        evaluationOptions.setLevel(level);
        this.simulator.setSimulationOptions(evaluationOptions);
    }

    /**
     * Simulate a given level
     * Counts enemies and returns as fitness
     * @return
     */
    public double levelEnemiesFitness(String level) {
        /*
        var game = new OriginalAgentBenchmarkGame();
        var agent = new agents.robinBaumgarten.Agent();
        */
        var game = new AgentBenchmarkGame();
        var agent = new mff.agents.astarGrid.Agent();
        AStarTree.NODE_DEPTH_WEIGHT = 1f;
        AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
        AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 1f;
        AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 5f;
        AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 5f;
        AStarGridHelper.giveLevelTilesWithPathGivenLevel(agent, level);

        int numberOfEnemies = level.length() - level.replace("g", "").length();
        var result = game.runGame(agent, level, 8, 0, false);

        if (!result.win){
            return result.percentageTravelled;
        }

        return numberOfEnemies + 1; // +1 is there because we got to finish (completion percentage)
    }

    /**
     * Simulate a given level using only grid search (no dynamic obstacles)
     * Calculates completion percentage and number of jumps just from the grid text output
     * Returns static jump fitness
     *
     * @return
     */
    public double levelGridJumpFitness(String level) {
        MarioEvent[] killEvents = new MarioEvent[0];
        MarioWorld world = new MarioWorld(killEvents);
        world.initializeLevel(level, 1000000);
        int[][] levelTiles = world.level.getLevelTiles();
        int marioTileX = world.level.marioTileX;
        int marioTileY = world.level.marioTileY;

        GridSearch gridSearch = new GridSearch(levelTiles, marioTileX, marioTileY, 0);
        ArrayList<GridSearchNode> resultPath = gridSearch.findGridPath();
        //GridPathVisualizer.visualizePath(level, levelTiles, resultPath);

        if (!gridSearch.success) { // Didn't find a path to finish
            return gridSearch.getTotalCompletionPercentage();
        }
        var levelWithPath = GridPathVisualizer.getLevelPathString(level, levelTiles, resultPath);
        List<String> grid = loadLevelFromString(levelWithPath);
        int jumpCount = detectJumps(grid);
        return jumpCount+1; // +1 is there because the grid got to finish
    }

    /**
     * Simulate a given level and return with fitness of backtracked nodes
     * @return
     */
    public double levelBacktrackFitness(String level) {
        var game = new OriginalAgentBenchmarkGame();
        var agent = new agents.robinBaumgarten.Agent();

        //var agent = new mff.agents.astar.Agent();
        // var game = new AgentBenchmarkGame();
        // AStarTree.NODE_DEPTH_WEIGHT = 1f;
        // AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
        // AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 2f;
        // AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 5f;
        // AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 7f;
        //AStarGridHelper.giveLevelTilesWithPathGivenLevel(agent, level);

        var result = game.runGame(agent, level, 8, 0, false);
        if (!result.win){
            return result.percentageTravelled;
        }
        var numOfBacktrackedNodes = ((IAgentBenchmarkBacktrack) agent).getMostBacktrackedNodes();
        return numOfBacktrackedNodes + 1;
    }

    /**
     * Simulate a given level and return with runtime fitness
     * @return
     */
    public double levelRuntimeFitness(String level) {
        //var game = new OriginalAgentBenchmarkGame();
        //var agent = new agents.robinBaumgarten.Agent();

        var agent = new mff.agents.astar.Agent();
        var game = new AgentBenchmarkGame();
        AStarTree.NODE_DEPTH_WEIGHT = 1f;
        AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
        AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 1f;
        AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 5f;
        AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 5f;
        AStarGridHelper.giveLevelTilesWithPathGivenLevel(agent, level);

        var result = game.runGame(agent, level, 8, 0, false);
        if (!result.win){
            return result.percentageTravelled;
        }
        var agentRunTime = result.runTime; //totalGameTicks for Baumgarten
        return agentRunTime + 1;
    }


    /**
     * Simulate a given level and return jump fitness
     * @return
     */
    public double levelJumpFitness(String level) {

        /*
        var game = new MarioGame();
        var result = game.runGame(new agents.robinBaumgarten.Agent(), level, 13, 0, false);
        var percentageTravelled = result.getCompletionPercentage();
        if (percentageTravelled != 1.0){
            return percentageTravelled;
        }
        return  result.getNumJumps() + 1;
*/
        var game = new AgentBenchmarkGame();
        IMarioAgentMFF astarGridAgent = new mff.agents.astarGrid.Agent();

        AStarTree.NODE_DEPTH_WEIGHT = 1f;
        AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
        AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 1f;
        AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 5f;
        AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 5f;
        AStarGridHelper.giveLevelTilesWithPathGivenLevel(astarGridAgent, level);

        //System.out.println(astarGridAgent.getAgentName());
        var result = game.runGame(astarGridAgent, level, 8, 0, false);

        if (!result.win){
            return result.percentageTravelled;
        }

        return result.numberOfJumps + 1;


    }

    /**
     * Simulate a given level and return only completion percentage
     * @return
     */
    public double levelCompletionFitness(String level) {
        //var game = new MarioGame();
        //var result = game.runGame(new agents.robinBaumgarten.Agent(), level, 13, 0, false);

        var game = new AgentBenchmarkGame();
        IMarioAgentMFF astarGridAgent = new mff.agents.astarGrid.Agent();

        AStarTree.NODE_DEPTH_WEIGHT = 1f;
        AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
        AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 2f;
        AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 5f;
        AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 7f;
        AStarGridHelper.giveLevelTilesWithPathGivenLevel(astarGridAgent, level);

        //System.out.println(astarGridAgent.getAgentName());
        var result = game.runGame(astarGridAgent, level, 8, 0, false);
        if (!result.win){
            return result.percentageTravelled;
        }

        return 1;
    }

    public EvaluationInfo simulateOneLevel() {
        evaluationOptions.setLevelFile("sample_1.json");
        EvaluationInfo info = this.simulator.simulateOneLevel();
        return info;
    }

    public void simulateOneLevel(String level){
        var game = new AgentBenchmarkGame();
        IMarioAgentMFF astarGridAgent = new mff.agents.astarGrid.Agent();

        AStarTree.NODE_DEPTH_WEIGHT = 1f;
        AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
        AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 2f;
        AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 5f;
        AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 7f;
        AStarGridHelper.giveLevelTilesWithPathGivenLevel(astarGridAgent, level);

        //System.out.println(astarGridAgent.getAgentName());
        var result = game.runGame(astarGridAgent, level, 8, 0, true);
        System.out.println(result.win);
    }

    public void simulateOneLevelHuman(String level){
        MarioGame game = new MarioGame();
        game.playGame(level, 10000);
    }

    @Override
    public void start() {
        this.launchMario();
    }

    @Override
    public void initBuffers() {

    }
}
