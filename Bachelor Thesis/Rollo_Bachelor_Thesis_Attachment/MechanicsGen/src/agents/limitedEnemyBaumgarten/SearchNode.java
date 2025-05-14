package agents.limitedEnemyBaumgarten;

import engine.core.MarioForwardModel;
import engine.helper.GameStatus;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import engine.core.MarioWorld;
import engine.helper.SpriteType;
import engine.core.MarioSprite;

public class SearchNode {
    public int timeElapsed = 0;
    public float remainingTimeEstimated = 0;
    public float remainingTime = 0;

    public SearchNode parentPos = null;
    public MarioForwardModel sceneSnapshot = null;
    public int distanceFromOrigin = 0;
    public boolean hasBeenHurt = false;
    public boolean isInVisitedList = false;

    boolean[] action;
    int repetitions = 1;

    public float calcRemainingTime(float marioX, float marioXA) {
        return (100000 - (maxForwardMovement(marioXA, 1000) + marioX)) / Helper.maxMarioSpeed - 1000;
    }

    public float getRemainingTime() {
        if (remainingTime > 0)
            return remainingTime;
        else
            return remainingTimeEstimated;
    }

    public float estimateRemainingTimeChild(boolean[] action, int repetitions) {
        float[] childbehaviorDistanceAndSpeed = Helper.estimateMaximumForwardMovement(
                this.sceneSnapshot.getMarioFloatVelocity()[0], action, repetitions);
        return calcRemainingTime(this.sceneSnapshot.getMarioFloatPos()[0] + childbehaviorDistanceAndSpeed[0],
                childbehaviorDistanceAndSpeed[1]);
    }

    public SearchNode(boolean[] action, int repetitions, SearchNode parent) {
        this.parentPos = parent;
        if (parent != null) {
            this.remainingTimeEstimated = parent.estimateRemainingTimeChild(action, repetitions);
            this.distanceFromOrigin = parent.distanceFromOrigin + 1;
        }
        this.action = action;
        this.repetitions = repetitions;
        if (parent != null)
            timeElapsed = parent.timeElapsed + repetitions;
        else
            timeElapsed = 0;
    }

    public void initializeRoot(MarioForwardModel model) {
        if (this.parentPos == null) {
            this.sceneSnapshot = model.clone();
            this.remainingTimeEstimated = calcRemainingTime(model.getMarioFloatPos()[0], 0);
        }
    }

    public float simulatePos() {
        this.sceneSnapshot = parentPos.sceneSnapshot.clone();

        // Save enemy states with a unique mapping
        ArrayList<MarioSprite> sprites = this.sceneSnapshot.getCurWorld().getSprites();
        HashMap<MarioSprite, Object[]> enemyStates = new HashMap<>(); // Map sprite reference to its state

        for (MarioSprite sprite : sprites) {
            SpriteType type = sprite.type;
            boolean isEnemy = type == SpriteType.GOOMBA || type == SpriteType.GOOMBA_WINGED ||
                    type == SpriteType.RED_KOOPA || type == SpriteType.RED_KOOPA_WINGED ||
                    type == SpriteType.GREEN_KOOPA || type == SpriteType.GREEN_KOOPA_WINGED ||
                    type == SpriteType.SPIKY || type == SpriteType.SPIKY_WINGED ||
                    type == SpriteType.BULLET_BILL || type == SpriteType.ENEMY_FLOWER ||
                    type == SpriteType.SHELL;

            if (isEnemy) {
                // Store sprite reference and its state in a map
                enemyStates.put(sprite, new Object[] { sprite.x, sprite.y, sprite.xa, sprite.ya });

                // Move enemies far away
                sprite.x = 1000.0f;
                sprite.y = 1000.0f;
                sprite.xa = 0.0f;
                sprite.ya = 0.0f;
            }
        }

        // Run simulation with enemies gone
        for (int i = 0; i < repetitions; i++) {
            this.sceneSnapshot.advance(action);
        }

        // Restore enemy states
        ArrayList<MarioSprite> postSimulationSprites = this.sceneSnapshot.getCurWorld().getSprites();
        for (Map.Entry<MarioSprite, Object[]> entry : enemyStates.entrySet()) {
            MarioSprite sprite = entry.getKey();
            Object[] state = entry.getValue();
            // Only restore if the sprite still exists
            if (postSimulationSprites.contains(sprite)) {
                sprite.x = (float) state[0];
                sprite.y = (float) state[1];
                sprite.xa = (float) state[2];
                sprite.ya = (float) state[3];
            }
        }

        remainingTime = calcRemainingTime(this.sceneSnapshot.getMarioFloatPos()[0],
                this.sceneSnapshot.getMarioFloatVelocity()[0]);
        if (isInVisitedList) {
            remainingTime += Helper.visitedListPenalty;
        }

        hasBeenHurt = false;

        return remainingTime;
    }

    public ArrayList<SearchNode> generateChildren() {
        ArrayList<SearchNode> list = new ArrayList<SearchNode>();
        ArrayList<boolean[]> possibleActions = Helper.createPossibleActions(this);
        if (this.isLeafNode()) {
            possibleActions.clear();
        }
        for (boolean[] action : possibleActions) {
            list.add(new SearchNode(action, repetitions, this));
        }
        return list;
    }


    public boolean isLeafNode() {
        if (this.sceneSnapshot == null) {
            return false;
        }
        var status = this.sceneSnapshot.getGameStatus();
        if (status == GameStatus.LOSE) {
            return false; // Ignore deaths, possibly caused by enemies
        }
        return status != GameStatus.RUNNING;
    }

    private float maxForwardMovement(float initialSpeed, int ticks) {
        float y = ticks;
        float s0 = initialSpeed;
        return (float) (99.17355373 * Math.pow(0.89, y + 1) - 9.090909091 * s0 * Math.pow(0.89, y + 1) + 10.90909091 * y
                - 88.26446282 + 9.090909091 * s0);
    }

}
