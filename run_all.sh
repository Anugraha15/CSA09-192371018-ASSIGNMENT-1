#!/bin/sh
# Compiles and runs every task. Usage: sh run_all.sh   (needs JDK 17+; developed on Java 21)
set -e
run() { dir=$1; shift; echo "=== $dir: $* ==="; rm -rf out/$dir; mkdir -p out/$dir
        (cd $dir && javac -d ../out/$dir $(find . -name '*.java')); for c in "$@"; do java -cp out/$dir $c; done; echo; }
run Task01 ParallelArraysDemo
run Task02 DataTypeDemo
run Task03 ParkingFacility AllocationBenchmark
run Task04 TicketDemo
run Task05 StaticDemo
run Task06 InheritanceDemo
run Task07 OverrideDemo
run Task08 FeeDemo OverloadDemo
run Task09 AbstractionDemo
run Task10 parking.app.Task10Demo
run Task11 parking.app.Main parking.app.TestRunner parking.app.Benchmark
run Task12 parking.app.Main parking.app.TestRunner
