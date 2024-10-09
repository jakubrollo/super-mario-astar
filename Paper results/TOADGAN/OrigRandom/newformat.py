import re

def convert_results(input_file, output_file):
    with open(input_file, 'r') as f_in, open(output_file, 'w') as f_out:
        level_count = 0
        for line in f_in:
            if line.startswith("-------"):
                level_count += 1
            elif line.startswith("Game Status:"):
                result = re.search(r'Game Status: (\w+)', line)
                if result:
                    game_status = result.group(1)
                    if game_status == "WIN":
                        f_out.write(f"./levels/random-orig-lvls/lvl-{level_count}.txt\nwin: true\n")
                    else:
                        f_out.write(f"./levels/random-orig-lvls/lvl-{level_count}.txt\nwin: false\n")

#convert_results('Baumgarten_TOADGEN_OrigRandom.txt', 'simpleFormat_Baumgarten_TOADGEN_OrigRandom.txt')

def find_failed_levels(input_file):
    failed_levels = []
    level_count = 0
    failed_count = 0
    with open(input_file, 'r') as f_in:
        level = ''
        for line in f_in:
            if line.startswith("./levels/random-orig-lvls/lvl-"):
                level_count += 1
                level = line.strip()
            elif line.startswith("win: false"):
                failed_levels.append(level)
                failed_count += 1
    return failed_levels, level_count, failed_count

## Replace 'input_file.txt' with the path to your file
#failed_levels, level_count, failed_count = find_failed_levels('simpleFormat_Baumgarten_TOADGEN_OrigRandom.txt')

## Print the list of failed levels
#for level in failed_levels:
#    print(level)

## Print the number of levels checked
#print("Number of levels checked:", level_count)

## Print the number of levels with something other than "win: true"
#print("Number of failed levels:", failed_count)

################################################################################


def extract_failed_levels(input_file):
    failed_levels = set()
    with open(input_file, 'r') as f_in:
        for line in f_in:
            if line.startswith("./levels/random-orig-lvls/lvl-") and "win: false" in next(f_in):
                level = line.strip()
                failed_levels.add(level)
    return failed_levels

def check_corresponding_wins(fileA, fileB):
    failed_levels_A = extract_failed_levels(fileA)
    win_levels_B = set()
    with open(fileB, 'r') as f_in:
        for line in f_in:
            if line.startswith("./levels/random-orig-lvls/lvl-") and "win: true" in next(f_in):
                level = line.strip()
                win_levels_B.add(level)
    
    matching_levels = failed_levels_A.intersection(win_levels_B)
    return matching_levels

# Replace 'fileA.txt' and 'fileB.txt' with the paths to your files
matching_levels = check_corresponding_wins('mffAstar_TOADGEN_OrigRandom.txt', 'simpleFormat_Baumgarten_TOADGEN_OrigRandom.txt')

# Print the matching levels
if matching_levels:
    print("Matching levels where fileB has 'win: true' for levels that fileA has 'win: false':")
    for level in matching_levels:
        print(level)
else:
    print("No matching levels found.")
