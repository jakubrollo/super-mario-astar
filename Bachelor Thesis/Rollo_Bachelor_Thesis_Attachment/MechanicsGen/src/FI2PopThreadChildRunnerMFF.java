import engine.core.MarioEvent;
import engine.core.MarioForwardModel;
import engine.helper.EventType;
import mff.agents.benchmark.AgentBenchmarkGame;
import mff.agents.common.IMarioAgentMFF;
import shared.Chromosome;
import shared.RepeatedLevelSlicesLibrary;
import shared.SlicesLibrary;
import shared.UniqueLevelSlicesLibrary;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

public class FI2PopThreadChildRunnerMFF implements Runnable {
	private int id; // Child index
	String[] levels;
	int[] levelIndices;
	private Chromosome[] resultTarget;
	private HashMap<String, String> parameters;
	private SlicesLibrary lib;
	private Random rnd;
	private int appendingSize;
	private int chromosomeLength;
	private Chromosome[] eliteChromosomes;
	private boolean isFirstRun;
	private boolean isRetest;

	public FI2PopThreadChildRunnerMFF(int id, String[] levels, int[] levelIndices, HashMap<String, String> parameters, Chromosome[] givenEliteChromosomes, boolean isFirstRun,  Chromosome[] resultTarget, boolean isRetest) {
		this.id = id;
		this.levels = levels;
		this.levelIndices = levelIndices;
		this.parameters = parameters;
		this.eliteChromosomes = givenEliteChromosomes;
		this.resultTarget = resultTarget;
		this.isRetest = isRetest;

		this.lib = parameters.get("slicesType").toLowerCase().contains("unique")
				? new UniqueLevelSlicesLibrary()
				: new RepeatedLevelSlicesLibrary();

		File directory = new File(parameters.get("levelFolder"));
		File[] files = directory.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith("txt"));
		try {
			Arrays.sort(files, Comparator.comparing(File::getName));
			for (File f : files) {
				String[] lines = Files.readAllLines(f.toPath()).toArray(new String[0]);
				lib.addLevel(lines);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.rnd = new Random(Integer.parseInt(parameters.get("seed")));
		this.appendingSize = Integer.parseInt(parameters.get("appendingSize"));
		this.chromosomeLength = Integer.parseInt(parameters.get("chromosomeLength"));
		this.isFirstRun = isFirstRun;
	}

	private static AgentBenchmarkGame[] prepareGamesWithEvents(MarioEvent[] events){
		return new AgentBenchmarkGame[] {
				new AgentBenchmarkGame(events),
				AgentBenchmarkGame.withMustDoEvents(events)
		};
	}


	private static void runExperiment(Chromosome c, HashMap<String, String> parameters, boolean isRetest) {
		var experimentType = parameters.get("experimentType").toLowerCase();
		var agentType = parameters.get("agentType").toLowerCase();
		switch(experimentType) {
			case "punishingmodel":
				switch(agentType) {
					case "run":
						c.calculateResultsMFF(
								prepareGamesWithEvents(new MarioEvent[] {new MarioEvent(EventType.RUN)}),
								new mff.agents.astar.Agent(), 6, isRetest);
						break;
					case "coin":
						c.calculateResultsMFF(
								prepareGamesWithEvents(new MarioEvent[] {new MarioEvent(EventType.COLLECT, 15)}), //15 is for coin
								new mff.agents.astar.Agent(), 6, isRetest);
						break;
					case "mushroom":
						c.calculateResultsMFF(
								prepareGamesWithEvents(new MarioEvent[] {new MarioEvent(EventType.COLLECT, MarioForwardModel.OBS_MUSHROOM)}),
								new mff.agents.astar.Agent(), 6, isRetest);
						break;
					case "highjump":
						c.calculateResultsMFF(
								prepareGamesWithEvents(new MarioEvent[] {new MarioEvent(EventType.HIGH_JUMP)}),
								new mff.agents.astar.Agent(), 6, isRetest);
						break;
					case "shell":
						c.calculateResultsMFF(
								prepareGamesWithEvents(new MarioEvent[] {new MarioEvent(EventType.SHELL_KILL)}),
								new mff.agents.astar.Agent(), 6, isRetest);
						break;
					case "stomp":
						c.calculateResultsMFF(
								prepareGamesWithEvents(new MarioEvent[] {new MarioEvent(EventType.STOMP_KILL)}),
								new mff.agents.astar.Agent(), 6, isRetest);
						break;
				}
				break;
			case "limitedagent":
				switch(agentType) {
					case "jump":
						c.calculateResultsMFF(
								new AgentBenchmarkGame[] { new AgentBenchmarkGame(), new AgentBenchmarkGame() },
								new IMarioAgentMFF[] { new mff.agents.limitedJumpAstar.Agent(), new mff.agents.astar.Agent() },
								6, isRetest
						);
						break;
					case "enemy":
						c.calculateResultsMFF(
								new AgentBenchmarkGame[] { new AgentBenchmarkGame(), new AgentBenchmarkGame() },
								new IMarioAgentMFF[] { new mff.agents.limitedEnemyAstar.Agent(), new mff.agents.astar.Agent() },
								6, isRetest
						);
						break;
					case "norun":
						c.calculateResultsMFF(
								new AgentBenchmarkGame[] { new AgentBenchmarkGame(), new AgentBenchmarkGame() },
								new IMarioAgentMFF[] { new mff.agents.limitedRunAstar.Agent(), new mff.agents.astar.Agent() },
								6, isRetest
						);
						break;
				}
				break;
		}
	}

	@Override
	public void run() {

		try {
			Chromosome[] chromosomes = new Chromosome[this.levels.length];

			for (int i = 0; i < chromosomes.length; i++) {
				if (!isFirstRun && levels[i].trim().isEmpty()) {
					chromosomes[i] = eliteChromosomes[chromosomes.length - 1 - i];
					chromosomes[i].increaseAge();
				} else {
					chromosomes[i] = new Chromosome(rnd, lib, chromosomeLength, appendingSize);
					chromosomes[i].stringInitialize(levels[i]);
					runExperiment(chromosomes[i], parameters, isRetest);
				}
				resultTarget[levelIndices[i]] = chromosomes[i];
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}