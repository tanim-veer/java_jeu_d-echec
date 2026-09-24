#!/bin/bash
# Compile puis lance le moteur UCI (a declarer comme moteur dans ChessX / CuteChess).
cd "$(dirname "$0")" || exit 1
javac -d out $(find src -name "*.java") || exit 1
exec java -cp out Appli
