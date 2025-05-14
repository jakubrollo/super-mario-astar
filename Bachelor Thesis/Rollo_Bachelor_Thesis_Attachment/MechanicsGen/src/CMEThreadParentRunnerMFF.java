import constrainedMapElites.CMEMapElites;
import fi2pop.FI2PopGeneticAlgorithm;
import shared.Chromosome;
import shared.RepeatedLevelSlicesLibrary;
import shared.SlicesLibrary;
import shared.UniqueLevelSlicesLibrary;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CMEThreadParentRunnerMFF {

	private HashMap<String, String> parameters;
	private SlicesLibrary lib;
	private Chromosome[] chromosomes;
	private int numThreads;
	private int maxIterations;
	private int iteration;
	private String result_path;
	private int batchSize;
	private CMEMapElites map;

	public CMEThreadParentRunnerMFF(String[] args) {
		this.parameters = loadParameters("CMEParameters.txt");
		if (this.parameters == null) throw new RuntimeException("Could not load parameters");

		this.batchSize = Integer.parseInt(parameters.get("batchSize"));

		this.lib = initSlicesLibrary(this.parameters);
		loadLevels(this.parameters, this.lib);
		this.map = initCMEAlgorithm(this.parameters, this.lib);
		chromosomes = map.randomChromosomes(this.batchSize);
		this.numThreads = args.length > 1 ? Integer.parseInt(args[1]) : 25;
		this.maxIterations = args.length > 0 ? Integer.parseInt(args[0]) : 1000;
		this.iteration = 0;
		this.result_path = parameters.get("resultFolder");
	}

	private void deleteDirectory(File directoryToBeDeleted) {
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		directoryToBeDeleted.delete();
	}

	private void appendInfo(int iteration) throws FileNotFoundException {
		double[] stats = map.getStatistics();
		PrintWriter pw = new PrintWriter(new FileOutputStream(result_path + "result.txt", true));
		String result = String.join(", ", Arrays.stream(stats).mapToObj(String::valueOf).toArray(String[]::new));
		pw.println("Batch number " + iteration + ": " + result);
		pw.close();
	}

	private HashMap<String, String> readParameters(String filename) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get("", filename));
		HashMap<String, String> parameters = new HashMap<>();
		for (String line : lines) {
			if (line.trim().isEmpty()) continue;
			String[] parts = line.split("=");
			parameters.put(parts[0].trim(), parts[1].trim());
		}
		return parameters;
	}

	private HashMap<String, String> loadParameters(String filename) {
		try {
			return readParameters(filename);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private SlicesLibrary initSlicesLibrary(HashMap<String, String> parameters) {
		return parameters.get("slicesType").toLowerCase().contains("unique")
				? new UniqueLevelSlicesLibrary()
				: new RepeatedLevelSlicesLibrary();
	}

	private void loadLevels(HashMap<String, String> parameters, SlicesLibrary lib) {
		File directory = new File(parameters.get("levelFolder"));
		File[] files = directory.listFiles((f) -> f.isFile() && f.getName().endsWith("txt"));
		try {
			Arrays.sort(files, Comparator.comparing(File::getName));
			for (File f : files) {
				String[] lines = Files.readAllLines(f.toPath()).toArray(new String[0]);
				lib.addLevel(lines);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private CMEMapElites initCMEAlgorithm(HashMap<String, String> parameters, SlicesLibrary lib) {
		Random rnd = new Random(Integer.parseInt(parameters.get("seed")));
		return new CMEMapElites(
				lib,
				rnd,
				Integer.parseInt(parameters.get("appendingSize")),
				Integer.parseInt(parameters.get("chromosomeLength")),
				Integer.parseInt(parameters.get("populationSize")),
				Double.parseDouble(parameters.get("inbreeding")),
				Double.parseDouble(parameters.get("crossover")),
				Double.parseDouble(parameters.get("mutation")),
				Double.parseDouble(parameters.get("eliteProb"))
		);
	}

	private String[] serializeChromosomes() {
		String[] levels = new String[chromosomes.length];
		for (int i = 0; i < chromosomes.length; i++) {
			levels[i] = chromosomes[i].getAge() + "," + chromosomes[i].getGenes();
		}

		return levels;
	}

	private void runEvaluationThreads(String[] levels) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		int totalLevels = chromosomes.length;
		int levelsPerThread = (int) Math.ceil((double) totalLevels / numThreads);

		for (int i = 0; i < numThreads; i++) {
			int startIndex = i * levelsPerThread;
			int endIndex = Math.min(startIndex + levelsPerThread, totalLevels);

			String[] childLevels = new String[endIndex - startIndex];
			int[] levelIndices = new int[endIndex - startIndex];

			for (int j = startIndex; j < endIndex; j++) {
				childLevels[j - startIndex] = levels[j];
				levelIndices[j - startIndex] = j;
			}

			CMEThreadChildRunnerMFF childRunner = new CMEThreadChildRunnerMFF(
					i, childLevels, levelIndices, parameters, chromosomes, false
			);

			executor.submit(childRunner);
		}

		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.MINUTES);
	}

	private boolean shouldSave() {
		return iteration <= 50 || (iteration % (maxIterations / 10) == 0);
	}

	private void savePopulation() throws IOException {
		String path = parameters.get("resultFolder") + iteration + "/";
		new File(path).mkdir();
		map.writeMap(path);

		int previous = getPreviousIteration();
		if (previous >= 0) {
			deleteDirectory(new File(parameters.get("resultFolder") + previous + "/"));
		}
	}

	private int getPreviousIteration() {
		if (iteration <= 50) {
			return iteration - 1;
		} else if (iteration == maxIterations / 10) {
			return 50;
		} else {
			return iteration - (maxIterations / 10);
		}
	}

	public void run() {
		while (true) {
			try {
				System.out.println(iteration);
				String[] levels = serializeChromosomes();

				runEvaluationThreads(levels);
				map.assignChromosomes(chromosomes);
				if (shouldSave()) {
					savePopulation();
				}
				appendInfo(iteration);

				if (maxIterations > 0 && iteration >= maxIterations) break;

				chromosomes = map.getNextChromosomes(batchSize);
				iteration++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new CMEThreadParentRunnerMFF(args).run();
	}

}