default: Driver.class
Driver.class: src/Driver.java
	javac -sourcepath src/ -classpath lib/commons-codec-1.7.jar -d bin/ src/*.java

run:
	java -classpath bin/:lib/commons-codec-1.7.jar  Driver "$(ARG1)" "$(ARG2)" "$(ARG3)" "$(ARG4)"

clean:
	@rm -f bin/*
