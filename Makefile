agent: agent.class
agent.class: ReversiGame.java Node.java
	javac ReversiGame.java Node.java
run: agent.class
	java ReversiGame Neighbour
clean:
	rm -rf *.class
