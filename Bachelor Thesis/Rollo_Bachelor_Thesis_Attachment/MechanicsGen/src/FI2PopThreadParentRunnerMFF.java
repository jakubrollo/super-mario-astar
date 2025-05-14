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

public class FI2PopThreadParentRunnerMFF {

	private HashMap<String, String> parameters;
	private SlicesLibrary lib;
	private FI2PopGeneticAlgorithm gen;
	private Chromosome[] chromosomes;
	private double maxFitness;
	private int elitism;
	private Chromosome[] eliteChromosomes;
	private int numThreads;
	private int maxIterations;
	private int iteration;
	private boolean isFirstRun;
	private String result_path;

	public FI2PopThreadParentRunnerMFF(String[] args) {
		this.parameters = loadParameters("FI2PopParameters.txt");
		if (this.parameters == null) throw new RuntimeException("Could not load parameters");

		this.lib = initSlicesLibrary(this.parameters);
		loadLevels(this.parameters, this.lib);
		this.gen = initGeneticAlgorithm(this.parameters, this.lib);
		this.chromosomes = initializePopulation(this.gen);
		this.maxFitness = 0.0;
		this.elitism = Integer.parseInt(parameters.get("elitism"));
		this.eliteChromosomes = new Chromosome[elitism];
		this.numThreads = args.length > 1 ? Integer.parseInt(args[1]) : 25;
		this.maxIterations = args.length > 0 ? Integer.parseInt(args[0]) : 1000;
		this.iteration = 0;
		this.isFirstRun = true;
		this.result_path = parameters.get("resultFolder");;
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
		double[] stats = gen.getStatistics();
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

	private FI2PopGeneticAlgorithm initGeneticAlgorithm(HashMap<String, String> parameters, SlicesLibrary lib) {
		Random rnd = new Random(Integer.parseInt(parameters.get("seed")));
		return new FI2PopGeneticAlgorithm(
				lib,
				rnd,
				Integer.parseInt(parameters.get("populationSize")),
				Integer.parseInt(parameters.get("chromosomeLength")),
				Integer.parseInt(parameters.get("appendingSize")),
				Double.parseDouble(parameters.get("crossover")),
				Double.parseDouble(parameters.get("mutation")),
				Integer.parseInt(parameters.get("elitism"))
		);
	}

	private Chromosome[] initializePopulation(FI2PopGeneticAlgorithm gen) {
		System.out.println("Initialize FI2Pop\nFirst Batch of Chromosomes");
		gen.randomChromosomesInitialize();
		return gen.getPopulation();
	}

	private String[] serializeChromosomes() {
		String[] levels = new String[chromosomes.length];
		for (int i = 0; i < chromosomes.length; i++) {
			levels[i] = chromosomes[i].getAge() + "," + chromosomes[i].getGenes();
		}
		if(!isFirstRun) {
			for (int i = chromosomes.length - elitism; i < chromosomes.length; i++) {
				levels[i] = "";
			}
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

			FI2PopThreadChildRunnerMFF childRunner = new FI2PopThreadChildRunnerMFF(
					i, childLevels, levelIndices, parameters, eliteChromosomes, isFirstRun, chromosomes, false
			);

			executor.submit(childRunner);
		}

		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.MINUTES);
	}

	private List<Integer> findRetestIndexes() {
		List<Integer> indexes = new ArrayList<>();
		for (int i = 0; i < chromosomes.length; i++) {
			if (chromosomes[i].getFitness() > maxFitness) {
				indexes.add(i);
			}
		}
		return indexes;
	}

	private void runRetestThreads(String[] levels, List<Integer> indexes) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(numThreads);
		int total = indexes.size();
		int base = total / numThreads;
		int rem = total % numThreads;
		int current = 0;

		for (int i = 0; i < numThreads; i++) {
			int count = base + (i < rem ? 1 : 0);
			if (count == 0) continue;

			String[] subset = new String[count];
			int[] indices = new int[count];

			for (int j = 0; j < count; j++) {
				int idx = indexes.get(current++);
				subset[j] = levels[idx];
				indices[j] = idx;
			}

			executor.submit(new FI2PopThreadChildRunnerMFF(i, subset, indices, parameters, eliteChromosomes, false, chromosomes,true));
		}

		executor.shutdown();
		executor.awaitTermination(10, TimeUnit.MINUTES);
	}

	private void updateMaxFitness(List<Integer> indexes) {
		for (int idx : indexes) {
			double fitness = chromosomes[idx].getFitness();
			if (fitness > maxFitness) {
				maxFitness = fitness;
			}
		}
	}

	private boolean shouldSave() {
		return iteration <= 50 || (iteration % (maxIterations / 10) == 0);
	}

	private void savePopulation() throws IOException {
		String path = parameters.get("resultFolder") + iteration + "/";
		new File(path).mkdir();
		gen.writePopulation(path);

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

				List<Integer> retestIndexes = findRetestIndexes();

				if (!retestIndexes.isEmpty()) {
					runRetestThreads(levels, retestIndexes);
					updateMaxFitness(retestIndexes);
				}

				if (shouldSave()) {
					savePopulation();
				}
				appendInfo(iteration);

				if (maxIterations > 0 && iteration >= maxIterations) break;

				gen.getNextGeneration();
				chromosomes = gen.getPopulation();
				for (int i = 0; i < elitism; i++) {
					eliteChromosomes[i] = chromosomes[chromosomes.length - 1 - i];
				}
				isFirstRun = false;
				iteration++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new FI2PopThreadParentRunnerMFF(args).run();
	}

}