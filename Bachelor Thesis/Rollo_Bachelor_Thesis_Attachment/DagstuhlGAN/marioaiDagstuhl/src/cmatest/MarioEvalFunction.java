package cmatest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.level.LevelParser;
import ch.idsia.tools.EvaluationInfo;
import communication.GANProcess;
import communication.MarioProcess;
import engine.core.MarioResult;
import fr.inria.optimization.cmaes.fitness.IObjectiveFunction;
import mff.agents.benchmark.AgentStats;
import reader.JsonReader;
import static reader.JsonReader.JsonToDoubleArray;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class MarioEvalFunction implements IObjectiveFunction {

	// This is the distance that Mario traverses when he beats the short levels
	// that we are generating. It would need to be changed if we train on larger
	// levels or in any way change the level length.
	public static final int LEVEL_LENGTH = 704;

	private GANProcess ganProcess;
	private MarioProcess marioProcess;

	// changing floor will change the reason for termination
	// (in conjunction with the target value)
	// see cma.options.stopFitness
	static double floor = 0.0;

	public MarioEvalFunction() throws IOException {
		// set up process for GAN
		ganProcess = new GANProcess();
		ganProcess.start();
		// set up mario game
		marioProcess = new MarioProcess();
		marioProcess.start();        
		// consume all start-up messages that are not data responses
		String response = "";
		while(!response.equals("READY")) {
			response = ganProcess.commRecv();
		}
	}
        
        public MarioEvalFunction(String GANPath, String GANDim) throws IOException {
		// set up process for GAN
		ganProcess = new GANProcess(GANPath, GANDim);
		ganProcess.start();
		// set up mario game
		marioProcess = new MarioProcess();
		marioProcess.start();        
		// consume all start-up messages that are not data responses
		String response = "";
		while(!response.equals("READY")) {
			response = ganProcess.commRecv();
		}
	}

	/**
	 * Takes a json String representing several levels 
	 * and returns an array of all of those Mario levels.
	 * In order to convert a single level, it needs to be put into
	 * a json array by adding extra square brackets [ ] around it.
	 * @param json Json String representation of multiple Mario levels
	 * @return Array of those levels
	 */
	public static Level[] marioLevelsFromJson(String json) {
		List<List<List<Integer>>> allLevels = JsonReader.JsonToInt(json);
		Level[] result = new Level[allLevels.size()];
		int index = 0;
		for(List<List<Integer>> listRepresentation : allLevels) {
			result[index++] = LevelParser.createLevelJson(listRepresentation);
		}
		return result;
	}
        
        public void exit() throws IOException{
            ganProcess.commSend("0");
        }

	/**
	 * Helper method to get the Mario Level from the latent vector
	 * @param x Latent vector
	 * @return Mario Level string
	 * @throws IOException Problems communicating with Python GAN process
	 */
	public String levelFromLatentVector(double[] x) throws IOException {
		x = mapArrayToOne(x);
		// Interpret x to a level
		// Brackets required since generator.py expects of list of multiple levels, though only one is being sent here
		ganProcess.commSend("[" + Arrays.toString(x) + "]");
		String levelString = ganProcess.commRecv(); // Response to command just sent

		// mapping of numbers to symbol-tiles
		String[][] symbolMapping = {
				{"0", "X"},  // Solid/Ground
				{"1", "S"},  // Breakable
				{"2", "-"},  // Empty (passable)
				{"3", "@"},  // Full question block
				{"4", "D"},  // Empty question block
				{"5", "g"},  // Goomba
				{"6", "t"},  // Top-left pipe
				{"7", "t"}, // Top-right pipe
				{"8", "t"}, // Left pipe
				{"9", "t"} // Right pipe
		};

		// Remove the outermost brackets and split the string into rows
		String[] rows = levelString.substring(2, levelString.length() - 2).split("\\], \\[");

		StringBuilder levelBuilder = new StringBuilder();

		// Add two rows of "-" at the top
		//(to simulate the level representation of the older Mario AI Framework)
		int rowLength = rows[0].replace(", ", "").length() + 14+ 2;
		String topRow = "-".repeat(rowLength);
		levelBuilder.append(topRow).append("\n").append(topRow).append("\n");

		// Loop through each row and replace numbers with corresponding symbols
		for (int i = 0; i < rows.length; i++) {
			String row = rows[i];

			// Replace each number in the row with its corresponding symbol
			for (String[] mapping : symbolMapping) {
				row = row.replace(mapping[0], mapping[1]);
			}

			// Remove commas between the symbols
			row = row.replace(", ", "");

			// Add 14 tiles on the beggining and 2 tiles at the end, "-" or "X" based on row position
			// (to simulate the level representation of the older Mario AI Framework)

			if (i >= rows.length - 1) {
				// For the last two rows, add "X" for the ground
				row = "X".repeat(14) + row + "X".repeat(2);
			} else {
				// For all other rows, add "-" at the start and end
				row = "-".repeat(14) + row + "-".repeat(2);
			}

			// Add the modified row to the StringBuilder, followed by a newline
			levelBuilder.append(row).append("\n");
		}

		// Write to file
		/*
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("MyLevelTest.txt"))) {
			for (String row : rows) {
				writer.write(row.replace(", ", ""));
				writer.newLine();
			}
			System.out.println("File written successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/

		return levelBuilder.toString();
	}
	public Level levelFromLatentVectorOld(double[] x) throws IOException {
		x = mapArrayToOne(x);
		// Interpret x to a level
		// Brackets required since generator.py expects of list of multiple levels, though only one is being sent here
		ganProcess.commSend("[" + Arrays.toString(x) + "]");
		String levelString = ganProcess.commRecv(); // Response to command just sent
		Level[] levels = marioLevelsFromJson("[" +levelString + "]"); // Really only one level in this array
		Level level = levels[0];
		return level;
	}
	/**
	 * Directly send a string to the GAN (Should be array of arrays of doubles in Json format).
	 * 
	 * Note: A bit redundant: This could be called from the method above.
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public String stringToFromGAN(String input) throws IOException {
                double[] x = JsonToDoubleArray(input);
                x = mapArrayToOne(x);
		ganProcess.commSend(Arrays.toString(x));
		String levelString = ganProcess.commRecv(); // Response to command just sent
		return levelString;
	}
	
	/**
	 * Gets objective score for single latent vector.
	 */
	@Override
	public double valueOf(double[] x) {
		try {
			// Fitness is negative since CMA-ES tries to minimize

			String level = levelFromLatentVector(x);

			// double fitness = -this.marioProcess.levelEnemiesFitness(level);
			// double fitness = -this.marioProcess.levelGridJumpFitness(level);
			// double fitness = -this.marioProcess.levelBacktrackFitness(level);
			double fitness = -this.marioProcess.levelJumpFitness(level);
			// double fitness = -this.marioProcess.levelRuntimeFitness(level);
			
			return fitness;

        } catch (IOException e) {
			// Error occurred
			e.printStackTrace();
			System.exit(1);
			return Double.NaN;
		}
	}

	@Override
	public boolean isFeasible(double[] x) {
		return true;
	}

	/**
	 * Map the value in R to (-1, 1)
	 * @param valueInR
	 * @return
	 */
	public static double mapToOne(double valueInR) {
		return ( valueInR / Math.sqrt(1+valueInR*valueInR) );
	}

	public static double[] mapArrayToOne(double[] arrayInR) {
		double[] newArray = new double[arrayInR.length];
		for(int i=0; i<newArray.length; i++) {
			double valueInR = arrayInR[i];
			newArray[i] = mapToOne(valueInR);
                        //System.out.println(valueInR);
		}
		return newArray;
	}
}
