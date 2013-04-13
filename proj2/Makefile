default: all

all:
	rm -rf bin
	find ./src -name *.java > sources_list.txt
	mkdir bin
	javac -d bin/. @sources_list.txt
	rm sources_list.txt

classpath:
	rm -rf bin
	find ./src -name *.java > sources_list.txt
	mkdir bin
	javac -d bin/. -classpath "${CLASSPATH}" @sources_list.txt
	rm sources_list.txt

clean:
	rm -rf bin
