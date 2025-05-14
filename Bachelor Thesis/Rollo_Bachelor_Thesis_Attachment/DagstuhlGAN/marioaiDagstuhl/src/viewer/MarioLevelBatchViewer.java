package viewer;

import basicMap.Settings;
import ch.idsia.ai.tasks.ProgressTask;
import ch.idsia.mario.engine.LevelRenderer;
import ch.idsia.mario.engine.level.Level;
import ch.idsia.mario.engine.level.LevelParser;
import ch.idsia.tools.CmdLineOptions;
import ch.idsia.tools.EvaluationOptions;
import cmatest.MarioEvalFunction;
import reader.JsonReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static reader.JsonReader.JsonToDoubleArray;

public class MarioLevelBatchViewer {

	public static final int BLOCK_SIZE = 16;
	public static final int LEVEL_HEIGHT = 14;

	public static BufferedImage getLevelImage(Level level, boolean excludeBufferRegion) {
		EvaluationOptions options = new CmdLineOptions(new String[0]);
		ProgressTask task = new ProgressTask(options);
		options.setLevel(level);
		task.setOptions(options);

		int relevantWidth = (level.width - (excludeBufferRegion ? 2 * LevelParser.BUFFER_WIDTH : 0)) * BLOCK_SIZE;
		BufferedImage image = new BufferedImage(relevantWidth, LEVEL_HEIGHT * BLOCK_SIZE, BufferedImage.TYPE_INT_RGB);
		LevelRenderer.renderArea((Graphics2D) image.getGraphics(), level, 0, 0,
				excludeBufferRegion ? LevelParser.BUFFER_WIDTH * BLOCK_SIZE : 0, 0,
				relevantWidth, LEVEL_HEIGHT * BLOCK_SIZE);
		return image;
	}

	public static void saveLevelImage(Level level, String filename, boolean clipBuffer) throws IOException {
		BufferedImage image = getLevelImage(level, clipBuffer);
		File file = new File(filename + ".jpg");
		ImageIO.write(image, "jpg", file);
		System.out.println("Image saved: " + file.getName());
	}

	public static void saveLevelText(String levelString, File outputFile) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
			for (String row : levelString.split("\n")) {
				writer.write(row.replace(", ", ""));
				writer.newLine();
			}
		}
		System.out.println("Level file saved: " + outputFile.getName());
	}

	public static int getNextAvailableDirectoryIndex() {
		int index = 0;
		while (new File(String.valueOf(index)).exists()) {
			index++;
		}
		return index;
	}

	public static List<String> readVectorsFromFile(String filename) throws IOException {
		List<String> vectors = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			if (!line.isEmpty()) {
				vectors.add(line);
			}
		}
		reader.close();
		return vectors;
	}

	public static void main(String[] args) throws IOException {
		Settings.setPythonProgram();
		MarioEvalFunction eval = new MarioEvalFunction();

		String vectorFile = "imagesVector.txt";
		List<String> vectors = readVectorsFromFile(vectorFile);

		int dirIndex = getNextAvailableDirectoryIndex();
		File outputDir = new File(String.valueOf(dirIndex));
		outputDir.mkdir();

		Files.copy(new File(vectorFile).toPath(),
				new File(outputDir, vectorFile).toPath(),
				StandardCopyOption.REPLACE_EXISTING);

		for (int i = 0; i < vectors.size(); i++) {
			String vectorStr = vectors.get(i);
			double[] latentVector = JsonToDoubleArray(vectorStr);
			Level level = eval.levelFromLatentVectorOld(latentVector);
			String levelStr = eval.levelFromLatentVector(latentVector);

			String baseName = outputDir + File.separator + "level_" + i;
			saveLevelImage(level, baseName + "_clipped", true);
			saveLevelImage(level, baseName + "_full", false);
			saveLevelText(levelStr, new File(baseName + ".txt"));
		}

		eval.exit();
		System.exit(0);
	}
}
