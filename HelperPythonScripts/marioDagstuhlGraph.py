import matplotlib.pyplot as plt
import numpy as np
import os
colors = ['blue', 'green', 'red', 'purple', 'orange', 'cyan', 'gray', 'yellow']

def load_last_1000_values(folder):
    all_values = {}

    for subfolder in os.listdir(folder):
        subfolder_path = os.path.join(folder, subfolder)

        if os.path.isdir(subfolder_path):
            all_numbers = []
            for i in range(1000):  
                file_name = os.path.join(subfolder_path, f'timeline{i}.txt')  

                if os.path.exists(file_name):
                    numbers = []  
                    with open(file_name, 'r') as file:
                        for line in file:

                            number = float(line.strip().split(': ')[-1])
                            numbers.append(number)

                    all_numbers.append(numbers[-10000:])  
            all_values[subfolder] = all_numbers  

    return all_values

def plot_values(all_values):
    for method, values in all_values.items():
        plt.figure(figsize=(12, 8))
        for i in range(len(values)):
            plt.scatter(range(len(values[i])), values[i], label=f'{method} timeline{i}', marker='o', s=5, color="black")
        plt.xlabel('Iteration')
        plt.ylabel('Fitness')
        plt.title(f'All Individuals - {method}')
        plt.show()

    plt.figure(figsize=(12, 8))

    for i, (method, values) in enumerate(all_values.items()):
        if True or i != 4 and i!=1:

            max_length = max(len(sublist) for sublist in values)
            padded_values = [sublist + [np.nan] * (max_length - len(sublist)) for sublist in values]
            data_array = np.array(padded_values)
            average_values = np.nanmean(data_array, axis=0)
            color = colors[i % len(colors)]
            plt.plot(range(len(average_values)), average_values
                     , marker='o', color=color)

    for i, (method, values) in enumerate(all_values.items()):
        if True: #or i != 4 and i!=1:   #can exclude some tested models:
            max_length = max(len(sublist) for sublist in values)
            padded_values = [sublist + [np.nan] * (max_length - len(sublist)) for sublist in values]
            data_array = np.array(padded_values)
            average_values = np.nanmean(data_array, axis=0)
            color = colors[(i + 2) % len(colors)]
            z = np.polyfit(range(len(average_values)), average_values,4)  
            p = np.poly1d(z)  
            plt.plot(range(len(average_values)), p(range(len(average_values))), linestyle='--', color=color, label=f'Trendline: {method}')

    plt.xlabel('Iteration')
    plt.ylabel('Fitness')
    plt.title('Mean Fitness Over Time')
    plt.legend()
    plt.grid()
    plt.show()

folder_name = r"ExampleInputs\BacktrackedNodes_smallExample"
all_values = load_last_1000_values(folder_name)
plot_values(all_values)