#!/bin/bash
#PBS -q default@pbs-m1.metacentrum.cz
#PBS -N ${PBS_JOBNAME}
#PBS -l select=1:ncpus=28:mem=64gb:scratch_local=400mb:spec=13.1
#PBS -l walltime=24:00:00
#PBS -e /auto/vestec1-elixir/home/rolloj/job_logs
#PBS -o /auto/vestec1-elixir/home/rolloj/job_logs

cleanup() {
    echo "Cleaning up scratch directory..." >> $PBS_O_WORKDIR/job_start.log
    clean_scratch
}

trap cleanup SIGTERM SIGKILL

echo "Job Started" >> $PBS_O_WORKDIR/job_start.log

module add openjdk-17

INPUT_DIR="$SCRATCHDIR/input_child"
OUTPUT_DIR="$SCRATCHDIR/output_child"
RESULT_DIR="$SCRATCHDIR/result"

FINAL_RESULT_DIR=${FINAL_RESULT_DIR:?FINAL_RESULT_DIR not set}  
PROJECT_DIR="/auto/vestec1-elixir/home/rolloj/MarioExperiments-master"  
PARAM_FILE=${PARAM_FILE:?PARAM_FILE not set}

mkdir -p $INPUT_DIR $OUTPUT_DIR $RESULT_DIR

cp -R $PROJECT_DIR $SCRATCHDIR || { echo >&2 "Error while copying project directory!"; exit 1; }
cp $PARAM_FILE $SCRATCHDIR || { echo >&2 "Error while copying FI2PopParameters.txt file!"; exit 2; }
cp $PARAM_FILE $SCRATCHDIR/MarioExperiments-master || { echo >&2 "Error while copying FI2PopParameters.txt file!"; exit 2; }
cp -R $PROJECT_DIR/levels3 $SCRATCHDIR || { echo >&2 "Error while copying level3 folder!"; exit 3; }

cd $SCRATCHDIR/MarioExperiments-master

javac -cp src src/FI2PopChildRunner.java src/FI2PopParentRunner.java 

for i in $(seq 0 24); do
    java -cp src FI2PopChildRunner $i 4 &
    echo $! >> child_pids.txt
    echo "Ran child $i" >> $PBS_O_WORKDIR/job_start.log
done

java -cp src FI2PopParentRunner 1000

while read pid; do
    kill "$pid" 2>/dev/null
done < child_pids.txt

rm child_pids.txt

echo "Ran parent" >> $PBS_O_WORKDIR/job_start.log    

cp -a $SCRATCHDIR/result/. $FINAL_RESULT_DIR/ || { echo >&2 "Error while copying output files to final result dir!"; exit 4; }

echo "Job ended" >> $PBS_O_WORKDIR/job_start.log

clean_scratch
