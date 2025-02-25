package mff.agents.benchmark;

import java.util.ArrayList;
import java.util.List;

public class LevelAnalyzer {

    // Convert the level string to a list of strings (rows of the grid)
    public static List<String> loadLevelFromString(String levelString) {
        String[] lines = levelString.split("\n");
        List<String> grid = new ArrayList<>();
        for (String line : lines) {
            grid.add(line.trim());
        }
        return grid;
    }

    public static int detectJumps(List<String> grid) {
        int rows = grid.size();
        int cols = grid.get(0).length();
        int[] lowestYPerColumn = new int[cols];
        int jumps = 0;
        int consecutiveJumpCols = 0;

        // Initialize lowestYPerColumn with row count (indicating no path found initially)
        for (int i = 0; i < cols; i++) {
            lowestYPerColumn[i] = rows;
        }

        // Find the lowest y-coordinate of the path in each column
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                if (grid.get(row).charAt(col) == '■') {
                    lowestYPerColumn[col] = Math.min(lowestYPerColumn[col], rows - row - 1);
                }
            }
        }

        // Detect jumps based on vertical movement
        for (int i = 1; i < cols; i++) {
            if (lowestYPerColumn[i] == rows || lowestYPerColumn[i - 1] == rows) {
                continue;  // Skip columns without a path
            }

            if (lowestYPerColumn[i] > lowestYPerColumn[i - 1]) {  // Current column is higher than previous
                consecutiveJumpCols++;
            } else {
                if (consecutiveJumpCols > 0) {  // End of a jump
                    jumps++;
                }
                consecutiveJumpCols = 0;  // Reset consecutive counter
            }

            // If consecutive increases reach 4 columns, count it as one jump and reset
            if (consecutiveJumpCols == 4) {
                jumps++;
                consecutiveJumpCols = 0;
            }
        }

        // Check if a jump was in progress at the end
        if (consecutiveJumpCols > 0) {
            jumps++;
        }

        // Detect horizontal pattern "■■■" with "X-X" below

        for (int row = 1; row < rows - 1; row++) {
            for (int col = 0; col < cols - 2; col++) {
                if (grid.get(row).charAt(col) == '■' &&
                        grid.get(row).charAt(col + 1) == '■' &&
                        grid.get(row).charAt(col + 2) == '■' &&
                        grid.get(row + 1).charAt(col) == 'X' &&
                        grid.get(row + 1).charAt(col + 1) == '-' &&
                        grid.get(row + 1).charAt(col + 2) == 'X') {
                    jumps++;
                }
            }
        }

        return jumps;
    }

    public static void main(String[] args) {
        // Example level grid as a string
        String levelString =
        "--------------------------------------------\n" +
        "--------------------------------------------\n" +
        "--------------------------------------------\n" +
        "--------------------------------------------\n" +
        "--------------------------------------------\n" +
        "--------------------------------------------\n" +
        "--------------------------------------------\n" +
        "------------------S-------------------------\n" +
        "--------------------------------------------\n" +
        "----------------------------------■■■■■■----\n" +
        "--------------------------------■■■-■■-■■■■■\n" +
        "-----------------------S------■■■X--X---■■--\n" +
        "---------------t-------------■■XXX------X---\n" +
        "--------------tt-------■■■--■■XXXX-----XXX--\n" +
        "--------------tt------■■-■■■■XXXXX-----XXX--\n" +
        "XXXXXXXXXXXXXXXXX---XXX---XXXXXXXXXXXXXXXXXX";

        // Convert the level string into a grid
        List<String> grid = loadLevelFromString(levelString);

        // Analyze the grid for jumps
        int jumpCount = detectJumps(grid);
        System.out.println("Number of jumps detected: " + jumpCount);
    }
}
