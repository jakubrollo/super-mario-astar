package engine.core;

import java.util.ArrayList;

import engine.helper.EventType;
import engine.helper.GameStatus;
import engine.helper.SpriteType;

public class MarioForwardModel {
    private static final int OBS_SCENE_SHIFT = 16;

    public static final int OBS_NONE = 0;
    public static final int OBS_UNDEF = -42;

    public static final int OBS_SOLID = OBS_SCENE_SHIFT + 1;
    public static final int OBS_BRICK = OBS_SCENE_SHIFT + 6;
    public static final int OBS_QUESTION_BLOCK = OBS_SCENE_SHIFT + 8;
    public static final int OBS_COIN = OBS_SCENE_SHIFT + 15;

    public static final int OBS_PYRAMID_SOLID = OBS_SCENE_SHIFT + 2;
    public static final int OBS_PIPE_BODY_RIGHT = OBS_SCENE_SHIFT + 21;
    public static final int OBS_PIPE_BODY_LEFT = OBS_SCENE_SHIFT + 20;
    public static final int OBS_PIPE_TOP_RIGHT = OBS_SCENE_SHIFT + 19;
    public static final int OBS_PIPE_TOP_LEFT = OBS_SCENE_SHIFT + 18;
    public static final int OBS_USED_BLOCK = OBS_SCENE_SHIFT + 14;
    public static final int OBS_BULLET_BILL_BODY = OBS_SCENE_SHIFT + 5;
    public static final int OBS_BULLET_BILL_NECT = OBS_SCENE_SHIFT + 4;
    public static final int OBS_BULLET_BILL_HEAD = OBS_SCENE_SHIFT + 3;
    public static final int OBS_BACKGROUND = OBS_SCENE_SHIFT + 47;
    public static final int OBS_PLATFORM_SINGLE = OBS_SCENE_SHIFT + 43;
    public static final int OBS_PLATFORM_LEFT = OBS_SCENE_SHIFT + 44;
    public static final int OBS_PLATFORM_RIGHT = OBS_SCENE_SHIFT + 45;
    public static final int OBS_PLATFORM_CENTER = OBS_SCENE_SHIFT + 46;

    public static final int OBS_PLATFORM = OBS_SCENE_SHIFT + 43;
    public static final int OBS_CANNON = OBS_SCENE_SHIFT + 3;
    public static final int OBS_PIPE = OBS_SCENE_SHIFT + 18;

    public static final int OBS_SCENE_OBJECT = OBS_SCENE_SHIFT + 84;

    public static final int OBS_FIREBALL = 16;

    public static final int OBS_GOOMBA = 2;
    public static final int OBS_GOOMBA_WINGED = 3;
    public static final int OBS_RED_KOOPA = 4;
    public static final int OBS_RED_KOOPA_WINGED = 5;
    public static final int OBS_GREEN_KOOPA = 6;
    public static final int OBS_GREEN_KOOPA_WINGED = 7;
    public static final int OBS_SPIKY = 8;
    public static final int OBS_SPIKY_WINGED = 9;
    public static final int OBS_BULLET_BILL = 10;
    public static final int OBS_ENEMY_FLOWER = 11;
    public static final int OBS_MUSHROOM = 12;
    public static final int OBS_FIRE_FLOWER = 13;
    public static final int OBS_SHELL = 14;
    public static final int OBS_LIFE_MUSHROOM = 15;

    public static final int OBS_STOMPABLE_ENEMY = 2;
    public static final int OBS_NONSTOMPABLE_ENEMY = 8;
    public static final int OBS_SPECIAL_ITEM = 12;

    public static final int OBS_ENEMY = 1;

    public static int getSpriteTypeGeneralization(SpriteType sprite, int detail) {

        return OBS_NONE;
    }

