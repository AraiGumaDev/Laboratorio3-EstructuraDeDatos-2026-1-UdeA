#!/usr/bin/env bash
set -e

cd "$(dirname "$0")"

mkdir -p out
javac -d out -sourcepath src/arboles_b src/arboles_b/arboles_b/*.java

java -cp out arboles_b.Main
