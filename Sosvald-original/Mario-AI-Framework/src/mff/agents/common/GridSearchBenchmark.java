package mff.agents.common;

import engine.core.MarioEvent;
import engine.core.MarioWorld;
import mff.LevelLoader;
import mff.agents.gridSearch.GridSearch;
import mff.agents.gridSearch.GridSearchNode;

import java.util.ArrayList;
import java.util.Objects;

public class GridSearchBenchmark {
    private static final ArrayList<String> levels = new ArrayList<>() {{
        //add("ge");
        //add("hopper");
        //add("notch");
        //add("notchParam");
        //add("notchParamRand");
        //add("ore");
        //add("patternCount");
        //add("patternOccur");
        //add("patternWeightCount");
        //add("krys");
        //add("original");
        //add("orig-lvl-1");

        //add("random-orig-lvls");
        //add("notWorking");

        add("krys-levels-TOADGAN");
    }};

    public static void main(String[] args) {
        System.out.println("levels tested: " + testGridSearchRunTime());
    }

    private static int testGridSearchRunTime() {
        long timePure = 0;
        ArrayList<Integer> toCheck = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        MarioEvent[] killEvents = new MarioEvent[0];
        for (String levelPack : levels) {
            //int levelCount = Objects.equals(levelPack, "orig-lvl-1") ? 100 : 1000;//15 : 100;
            for (int i = 1; i <= 100; i++) {
                String level = LevelLoader.getLevel("./levels/" + levelPack + "/lvl-" + i + ".txt");
                MarioWorld world = new MarioWorld(killEvents);
                world.initializeLevel(level, 1000000);
                int[][] levelTiles = world.level.getLevelTiles();
                int marioTileX = world.level.marioTileX;
                int marioTileY = world.level.marioTileY;

                GridSearch gridSearch = new GridSearch(levelTiles, marioTileX, marioTileY, 1);
                long startTimePure = System.currentTimeMillis();
                System.out.println("./levels/" + levelPack + "/lvl-" + i + ".txt");
                ArrayList<GridSearchNode> resultPath = gridSearch.findGridPath();
                long endTimePure = System.currentTimeMillis();
                timePure += endTimePure - startTimePure;
                toCheck.add(resultPath.size());
                System.out.println(gridSearch.success);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("grid search time: " + (endTime - startTime) + " ms");
        System.out.println("grid search time pure: " + (timePure) + " ms");
        return toCheck.size();
    }
}
