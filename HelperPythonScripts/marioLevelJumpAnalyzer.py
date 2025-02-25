def load_level_from_string(level_string):

    return level_string.strip().split("\n")

def detect_jumps(grid):
    rows = len(grid)
    cols = len(grid[0])
    lowest_y_per_column = []
    jumps = 0
    consecutive_jump_cols = 0

    for col in range(cols):
        lowest_y = rows  
        for row in range(rows):
            if grid[row][col] == '■':  
                lowest_y = min(lowest_y, rows - row - 1)  
        lowest_y_per_column.append(lowest_y)

    for i in range(1, cols):
        if lowest_y_per_column[i] == rows or lowest_y_per_column[i - 1] == rows:
            continue  

        if lowest_y_per_column[i] > lowest_y_per_column[i - 1]:  
            consecutive_jump_cols += 1
        else:
            if consecutive_jump_cols > 0:  
                jumps += 1
            consecutive_jump_cols = 0  

        if consecutive_jump_cols == 4:
            jumps += 1
            consecutive_jump_cols = 0

    if consecutive_jump_cols > 0:
        jumps += 1

    for row in range(1, rows - 1):
        for col in range(cols - 2):
            if (grid[row][col] == '■' and grid[row][col + 1] == '■' and grid[row][col + 2] == '■' and
                grid[row + 1][col] == 'X' and grid[row + 1][col + 1] == '-' and grid[row + 1][col + 2] == 'X'):
                jumps += 1

    return jumps

level_string = """
--------------------------------------------
--------------------------------------------
--------------------------------------------
--------------------------------------------
--------------------------------------------
--------------------------------------------
--------------------------------------------
--------------------------------------------
--------------------------------------------
------------------------------------------■■
-----------------------------------■■■■■■■■-
----------------■■■--------------■■■X-X-XX--
-------------■■■■-■■----------■■■■XXX--XXX--
------------■■Xt---■■--------■■X-XXXX--XXX--
■■■■■■■■■■■■■-t-----■■■■■■■■■■--XXXXX--XXX--
XXXXXXXXXXXXXXXXXX--XXXX-XXXXXXXXXXXXXXXXXXX

"""

grid = load_level_from_string(level_string)

jump_count = detect_jumps(grid)
print("Number of jumps detected:", jump_count)