    public static int getBlockValueGeneralization(int tile, int detail) {
        if (tile == 0) {
            return OBS_NONE;
        }
        switch (detail) {
            case (0):
                switch (tile) {

                    case 48:
                    case 49:
                        return OBS_NONE;

                    case 6:
                    case 7:
                    case 50:
                    case 51:
                        return OBS_BRICK;

                    case 8:
                    case 11:
                        return OBS_QUESTION_BLOCK;
                }
                return tile + OBS_SCENE_SHIFT;
            case (1):
                switch (tile) {

                    case 48:
                    case 49:

                    case 47:
                        return OBS_NONE;

                    case 1:
                    case 2:
                    case 14:
                        return OBS_SOLID;

                    case 3:
                    case 4:
                    case 5:
                        return OBS_CANNON;

                    case 18:
                    case 19:
                    case 20:
                    case 21:
                        return OBS_PIPE;

                    case 6:
                    case 7:
                    case 50:
                    case 51:
                        return OBS_BRICK;

                    case 8:
                    case 11:
                        return OBS_QUESTION_BLOCK;

                    case 15:
                        return OBS_COIN;

                    case 44:
                    case 45:
                    case 46:
                        return OBS_PLATFORM;
                }
                return OBS_NONE;
            case (2):
                switch (tile) {

                    case 48:
                    case 49:

                    case 47:
                        return OBS_NONE;
                }

                return OBS_SCENE_OBJECT;
        }
        return OBS_UNDEF;
    }

    public final int obsGridWidth = MarioGame.tileWidth;

    public final int obsGridHeight = MarioGame.tileHeight;

    private MarioWorld world;

    public MarioWorld getWorld() {
        return this.world.clone();
    }

    public MarioWorld getCurWorld() {
        return this.world;
    }

    private int fallKill;
    private int stompKill;
    private int fireKill;
    private int shellKill;
    private int mushrooms;
    private int flowers;
    private int breakBlock;

    public MarioForwardModel(MarioWorld world) {
        this.world = world;
    }

    public MarioForwardModel clone() {
        MarioForwardModel model = new MarioForwardModel(this.world.clone());
        model.fallKill = this.fallKill;
        model.stompKill = this.stompKill;
        model.fireKill = this.fireKill;
        model.shellKill = this.shellKill;
        model.mushrooms = this.mushrooms;
        model.flowers = this.flowers;
        model.breakBlock = this.breakBlock;
        return model;
    }

    public void advance(boolean[] actions) {
        this.world.update(actions);
        for (MarioEvent e : this.world.lastFrameEvents) {
            if (e.getEventType() == EventType.FIRE_KILL.getValue()) {
                this.fireKill += 1;
            }
            if (e.getEventType() == EventType.STOMP_KILL.getValue()) {
                this.stompKill += 1;
            }
            if (e.getEventType() == EventType.FALL_KILL.getValue()) {
                this.fallKill += 1;
            }
            if (e.getEventType() == EventType.SHELL_KILL.getValue()) {
                this.shellKill += 1;
            }
            if (e.getEventType() == EventType.COLLECT.getValue()) {
                if (e.getEventParam() == SpriteType.FIRE_FLOWER.getValue()) {
                    this.flowers += 1;
                }
                if (e.getEventParam() == SpriteType.MUSHROOM.getValue()) {
                    this.mushrooms += 1;
                }
            }
            if (e.getEventType() == EventType.BUMP.getValue() && e.getEventParam() == OBS_BRICK
                    && e.getMarioState() > 0) {
                this.breakBlock += 1;
            }
        }
    }

    public GameStatus getGameStatus() {
        return this.world.gameStatus;
    }

    public float getCompletionPercentage() {
        return this.world.mario.x / (this.world.level.exitTileX * 16);
    }

    public float[] getLevelFloatDimensions() {
        return new float[]{this.world.level.width, this.world.level.height};
    }

    public int getRemainingTime() {
        return this.world.currentTimer;
    }

    public float[] getMarioFloatPos() {
        return new float[]{this.world.mario.x, this.world.mario.y};
    }

    public float[] getMarioFloatVelocity() {
        return new float[]{this.world.mario.xa, this.world.mario.ya};
    }

    public boolean getMarioCanJumpHigher() {
        return this.world.mario.jumpTime > 0;
    }

