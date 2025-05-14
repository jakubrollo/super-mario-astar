package mff.agents.common;

import engine.core.*;
import engine.helper.EventType;
import levelGenerators.GenerateLevel;
import mff.LevelLoader;
import mff.agents.astarGrid.AStarTree;
import mff.agents.benchmark.AgentBenchmarkGame;
import mff.agents.benchmark.IAgentBenchmarkBacktrack;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;

public class AgentMain {
    public static void main(String[] args) {
        //testLevel();

        // testLevelBaumgarten();
        testLevelAstar();
        // testLevelGrid();

        //testLevelBenchmark();

        //testAllOriginalLevels();
        //testAllOriginalLevelsGrid();
        //testAllKrysLevelsGrid();

        //testGeneratedLevels();
        //testAllAgents();

        //testAllTOADGENLevelsAstarGridBenchmark();
        //testAllTOADGENLevelsBaumgartenBenchmark();
    }

    private static void testLevel() {
        var game = new MarioGame();
        game.runGame(new agents.human.Agent(), LevelLoader.getLevel("./other_levels/krys/lvl-3.txt"),
                200,0,true);
    }

    private static void testLevelBaumgarten() {

        String level = LevelLoader.getLevel("./other_levels/krys/lvl-3.txt");
        //MarioGame game = new MarioGame(new MarioEvent[]{new MarioEvent(EventType.COLLECT, 15)}); // coin kill event
        MarioGame game = new MarioGame();

        System.out.println("-------");
        var agent = new agents.robinBaumgarten.Agent();
        GenerateLevel.printResults(game.runGame(agent, level, 20, 0, true));

        var mostBacktrackedNodes = ((IAgentBenchmarkBacktrack) agent).getMostBacktrackedNodes();
        System.out.println("BAUMGARTEN Most Backtracked Nodes: " + mostBacktrackedNodes);
    }

    private static void testLevelAstar() {
     //   var game = new AgentBenchmarkGame(new MarioEvent[]{new MarioEvent(EventType.COLLECT, 15)}); // coin kill event
     //   var game = new AgentBenchmarkGame();
        var game = AgentBenchmarkGame.withMustDoEvents(new MarioEvent[]{new MarioEvent(EventType.COLLECT, 15)});

        String levelPath = "./other_levels/krys/lvl-3.txt";
        IMarioAgentMFF astarGridAgent = new mff.agents.astar.Agent();
        var result = game.runGame(astarGridAgent, LevelLoader.getLevel(levelPath), 20, 0, true);

        System.out.println("MFF A* has won: " + result.win);
        System.out.println("Has triggered given event: " + result.hasDoneMustDoEvents);
    }

    private static void testLevelGrid() {
        var game = new AgentBenchmarkGame();
        String levelPath = "./other_levels/krys/lvl-3.txt";
        IMarioAgentMFF astarGridAgent = new mff.agents.astarGrid.Agent();
        AStarTree.NODE_DEPTH_WEIGHT = 1f;
        AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
        AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 1f;
        AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 5f;
        AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 5f;
        AStarGridHelper.giveLevelTilesWithPath(astarGridAgent, levelPath, 1);
        var result = game.runGame(astarGridAgent, LevelLoader.getLevel(levelPath),
                20, 0, true);

        //System.out.println(result.numOfJumps);
        System.out.println("MFF A* Grid has won" + result.win);
    }

