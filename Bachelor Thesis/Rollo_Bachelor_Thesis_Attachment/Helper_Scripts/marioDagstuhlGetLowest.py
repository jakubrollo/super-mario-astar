# Script to find the X lowest fitness values across multiple timeline files in a folder, used for DagstuhlGAN.


import os

folder_path = r"ExampleInputs\DagstuhlGAN_example_result\OriginalJumpTest\MFF Astar Grid"
num_lowest_values = 1

def find_x_lowest_lines(folder, x):
    all_lines = []

    for i in range(3): # don't forget to change 100 to with larger timeline files
        file_name = os.path.join(folder, f'timeline{i}.txt')

        with open(file_name, 'r') as file:
            for line in file:
                number = float(line.strip().split(': ')[-1])
                all_lines.append((number, file_name, line.strip()))

    all_lines.sort(key=lambda x: x[0])

    lowest_values = []
    seen = set()

    for number, _, _ in all_lines:
        if number not in seen:
            seen.add(number)
            lowest_values.append(number)
            if len(lowest_values) == x:
                break

    result_lines = [(file, line) for number, file, line in all_lines if number in lowest_values]

    return lowest_values, result_lines

lowest_values, result_lines = find_x_lowest_lines(folder_path, num_lowest_values)

print(f"Lowest {num_lowest_values} fitness values: {lowest_values}")
print("Lines with those values:")
for file_name, line in result_lines:
    print(f"{file_name}: {line}")
