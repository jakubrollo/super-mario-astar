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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

public class CMEThreadChildRunnerMFF implements Runnable {
	private int id; // Child index
	String[] levels;
	int[] levelIndices;
	private Chromosome[] resultTarget;
	private SlicesLibrary lib;
	private Random rnd;
	private int appendingSize;
	private int chromosomeLength;
	private boolean isRetest;

	public CMEThreadChildRunnerMFF(int id, String[] levels, int[] levelIndices, HashMap<String, String> parameters, Chromosome[] resultTarget, boolean isRetest) {
		this.id = id;
		this.levels = levels;
		this.levelIndices = levelIndices;
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
	}



	@Override
	public void run() {

		try {
			Chromosome[] chromosomes = new Chromosome[this.levels.length];

			for (int i = 0; i < chromosomes.length; i++) {
				chromosomes[i] = new Chromosome(rnd, lib, chromosomeLength, appendingSize);
				chromosomes[i].stringInitialize(levels[i]);
				chromosomes[i].calculateResultsMFF(new AgentBenchmarkGame(),new mff.agents.astar.Agent(),10, isRetest);

				resultTarget[levelIndices[i]] = chromosomes[i];
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}