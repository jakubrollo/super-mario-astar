# Script for creating the MechanicsGen fitness comparison graphs in .pdf format used in the thesis.

import os
import re
import numpy as np
import matplotlib.pyplot as plt
from collections import defaultdict

ROOT_DIR = "./ExampleInputs/results_MechanicsGen"
GRAPH_DIR = "./MechanicsGen_Graphs"
CATEGORIES = ["LimitedAgents", "PunishingModel"]

plt.rcParams.update({
    'font.size': 18,
    'axes.titlesize': 20,
    'axes.labelsize': 18,
    'xtick.labelsize': 16,
    'ytick.labelsize': 16,
    'legend.fontsize': 16,
    'figure.titlesize': 22
})

os.makedirs(GRAPH_DIR, exist_ok=True)

def parse_result_txt(file_path):
    max_fitness_values = []
    with open(file_path, 'r') as f:
        for line in f:
            match = re.match(r'Batch number \d+:\s*(.*)', line)
            if match:
                values = list(map(float, match.group(1).split(',')))
                if len(values) >= 2:
                    max_fitness_values.append(values[1])
    return max_fitness_values

def collect_data():
    data = defaultdict(lambda: defaultdict(lambda: defaultdict(list)))
    for category in CATEGORIES:
        category_path = os.path.join(ROOT_DIR, category)
        for strategy in os.listdir(category_path):
            strategy_path = os.path.join(category_path, strategy)
            if not os.path.isdir(strategy_path):
                continue
            for agent in ["Baumgarten", "MFFAstar"]:
                agent_path = os.path.join(strategy_path, agent)
                if not os.path.exists(agent_path):
                    continue
                for i in range(1, 6):
                    run_path = os.path.join(agent_path, str(i), "result.txt")
                    if os.path.exists(run_path):
                        max_fitness = parse_result_txt(run_path)
                        if not all(v == 0 for v in max_fitness):
                            data[category][strategy][agent].append(max_fitness)
    return data

def plot_avg_max_fitness(data):
    for category in data:
        for strategy in data[category]:
            baum_runs = data[category][strategy]["Baumgarten"]
            mff_runs = data[category][strategy]["MFFAstar"]

            baum_avg = np.mean(np.array(baum_runs), axis=0) if baum_runs else np.array([])
            mff_avg = np.mean(np.array(mff_runs), axis=0) if mff_runs else np.array([])

            generations = list(range(len(baum_avg))) if len(baum_avg) > 0 else list(range(len(mff_avg)))

            plt.figure(figsize=(10, 5))
            if baum_avg.any():
                plt.plot(generations, baum_avg, label="Baumgarten's Agent", color='red', linewidth=2.5)
            if mff_avg.any():
                plt.plot(generations, mff_avg, label="MFF A* Agent", color='blue', linewidth=2.5)

            plt.title(f"Avg Max Fitness Over Generations – {strategy} ({category})")
            plt.xlabel("Generation")
            plt.ylabel("Avg Max Fitness")
            plt.legend()
            plt.grid(True)
            plt.tight_layout()

            filename = f"{category}_{strategy}.pdf".replace(" ", "_")
            plt.savefig(os.path.join(GRAPH_DIR, filename))
            plt.close()

data = collect_data()
plot_avg_max_fitness(data)