    public int getMarioMode() {
        int value = 0;
        if (this.world.mario.isLarge) {
            value = 1;
        }
        if (this.world.mario.isFire) {
            value = 2;
        }
        return value;
    }

    public boolean isMarioOnGround() {
        return this.world.mario.onGround;
    }

    public boolean mayMarioJump() {
        return this.world.mario.mayJump;
    }

    public float[] getEnemiesFloatPosAndType() {

        return new float[0];
    }

    public float[] getSpritesFloatPosAndType() {
        ArrayList<MarioSprite> spritesAlive = this.world.getSprites();
        float[] spritesPosAndType = new float[spritesAlive.size() * 3];
        for (int i = 0; i < spritesAlive.size(); i++) {
            spritesPosAndType[3 * i] = spritesAlive.get(i).type.getValue();
            spritesPosAndType[3 * i + 1] = spritesAlive.get(i).x;
            spritesPosAndType[3 * i + 2] = spritesAlive.get(i).y;
        }
        return spritesPosAndType;
    }

    public int getKillsTotal() {
        return this.fallKill + this.fireKill + this.shellKill + this.stompKill;
    }

    public int getKillsByFire() {
        return this.fireKill;
    }

    public int getKillsByStomp() {
        return this.stompKill;
    }

    public int getKillsByShell() {
        return this.shellKill;
    }

    public int getKillsByFall() {
        return this.fallKill;
    }

    public int getNumLives() {
        return this.world.lives;
    }

    public int getNumCollectedMushrooms() {
        return this.mushrooms;
    }

    public int getNumCollectedFireflower() {
        return this.flowers;
    }

    public int getNumCollectedCoins() {
        return this.world.coins;
    }

    public int getNumDestroyedBricks() {
        return this.breakBlock;
    }

    public int[] getMarioScreenTilePos() {
        return new int[]{(int) ((this.world.mario.x - this.world.cameraX) / 16), (int) (this.world.mario.y / 16)};
    }

    public int[][] getScreenCompleteObservation() {
        return this.getScreenCompleteObservation(1, 0);
    }

    public int[][] getScreenEnemiesObservation() {
        return this.getScreenEnemiesObservation(0);
    }

    public int[][] getScreenSceneObservation() {
        return this.getScreenSceneObservation(1);
    }

    public int[][] getMarioCompleteObservation() {
        return this.getMarioCompleteObservation(1, 0);
    }

    public int[][] getMarioEnemiesObservation() {
        int[][] observation = new int[obsGridWidth][obsGridHeight];
        for (int i = 0; i < obsGridWidth; i++) {
            for (int j = 0; j < obsGridHeight; j++) {
                observation[i][j] = OBS_NONE;
            }
        }
        return observation;
    }

    public int[][] getMarioEnemiesObservation(int detail) {
        int[][] observation = new int[obsGridWidth][obsGridHeight];
        for (int i = 0; i < obsGridWidth; i++) {
            for (int j = 0; j < obsGridHeight; j++) {
                observation[i][j] = OBS_NONE;
            }
        }
        return observation;
    }

    public int[][] getMarioSceneObservation() {
        return this.getMarioSceneObservation(1);
    }

    public int[][] getScreenCompleteObservation(int sceneDetail, int enemyDetail) {

        return this.world.getMergedObservation(this.world.cameraX + MarioGame.width / 2, MarioGame.height / 2,
                sceneDetail, 0);
    }

    public int[][] getScreenEnemiesObservation(int detail) {

        int[][] observation = new int[obsGridWidth][obsGridHeight];
        return observation;    }

    public int[][] getScreenSceneObservation(int detail) {
        return this.world.getSceneObservation(this.world.cameraX + MarioGame.width / 2, MarioGame.height / 2, detail);
    }

    public int[][] getMarioCompleteObservation(int sceneDetail, int enemyDetail) {

        return this.world.getSceneObservation(this.world.mario.x, this.world.mario.y, sceneDetail);
    }

    public int[][] getMarioSceneObservation(int detail) {
        return this.world.getSceneObservation(this.world.mario.x, this.world.mario.y, detail);
    }
}