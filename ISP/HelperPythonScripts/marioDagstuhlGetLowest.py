import os

folder_path = r"ExampleInputs\BacktrackedNodes_smallExample\Astar"

def find_lowest_lines(folder):
    lowest_number = float('inf')  
    lowest_lines = []  

    for i in range(2):   # don't forget to change to 100
        file_name = os.path.join(folder, f'timeline{i}.txt')  

        with open(file_name, 'r') as file:
            for line in file:
                number = float(line.strip().split(': ')[-1])

                if number < lowest_number:
                    lowest_number = number  
                    lowest_lines = [(file_name, line.strip())]  
                elif number == lowest_number:
                    lowest_lines.append((file_name, line.strip()))  

    return lowest_number, lowest_lines

lowest_number, lowest_lines = find_lowest_lines(folder_path)

print(f"Lowest number: {lowest_number}")
print("Lines with the lowest number:")
for file_name, line in lowest_lines:
    print(f"{file_name}: {line}")