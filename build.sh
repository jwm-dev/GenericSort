#!/bin/sh
# Script to build without Maven/Gradle/etc.
# Compile all Java files, outputting .class files to bin/
mkdir -p bin
javac -d bin Main.java src/*.java

# Run the program from bin/
java -cp bin Main