#!/bin/bash
#PBS -q default@pbs-m1.metacentrum.cz
#PBS -N ${PBS_JOBNAME}
#PBS -l select=1:ncpus=28:mem=8gb:scratch_local=400mb:spec=13.1
#PBS -l walltime=2:00:00
#PBS -e /auto/vestec1-elixir/home/rolloj/job_logs
#PBS -o /auto/vestec1-elixir/home/rolloj/job_logs

echo "Job Started" >> $PBS_O_WORKDIR/job_start.log

module add openjdk-17

INPUT_DIR="$SCRATCHDIR/input_child"
OUTPUT_DIR="$SCRATCHDIR/output_child"
RESULT_DIR="$SCRATCHDIR/result"

FINAL_RESULT_DIR=${FINAL_RESULT_DIR:?FINAL_RESULT_DIR not set}
PARAM_FILE=${PARAM_FILE:?PARAM_FILE not set} 
PROJECT_DIR="/auto/vestec1-elixir/home/rolloj/MarioExperiments-master"  

mkdir -p $INPUT_DIR $OUTPUT_DIR $RESULT_DIR

cp -R $PROJECT_DIR $SCRATCHDIR || { echo >&2 "Error while copying project directory!"; exit 1; }
cp $PARAM_FILE $SCRATCHDIR || { echo >&2 "Error while copying FI2PopParameters.txt file!"; exit 2; }
cp $PARAM_FILE $SCRATCHDIR/MarioExperiments-master || { echo >&2 "Error while copying FI2PopParameters.txt file!"; exit 2; }
cp -R $PROJECT_DIR/levels3 $SCRATCHDIR || { echo >&2 "Error while copying level3 folder!"; exit 3; }

cd $SCRATCHDIR/MarioExperiments-master

javac -cp src src/FI2PopThreadParentRunnerMFF.java 

echo "Compiled parent" >> $PBS_O_WORKDIR/job_start.log   

java -cp src FI2PopThreadParentRunnerMFF 1000 25

wait

echo "Ran parent" >> $PBS_O_WORKDIR/job_start.log    

cp -a $SCRATCHDIR/result/. $FINAL_RESULT_DIR/ || { echo >> $PBS_O_WORKDIR/job_start.log "Error while copying output files to final result dir!"; exit 4; }

echo "Job ended" >> $PBS_O_WORKDIR/job_start.log

clean_scratch