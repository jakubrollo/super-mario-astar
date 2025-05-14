#Script for creating the DagstuhlGAN fitness comparison graphs in .pdf format used in the thesis.

import matplotlib.pyplot as plt
import numpy as np
import os
from matplotlib import colors as mcolors
from matplotlib.lines import Line2D
from matplotlib.legend_handler import HandlerTuple
import colorsys

main_folder_name = r"ExampleInputs/DagstuhlGAN_example_result"

colors = ['lightblue', 'lightgreen', 'plum', 'moccasin', 'lightcoral', 'paleturquoise', 'lightgray', 'khaki']

plt.rcParams.update({
    'font.size': 18,
    'axes.titlesize': 20,
    'axes.labelsize': 18,
    'xtick.labelsize': 16,
    'ytick.labelsize': 16,
    'legend.fontsize': 16,
    'figure.titlesize': 22
})

def get_contrasting_color(current_color):
    rgb = np.array(mcolors.to_rgb(current_color))
    inverted_rgb = 1 - rgb
    h, s, v = colorsys.rgb_to_hsv(*inverted_rgb)
    s = min(1.0, s * 1.4)
    v = min(1.0, v * 1.8)
    saturated_rgb = colorsys.hsv_to_rgb(h, s, v)
    return mcolors.to_hex(saturated_rgb)

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
                        for line_idx, line in enumerate(file):
                            if line_idx >= 1000:
                                break
                            number = float(line.strip().split(': ')[-1])
                            numbers.append(number)
                    all_numbers.append(numbers)
            name = subfolder.replace("Astar", "A*")
            all_values[name] = all_numbers
    return all_values

def plot_levels_above_negative1(all_values, folder):
    plt.figure(figsize=(12, 8))
    legend_handles = []
    legend_labels = []
    mean_lines = {}
    trend_lines = {}
    trend_data = []

    for i, (method, values) in enumerate(all_values.items()):
        levels_above_negative1 = [0] * 1000
        for timeline in values:
            for j, fitness in enumerate(timeline):
                if j < len(levels_above_negative1) and fitness > -1:
                    levels_above_negative1[j] += 1
        color = colors[i % len(colors)]
        line, = plt.plot(range(1, 1001), levels_above_negative1, marker='o', color=color, alpha=0.9, linestyle='None')
        mean_lines[method] = {'line': line, 'color': color}

        trendline_color = get_contrasting_color(color)
        z = np.polyfit(range(1, 1001), levels_above_negative1, 3)
        p = np.poly1d(z)
        line_style = '-.' if method == "Baumgarten" else '--'
        trend_data.append({
            'method': method,
            'x': range(1, 1001),
            'y': p(range(1, 1001)),
            'color': trendline_color,
            'style': line_style,
            'final_value': p(1000)
        })

    for data in trend_data:
        method = data['method']
        trend, = plt.plot(data['x'], data['y'], linestyle=data['style'], color=data['color'], linewidth=3)
        trend_lines[method] = {
            'line': trend,
            'style': data['style'],
            'color': data['color'],
            'final_value': data['final_value']
        }

        handle_trend = Line2D([0], [0], linestyle=data['style'], color=data['color'], linewidth=3)
        handle_mean = Line2D([0], [0], marker='o', color=mean_lines[method]['color'], linestyle='None', markersize=8)
        legend_handles.append((handle_mean, handle_trend))
        legend_labels.append(method)

    sorted_indices = np.argsort([trend_lines[method]['final_value'] for method in trend_lines])
    sorted_handles = [legend_handles[i] for i in sorted_indices]
    sorted_labels = [legend_labels[i] for i in sorted_indices]
    sorted_handles.reverse()
    sorted_labels.reverse()

    plt.xlabel('Iteration')
    plt.ylabel('Number of levels above -1')
    plt.title('Levels Above Fitness -1 Across Generations - ' + folder)
    plt.legend(handles=sorted_handles, labels=sorted_labels, handler_map={tuple: HandlerTuple(ndivide=None)})
    plt.grid(True)
    plt.savefig(f'{folder}_levels_above_negative1.pdf', format='pdf')
   # plt.show()

def plot_mean_fitness(all_values, folder_name, plot_means=True):
    plt.figure(figsize=(12, 8))
    legend_handles = []
    legend_labels = []

    mean_lines = {}
    trend_lines = {}

    if plot_means:
        for i, (method, values) in enumerate(all_values.items()):
            if True or i != 0:
                max_length = max(len(sublist) for sublist in values)
                padded_values = [sublist + [np.nan] * (max_length - len(sublist)) for sublist in values]
                data_array = np.array(padded_values)
                average_values = np.nanmean(data_array, axis=0)
                color = colors[i % len(colors)]
                line, = plt.plot(range(len(average_values)), average_values, marker='o', color=color, alpha=0.9, linestyle='None')
                mean_lines[method] = {'line': line, 'color': color}


    for i, (method, values) in enumerate(all_values.items()):
        if True or i !=0: # for turning off certain runs 
            max_length = max(len(sublist) for sublist in values)
            padded_values = [sublist + [np.nan] * (max_length - len(sublist)) for sublist in values]
            data_array = np.array(padded_values)
            average_values = np.nanmean(data_array, axis=0)
            trendline_color = get_contrasting_color(colors[i % len(colors)])
            z = np.polyfit(range(len(average_values)), average_values, 3)
            p = np.poly1d(z)
            if(method=="Baumgarten's Agent"):
                line_style = '-.'
            else:
                line_style = '--'
            trend, = plt.plot(range(len(average_values)), p(range(len(average_values))), linestyle=line_style, color=trendline_color, linewidth=3)
            trend_lines[method] = {'line': trend, 'style': line_style, 'color': trendline_color, 'final_value': p(len(average_values)-1)}

            if method in mean_lines:
                 handle_trend = Line2D([0], [0], linestyle=trend_lines[method]['style'], color=trend_lines[method]['color'], linewidth=3)
                 handle_mean = Line2D([0], [0], marker='o', color=mean_lines[method]['color'], linestyle='None', markersize=8)
                 legend_handles.append((handle_mean, handle_trend))
                 legend_labels.append(f"{method}")
            else:
                 handle_trend = Line2D([0], [0], linestyle=trend_lines[method]['style'], color=trend_lines[method]['color'], linewidth=3)
                 legend_handles.append(handle_trend)
                 legend_labels.append(f"{method}")

    sorted_indices = np.argsort([trend_lines[method]['final_value'] for method in trend_lines])
    sorted_handles = [legend_handles[i] for i in sorted_indices]
    sorted_labels = [legend_labels[i] for i in sorted_indices]
    sorted_handles.reverse()
    sorted_labels.reverse()
    plt.xlabel('Iteration')
    plt.ylabel('Fitness')
    plt.title('Mean Fitness Over Time')
    plt.legend(handles=sorted_handles, labels=sorted_labels, handler_map={tuple: HandlerTuple(ndivide=None)})
    plt.grid()
    plt.savefig(folder_name + ".pdf", format='pdf')
   #  plt.show()

for folder_name in os.listdir(main_folder_name):
    folder_path = os.path.join(main_folder_name, folder_name)
    if os.path.isdir(folder_path):
        all_values = load_last_1000_values(folder_path)
        #plot_levels_above_negative1(all_values, folder_name)
        plot_mean_fitness(all_values, folder_name, True)