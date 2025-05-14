package mff.agents.benchmark;

import engine.core.MarioForwardModel;
import engine.core.MarioRender;
import engine.core.MarioWorld;
import engine.helper.EventType;
import engine.helper.GameStatus;
import engine.helper.MarioActions;
import engine.helper.SpriteType;
import mff.agents.common.IMarioAgentMFF;
import mff.agents.common.MarioTimerSlim;
import mff.forwardmodel.common.Converter;
import mff.forwardmodel.slim.core.MarioForwardModelSlim;

import engine.core.MarioEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AgentBenchmarkGame {
    public static final long maxTime = 45;
    public static final int width = 256;
    public static final int height = 256;
    public boolean pause = false;

    //visualization
    private MarioRender render = null;

    private IMarioAgentMFF agent = null;
    private MarioEvent[] killEvents;
    Set<MarioEvent> mustDoSet = new HashSet<>();
    private ArrayList<MarioEvent> mustDoEvents = new ArrayList<>();
    HashSet<MarioEvent> completedMustDoEvents = new HashSet<>();

    private int getDelay(int fps) {
        if (fps <= 0) {
            return 0;
        }
        return 1000 / fps;
    }

    public AgentBenchmarkGame(MarioEvent[] killEvents, MarioEvent[] mustDoEvents) {
        this.killEvents = killEvents;
        if (mustDoEvents != null) {
            for (MarioEvent e : mustDoEvents) {
                mustDoSet.add(e);
                this.mustDoEvents.add(e);
            }
        }
    }

    public AgentBenchmarkGame(MarioEvent[] killEvents) {
        this(killEvents, null);
    }

    public AgentBenchmarkGame() {
        this(null, null);
    }

    public static AgentBenchmarkGame withMustDoEvents(MarioEvent[] mustDoEvents) {
        return new AgentBenchmarkGame(null, mustDoEvents);
    }

    private boolean eventsMatch(MarioEvent e1, MarioEvent e2) {
        return e1.getEventType() == e2.getEventType()
                && e1.getEventParam() == e2.getEventParam();
    }

    private void setAgent(IMarioAgentMFF agent) {
        this.agent = agent;
    }

    public AgentStats runGame(IMarioAgentMFF agent, String level, int timer, int marioState, boolean visuals) {
        return runGame(agent, level, timer, marioState, visuals, visuals ? 30 : 0, 2);
    }

    public AgentStats runGame(IMarioAgentMFF agent, String level, int timer, int marioState, boolean visuals, int fps, float scale) {
        JFrame window = null;
        if (visuals) {
            window = new JFrame("Mario AI Framework");
            this.render = new MarioRender(scale);
            window.setContentPane(this.render);
            window.pack();
            window.setResizable(false);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.render.init();
            window.setVisible(true);
        }
        this.setAgent(agent);
        AgentStats agentStats = gameLoop(level, timer, marioState, visuals, fps);
        if (visuals)
            window.dispose();
        return agentStats;
    }

    private AgentStats gameLoop(String level, int timer, int marioState, boolean visual, int fps) {
        MarioWorld world = new MarioWorld(killEvents);
        world.visuals = visual;
        world.initializeLevel(level, 1000 * timer);
        if (visual) {
            world.initializeVisuals(this.render.getGraphicsConfiguration());
        }
        world.mario.isLarge = marioState > 0;
        world.mario.isFire = marioState > 1;
        world.update(new boolean[MarioActions.numberOfActions()]);
        long currentTime = System.currentTimeMillis();

        //initialize graphics
        VolatileImage renderTarget = null;
        Graphics backBuffer = null;
        Graphics currentBuffer = null;
        if (visual) {
            renderTarget = this.render.createVolatileImage(width, height);
            backBuffer = this.render.getGraphics();
            currentBuffer = renderTarget.getGraphics();
            this.render.addFocusListener(this.render);
        }

        MarioTimerSlim agentTimer;
        MarioForwardModelSlim slimModel = Converter.originalToSlim(new MarioForwardModel(world.clone()), 27);
        this.agent.initialize(slimModel);

        AgentStats agentStats = new AgentStats();

        int totalGameTicks = 0;
        long totalPlanningTime = 0;
        long runStart = System.currentTimeMillis();
        float startX = -100;
        int startTime = -100;

        while (world.gameStatus == GameStatus.RUNNING) {
            if (!this.pause) {
                // get actions
                agentTimer = new MarioTimerSlim(maxTime);
                long planningStart = System.currentTimeMillis();
                boolean[] actions = this.agent.getActions(slimModel.clone(), agentTimer);
                long planningEnd = System.currentTimeMillis();
                totalPlanningTime += planningEnd - planningStart;

                // update world
                world.update(actions);
                totalGameTicks++;

                // Stats counters
                int kills = 0;
                int jumps = 0;
                int collect = 0;
                int stompkills = 0;
                int shellkills = 0;
                int fallkills = 0;
                int mushroomcollect = 0;
                int bumpBrick = 0;
                int bumpQuestionBlock = 0;

                for (MarioEvent e : world.lastFrameEvents) {
                    if (e.getEventType() == EventType.STOMP_KILL.getValue()) {
                        stompkills += 1;
                        kills += 1;
                    }
                    if (e.getEventType() == EventType.FIRE_KILL.getValue()) {
                        kills += 1;
                    }
                    if (e.getEventType() == EventType.FALL_KILL.getValue()) {
                        fallkills += 1;
                        kills += 1;
                    }
                    if (e.getEventType() == EventType.SHELL_KILL.getValue()) {
                        shellkills += 1;
                        kills += 1;
                    }
                    if (e.getEventType() == EventType.JUMP.getValue()) {
                        jumps += 1;
                        startX = e.getMarioX(); // Start tracking jump distance
                        startTime = e.getTime(); // Start tracking jump air time
                    }
                    if (e.getEventType() == EventType.LAND.getValue()) {
                        // Update max X jump distance
                        float jumpDistance = Math.abs(e.getMarioX() - startX);
                        if (jumpDistance > agentStats.maxXJump) {
                            agentStats.maxXJump = jumpDistance;
                        }

                        // Update max air jump time
                        int airTime = e.getTime() - startTime;
                        if (airTime > agentStats.maxJumpAirTime) {
                            agentStats.maxJumpAirTime = airTime;
                        }
                    }
                    if (e.getEventType() == EventType.COLLECT.getValue()) {
                        if (e.getEventParam() == 15) {
                            collect += 1;
                        }
                        if (e.getEventParam() == SpriteType.MUSHROOM.getValue()) {
                            mushroomcollect += 1;
                        }
                    }

                    if (e.getEventType() == EventType.BUMP.getValue()) {
                        if (e.getEventParam() == MarioForwardModel.OBS_BRICK) {
                            bumpBrick += 1;
                        }
                        if (e.getEventParam() == MarioForwardModel.OBS_QUESTION_BLOCK) {
                            bumpQuestionBlock += 1;
                        }
                    }
                }

                for (MarioEvent mustDo : mustDoEvents) {
                    if (world.lastFrameEvents.contains(mustDo) && !completedMustDoEvents.contains(mustDo)) {
                        completedMustDoEvents.add(mustDo);
                    }
                }

                // Update agentStats
                agentStats.numOfCollectedCoins += collect;
                agentStats.numOfKills += kills;
                agentStats.numOfJumps += jumps;
                agentStats.killsByStomp += stompkills;
                agentStats.killsByShell += shellkills;
                agentStats.killsByFall += fallkills;
                agentStats.numOfCollectedMushrooms += mushroomcollect;
                agentStats.numBumpBrick += bumpBrick;
                agentStats.numBumpQuestionBlock += bumpQuestionBlock;

                // keep forward model up with world
                slimModel.advance(actions);
            }

            //render world
            if (visual) {
                this.render.renderWorld(world, renderTarget, backBuffer, currentBuffer);
            }

            //check if delay needed
            if (fps > 0) {
                try {
                    currentTime += this.getDelay(fps);
                    Thread.sleep(Math.max(0, currentTime - System.currentTimeMillis()));
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        agentStats.marioMode = 0;
        if (world.mario.isLarge) {
            agentStats.marioMode = 1;
        }
        if (world.mario.isFire) {
            agentStats.marioMode = 2;
        }
        if (mustDoSet != null && completedMustDoEvents.size() == mustDoSet.size()) {
            agentStats.hasDoneMustDoEvents = true;
        }

        long runEnd = System.currentTimeMillis();
        agentStats.runTime = runEnd - runStart;
        agentStats.totalGameTicks = totalGameTicks;
        agentStats.totalPlanningTime = totalPlanningTime;
        agentStats.win = world.gameStatus == GameStatus.WIN;
        agentStats.currentLives = world.lives;
        agentStats.remainingTime = world.currentTimer;

        agentStats.percentageTravelled = (world.mario.x - world.level.marioTileX * 16)
                / (world.level.exitTileX * 16 - world.level.marioTileX * 16);

        try {
            IAgentBenchmark agentBenchmark = (IAgentBenchmark) this.agent;
            agentStats.searchCalls = agentBenchmark.getSearchCalls();
            agentStats.nodesEvaluated = agentBenchmark.getNodesEvaluated();
        }
        catch (ClassCastException e) {
            System.out.println("Agent doesn't implement IAgentBenchmark interface.");
        }
        return agentStats;
    }
}
