import re
import os

def calculate_and_print_status_percentages(file_path):

    with open(file_path, 'r') as file:
        data = file.read()

    statuses = re.findall(r"Game Status: (\w+)", data)

    total_levels = len(statuses)
    win_count = statuses.count("WIN")
    timeout_count = statuses.count("TIME_OUT")
    lose_count = statuses.count("LOSE")

    if(total_levels!=100):
        win_percentage = (win_count / total_levels) * 100 if total_levels > 0 else 0
        timeout_percentage = (timeout_count / total_levels) * 100 if total_levels > 0 else 0
        lose_percentage = (lose_count / total_levels) * 100 if total_levels > 0 else 0
    else:
        win_percentage= win_count
        timeout_percentage = timeout_count
        lose_percentage = lose_count

    print(f"Wins: {win_percentage}%")
    print(f"Time outs: {timeout_percentage}%")
    print(f"Lose: {lose_percentage}%")
    return win_percentage, timeout_percentage, lose_percentage

def calculate_win_rate_and_check_levels(file_path, start_level, end_level):
    with open(file_path, 'r') as file:
        data = file.read().strip().split('\n')

    wins = 0
    total_levels = 0
    found_levels = set()  

    for line in data:
        if line.startswith("level:"):
            level_name = line.split(": ")[1]
            try:
             level_number = int(re.search(r"-\d+(?!.*-\d+)", level_name).group()[1:])  
            except AttributeError:
                print(f"Skipping invalid level name: {level_name}")
                continue
            if start_level <= level_number <= end_level:
                found_levels.add(level_number)  
        elif line.startswith("win:") and start_level <= level_number <= end_level:
            total_levels += 1
            if "true" in line:
                wins += 1

    if total_levels == 0:
        print(f"No levels found in range {start_level} to {end_level}.")
        return

    win_rate = (wins / total_levels) * 100
    print(f"Total Levels in range {start_level} to {end_level}: {total_levels}")
    print(f"Wins: {wins}")
    print(f"Win Rate: {win_rate:.2f}%")

    missing_levels = [i for i in range(start_level, end_level + 1) if i not in found_levels]

    if missing_levels:
        print(f"Missing Levels in range {start_level} to {end_level}: {missing_levels}")
    else:
        print(f"All levels from {start_level} to {end_level} are present.")



print("Mff Astar Grid:")

file_path = os.path.join("ExampleInputs", "outputKrysTOADGAN-mffAstarGrid.txt")
calculate_win_rate_and_check_levels(file_path, 1, 100)

print()
print("Baumgarten:")

file_path = os.path.join("ExampleInputs", "outputKrysTOADGAN-Baumgarten.txt")
calculate_and_print_status_percentages(file_path)