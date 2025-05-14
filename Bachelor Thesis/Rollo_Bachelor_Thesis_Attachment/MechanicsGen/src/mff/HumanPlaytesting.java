package mff;

import engine.core.MarioGame;

public class HumanPlaytesting {
    public static void main(String[] args) {
        MarioGame game = new MarioGame();
        String level = LevelLoader.getLevel("./levels/notWorking/lvl-2.txt");
        game.playGame(level, 10000);
    }
}
