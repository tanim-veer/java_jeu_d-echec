#!/bin/bash
cd "$(dirname "$0")" || exit 1
javac -d out-test $(find src test -name "*.java") || exit 1
exec java -cp out-test chess.useCases.MoveGeneratorTest
