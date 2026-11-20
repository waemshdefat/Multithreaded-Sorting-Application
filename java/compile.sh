#!/usr/bin/env bash
# Compile the Java project. Requires JDK 17 or newer on PATH.
set -euo pipefail
cd "$(dirname "$0")"
rm -rf out
mkdir -p out
javac -Xlint:all -d out $(find src test -name '*.java')
echo "Compiled successfully into java/out"
