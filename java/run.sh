#!/usr/bin/env bash
# Compile (if needed) and launch the Swing desktop application.
set -euo pipefail
cd "$(dirname "$0")"
if [ ! -d out ]; then ./compile.sh; fi
java -cp out edu.aabu.os.sorting.Main