    private static void testLevelBenchmark() {
        String level = LevelLoader.getLevel("./other_levels/krys/lvl-3.txt");
        AgentBenchmarkGame game = new AgentBenchmarkGame();
        IMarioAgentMFF astarGridAgent = new mff.agents.astarGrid.Agent();
        AStarTree.NODE_DEPTH_WEIGHT = 1f;
        AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
        AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 1f;
        AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 5f;
        AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 5f;
        AStarGridHelper.giveLevelTilesWithPath(astarGridAgent, "./other_levels/krys/lvl-3.txt");
        var agentStats = game.runGame(astarGridAgent, level, 30, 0, true);
        agentStats.level = "krys-3";
        var mostBacktrackedNodes = ((IAgentBenchmarkBacktrack)astarGridAgent).getMostBacktrackedNodes();

        System.out.println("level: " + agentStats.level);
        System.out.println("win: " + agentStats.win);
        System.out.println("percentageTravelled: " + agentStats.percentageTravelled);
        System.out.println("runTime: " + agentStats.runTime);
        System.out.println("totalGameTicks: " + agentStats.totalGameTicks);
        System.out.println("totalPlanningTime: " + agentStats.totalPlanningTime);
        System.out.println("searchCalls: " + agentStats.searchCalls);
        System.out.println("nodesEvaluated: " + agentStats.nodesEvaluated);
        System.out.println("mostBacktrackedNodes: " + mostBacktrackedNodes);
        System.out.println("jumps: "+ agentStats.numOfJumps);
    }

    private static void testAllOriginalLevels() {
        for (int i = 1; i < 16; i++) {
            AgentBenchmarkGame game = new AgentBenchmarkGame();
            var result =game.runGame(new mff.agents.astar.Agent(), LevelLoader.getLevel("./other_levels/original/lvl-" + i + ".txt"),
                    200, 0, false);
            System.out.println(result.win);
        }
    }

    private static void testAllOriginalLevelsGrid() {
        for (int i = 1; i < 16; i++) {
            AgentMarioGame game = new AgentMarioGame();
            String levelPath = "./other_levels/original/lvl-" + i + ".txt";
            IMarioAgentMFF astarGridAgent = new mff.agents.astarGrid.Agent();
            AStarTree.NODE_DEPTH_WEIGHT = 1f;
            AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
            AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 1f;
            AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 5f;
            AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 5f;
            AStarGridHelper.giveLevelTilesWithPath(astarGridAgent, levelPath);
            game.runGame(astarGridAgent, LevelLoader.getLevel(levelPath),
                    200, 0, true);
        }
    }

    private static void testAllKrysLevelsGrid() {
        for (int i = 1; i <= 100; i++) {
            AgentMarioGame game = new AgentMarioGame();
            String levelPath = "./other_levels/krys/lvl-" + i + ".txt";
            IMarioAgentMFF astarGridAgent = new mff.agents.astarGrid.Agent();
            AStarTree.NODE_DEPTH_WEIGHT = 1f;
            AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
            AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 1f;
            AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 5f;
            AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 5f;
            AStarGridHelper.giveLevelTilesWithPath(astarGridAgent, levelPath);
            game.runGame(astarGridAgent, LevelLoader.getLevel(levelPath),
                    200, 0, true);
        }
    }


    private static void testGeneratedLevels() {
        for (int i = 1; i <= 100; i++) {
            MarioLevelGenerator generator = new levelGenerators.krys.LevelGenerator(i);
            String level = generator.getGeneratedLevel(new MarioLevelModel(150, 16),
                    new MarioTimer(5 * 60 * 60 * 1000));
            AgentMarioGame game = new AgentMarioGame();
            game.runGame(new mff.agents.astar.Agent(), level, 30, 0, true);
        }
    }

    private static void testAllAgents() {
        ArrayList<IMarioAgentMFF> agents = new ArrayList<>() {{
            add(new mff.agents.astar.Agent());
            add(new mff.agents.astarDistanceMetric.Agent());
            add(new mff.agents.astarFast.Agent());
            add(new mff.agents.astarJump.Agent());
            add(new mff.agents.astarPlanning.Agent());
            add(new mff.agents.astarPlanningDynamic.Agent());
            add(new mff.agents.astarWindow.Agent());
            add(new mff.agents.robinBaumgartenSlim.Agent());
            add(new mff.agents.robinBaumgartenSlimImproved.Agent());
        }};

        for (var agent : agents) {
            AgentMarioGame game = new AgentMarioGame();
            System.out.println("Testing " + agent.getAgentName());
            game.runGame(agent, LevelLoader.getLevel("./other_levels/original_3-3_TOADGAN/lvl-9.txt"), 200, 0, true);
        }
    }

