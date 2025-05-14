#!/bin/bash

BASE_RESULTS_DIR="/auto/vestec1-elixir/home/rolloj/results"
PBS_SCRIPT_PATH="/auto/vestec1-elixir/home/rolloj/metacentrum_FI2Pop_threads_MFF.sh"

for category in "$BASE_RESULTS_DIR"/*; do
    [ -d "$category" ] || continue
    category_name=$(basename "$category")

    for strategy in "$category"/*; do
        [ -d "$strategy/MFFAstar" ] || continue
        strategy_name=$(basename "$strategy")

        for run in "$strategy/MFFAstar"/[1-5]; do
            run_number=$(basename "$run")
            PARAM_FILE="$run/FI2PopParameters.txt"
            FINAL_RESULT_DIR="$run"
            JOB_NAME="${category_name}_${strategy_name}_MFFAstar_Plus_${run_number}_threads"

            echo "Submitting job: $JOB_NAME"

            qsub -v PARAM_FILE="$PARAM_FILE",FINAL_RESULT_DIR="$FINAL_RESULT_DIR" -N "$JOB_NAME" "$PBS_SCRIPT_PATH"
        done
    done
done

