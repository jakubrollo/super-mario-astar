package mff.agents.common;

import engine.core.*;
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
        //testLevelBaumgarten();
        testLevelGrid();
        testLevelBenchmark();
        //testLevelWaypoints();

        //testAllOriginalLevels();
        //testAllOriginalLevelsGrid();
        //testAllKrysLevelsGrid();

        //testGeneratedLevels();
        //testAllAgents();

        //testAllTOADGENLevelsAstarGridBenchmark(1);
        //testAllTOADGENLevelsBaumgartenBenchmark(1);
    }

    private static void testLevel() {
        AgentMarioGame game = new AgentMarioGame();
        game.runGame(new mff.agents.astar.Agent(), LevelLoader.getLevel("./levels/notWorking/lvl-1.txt"),
                200,0,true);
    }

    private static void testLevelBaumgarten() {
        String level = LevelLoader.getLevel("./levels/DagstuhlGAN/lvl-4.txt");
        MarioGame game = new MarioGame();
        System.out.println("-------");
        GenerateLevel.printResults(game.runGame(new agents.robinBaumgarten.Agent(), level, 30, 0, true));
    }

    private static void testLevelGrid() {
        var game = new AgentBenchmarkGame();
        String levelPath = "./levels/DagstuhlGAN/lvl-4.txt";
        IMarioAgentMFF astarGridAgent = new mff.agents.astarGrid.Agent();
        AStarTree.NODE_DEPTH_WEIGHT = 1f;
        AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
        AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 0f;
        AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 100f;
        AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 7f;
        AStarGridHelper.giveLevelTilesWithPath(astarGridAgent, levelPath);
        var result = game.runGame(astarGridAgent, LevelLoader.getLevel(levelPath),
                200, 0, true);

        System.out.println("win: " + result.win);
    }

    private static void testLevelBenchmark() {
        String level = LevelLoader.getLevel("./levels/DagstuhlGAN/lvl-4.txt");
        AgentBenchmarkGame game = new AgentBenchmarkGame();
        IMarioAgentMFF astarGridAgent = new mff.agents.astarGrid.Agent();
        AStarTree.NODE_DEPTH_WEIGHT = 2f;
        AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
        AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 1f;
        AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 20f;
        AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 7f;
        AStarGridHelper.giveLevelTilesWithPath(astarGridAgent, "./levels/original/lvl-1.txt");
        var agentStats = game.runGame(astarGridAgent, level, 30, 0, false);
        agentStats.level = "original-1";
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
        System.out.println("jumps: "+ agentStats.numberOfJumps);
    }

    private static void testLevelWaypoints() {
        AgentMarioGame game = new AgentMarioGame();
        String levelPath = "./levels/showcase/lvl-1.txt";
        IMarioAgentMFF astarWaypointsAgent = new mff.agents.astarWaypoints.Agent();
        AStarGridHelper.giveLevelTilesWithPath(astarWaypointsAgent, levelPath);
        AStarGridHelper.giveGridPath(astarWaypointsAgent, levelPath);
        game.runGame(astarWaypointsAgent, LevelLoader.getLevel(levelPath),
                200, 0, true);
    }

    private static void testAllOriginalLevels() {
        for (int i = 1; i < 16; i++) {
            AgentMarioGame game = new AgentMarioGame();
            game.runGame(new mff.agents.astar.Agent(), LevelLoader.getLevel("./levels/original/lvl-" + i + ".txt"),
                    200, 0, true);
        }
    }

    private static void testAllOriginalLevelsGrid() {
        for (int i = 1; i < 16; i++) {
            AgentMarioGame game = new AgentMarioGame();
            String levelPath = "./levels/original/lvl-" + i + ".txt";
            IMarioAgentMFF astarGridAgent = new mff.agents.astarGrid.Agent();
            AStarTree.NODE_DEPTH_WEIGHT = 1f;
            AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
            AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 2f;
            AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 5f;
            AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 7f;
            AStarGridHelper.giveLevelTilesWithPath(astarGridAgent, levelPath);
            game.runGame(astarGridAgent, LevelLoader.getLevel(levelPath),
                    200, 0, true);
        }
    }

    private static void testAllKrysLevelsGrid() {
        for (int i = 1; i <= 100; i++) {
            AgentMarioGame game = new AgentMarioGame();
            String levelPath = "./levels/krys/lvl-" + i + ".txt";
            IMarioAgentMFF astarGridAgent = new mff.agents.astarGrid.Agent();
            AStarTree.NODE_DEPTH_WEIGHT = 1f;
            AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
            AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 2f;
            AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 5f;
            AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 7f;
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
            game.runGame(agent, LevelLoader.getLevel("./levels/notWorking/lvl-9.txt"), 200, 0, true);
        }
    }

    private static void testAllTOADGENLevelsAstarGridBenchmark(int origLevelnum) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("outputOrig3-3TOADGAN_mffAstarGrid.txt", true))) {
            writer.println("original level: orig_3-3");
            for (int i = 1; i <= 100; i++) {
                String level = LevelLoader.getLevel("./levels/original_3-3_TOADGAN/lvl-" + i + ".txt");
                AgentBenchmarkGame game = new AgentBenchmarkGame();
                IMarioAgentMFF astarGridAgent = new mff.agents.astarGrid.Agent();
                AStarTree.NODE_DEPTH_WEIGHT = 2f;
                AStarTree.TIME_TO_FINISH_WEIGHT = 2f;
                AStarTree.DISTANCE_FROM_PATH_TOLERANCE = 1f;
                AStarTree.DISTANCE_FROM_PATH_ADDITIVE_PENALTY = 20f;
                AStarTree.DISTANCE_FROM_PATH_MULTIPLICATIVE_PENALTY = 7f;
                AStarGridHelper.giveLevelTilesWithPath(astarGridAgent, "./levels/original_3-3_TOADGAN/lvl-" + i + ".txt");
                var agentStats = game.runGame(astarGridAgent, level, 20, 0, false);
                agentStats.level = "orig_3-3_TOADGAN-" + i + ".txt";
                var mostBacktrackedNodes = ((IAgentBenchmarkBacktrack) astarGridAgent).getMostBacktrackedNodes();

                System.out.println("level: " + agentStats.level);
                System.out.println("win: " + agentStats.win);
                // Write output to the file
                writer.println("level: " + agentStats.level);
                writer.println("win: " + agentStats.win);
                //writer.println("percentageTravelled: " + agentStats.percentageTravelled);
                //writer.println("runTime: " + agentStats.runTime);
                //writer.println("totalGameTicks: " + agentStats.totalGameTicks);
                //writer.println("totalPlanningTime: " + agentStats.totalPlanningTime);
                //writer.println("searchCalls: " + agentStats.searchCalls);
                //writer.println("nodesEvaluated: " + agentStats.nodesEvaluated);
                //writer.println("mostBacktrackedNodes: " + mostBacktrackedNodes);
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

    private static void testAllTOADGENLevelsBaumgartenBenchmark(int origLevelnum){
        try (PrintWriter writer = new PrintWriter(new FileWriter("outputOrig3-3TOADGAN_Baumgarten.txt", true))) {
            writer.println("original level: orig_3-3");
            for(int i = 1; i <= 100; i++){
                //String level = LevelLoader.getLevel("./levels/orig-lvl-"+origLevelnum+"/lvl-"+i+".txt");
                String level = LevelLoader.getLevel("./levels/original_3-3_TOADGAN/lvl-" + i + ".txt");
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
