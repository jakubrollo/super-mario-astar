# Attachement Overview

This attachement contains scripts and tools related to procedural content generation and evaluation for Super Mario Bros. levels. It includes the implementations of TOAD-GAN (to be more exact TOAD-GUI), DagstuhlGAN, MechanicsGen and various helper scripts for testing, visualization, and parallel execution in the Metacentrum grid.

## Folder Descriptions

### TOAD-GAN
(Original repository: https://github.com/Mawiszus/TOAD-GUI)

Contains tools for generating Mario levels using the TOAD-GUI framework. Includes:
- A modified TOAD-GUI for generating and visualizing levels in batches.
- Pretrained models (`krys-1`, original levels) and level outputs.
- Levels generated with different models and a dataset of 1000 randomly generated levels.
- Note: TOAD-GAN itself is not included (as it wasn't modified), only used for the generation of levels. It can be cloned from https://github.com/Mawiszus/TOAD-GAN and used to train your own generators.
- The original README file from the repository.

### DagstuhlGAN
(Original repository: https://github.com/CIGbalance/DagstuhlGAN)

Contains an implementation of CMA-ES optimization over latent vectors using DagstuhlGAN-trained models. Includes:
- Fitness evaluation framework with multiple metrics (jump count, backtracking, etc.).
- Tools for training and testing GANs in Python and Java.
- A custom-trained `krys-2` generator in the `netG_epoch_14900_0_32.pth` file. The `original 1-1` generator is in `netG_epoch_5000.pth` file.
- Tools to view, save, and render levels from vectors.
- The MFF A* implementation here is the original MFF A* version.
- The original README file from the repository.

### MechanicsGen
(Original repository: https://github.com/amidos2006/MarioExperiments)

Includes scripts and configurations used in the "Intentional Computational Level Design" approach. 
Includes:
- Evolving levels that express specific gameplay mechanics using FI2Pop.
- Caching and limited agents impementation.
- An optimized version of the the Slim forward model and MFF A* agent, referred to as MFF A* Plus.
- Support for event detection via a `mustDo` system, allowing agents to identify if specific gameplay events were triggered.
- A collection of original levels, Krys levels, and TOAD-GAN-generated levels.
- Does not include a README file, as the original repository did not provide one.

### Helper_Scripts
A collection of Python scripts for:
- Parsing and analyzing result files (`timeline*.txt`).
- Plotting training progress and fitness evolution.
- Extracting best-performing vectors from evolutionary runs.

This folder also contains example input files to demonstrate how the scripts can be used in practice.
To save space, the Dagstuhl example results don't contain most vectors, keeping only some with the lowest fitness values. Similarly the MechanicsGen example results don't contain all the chromosomes evaluated in the last iteration.

**Note:** Some of these scripts were refined or extended with the assistance of ChatGPT, particularly for improving plotting  (e.g., for visual clarity) and automation.

### Metacentrum_Scripts
Scripts designed to run experiments on the Metacentrum HPC cluster. Includes:
- PBS job submission scripts.
- Parallel execution helpers for large-scale testing.
- Tools for organizing and collecting results efficiently.

---

Each folder is self-contained and includes necessary files for reproducing experiments or continuing development.

More detailed documentation on the design, usage, and evaluation of these tools can be found in the accompanying thesis.