    // In some cases, the grid search fails to find a valid path to the goal—often due to high obstacles.
    // However, the agent can still reach the goal by creatively using nearby enemies (e.g., bouncing off them).
    // This causes the agent to deviate significantly from the planned path, and since the path is no longer valid,
    // it results in an error (which we counted as a loss).

    private static void testAllTOADGENLevelsAstarGridBenchmark() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("output_Orig3-3_TOADGAN_mffAstarGrid.txt", true))) {
            writer.println("original level: original 3-3");
            for (int i = 1; i <= 100; i++) {
                var levelPath = "./other_levels/original_3-3_TOADGAN/lvl-" + i + ".txt";
                String level = LevelLoader.getLevel(levelPath);
                AgentBenchmarkGame game = new AgentBenchmarkGame();
                IMarioAgentMFF astarGridAgent = new mff.agents.astarGrid.Agent();
                AStarTree.NODE_DEPTH_WEIGHT = 1f;
                AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
                AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 1f;
                AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 5f;
                AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 5f;
                AStarGridHelper.giveLevelTilesWithPath(astarGridAgent, levelPath, 1);
                var agentStats = game.runGame(astarGridAgent, level, 20, 0, false);
                agentStats.level = "original_3-3_TOADGAN-" + i + ".txt";
                var mostBacktrackedNodes = ((IAgentBenchmarkBacktrack) astarGridAgent).getMostBacktrackedNodes();

                System.out.println("level: " + agentStats.level);
                System.out.println("win: " + agentStats.win);

                writer.println("level: " + agentStats.level);
                writer.println("win: " + agentStats.win);
                writer.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logGameStatus(PrintWriter writer, String levelName, int i, MarioResult result) {
        writer.println("level: " + levelName + "_lvl-" + i);
        writer.println("****************************************************************");
        writer.println("Game Status: " + result.getGameStatus().toString() +
                " Percentage Completion: " + result.getCompletionPercentage());
        writer.println("Lives: " + result.getCurrentLives() + " Coins: " + result.getCurrentCoins() +
                " Remaining Time: " + (int) Math.ceil(result.getRemainingTime() / 1000f));
        writer.println("Mario State: " + result.getMarioMode() +
                " (Mushrooms: " + result.getNumCollectedMushrooms() + " Fire Flowers: " + result.getNumCollectedFireflower() + ")");
        writer.println("Total Kills: " + result.getKillsTotal() + " (Stomps: " + result.getKillsByStomp() +
                " Fireballs: " + result.getKillsByFire() + " Shells: " + result.getKillsByShell() +
                " Falls: " + result.getKillsByFall() + ")");
        writer.println("Bricks: " + result.getNumDestroyedBricks() + " Jumps: " + result.getNumJumps() +
                " Max X Jump: " + result.getMaxXJump() + " Max Air Time: " + result.getMaxJumpAirTime());
        writer.println("****************************************************************");
    }

    private static void testAllTOADGENLevelsBaumgartenBenchmark(){
        try (PrintWriter writer = new PrintWriter(new FileWriter("output_Orig3-3_TOADGAN_Baumgarten.txt", true))) {
            writer.println("original level: orig_3-3");
            for(int i = 1; i <= 100; i++){
                String level = LevelLoader.getLevel("./other_levels/original_3-3_TOADGAN/lvl-" + i + ".txt");
                MarioGame game = new MarioGame();
                System.out.println("-------");
                System.out.println("level: Orig_3-3"+ "_lvl-"+i);
                MarioResult result = game.runGame(new agents.robinBaumgarten.Agent(), level, 30, 0, false);
                GenerateLevel.printResults(result);
                logGameStatus(writer, "orig_3-3", i, result);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
