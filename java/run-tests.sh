#!/usr/bin/env bash
# Compile and run the headless six-thread test suite.
set -euo pipefail
cd "$(dirname "$0")"
./compile.sh
java -cp out edu.aabu.os.sorting.SortingApplicationTest
