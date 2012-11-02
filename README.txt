Project 2 for COMS E6111 Advanced Database Systems
-------------------------------------------------------------
a) Your name and your partner's name and Columbia UNI

Yuan Du (yd2234)
Akshai Sarma (as4107)

-------------------------------------------------------------
b) A list of all the files that you are submitting:
* Makefile		 (how to compile our code)
* README.txt 	 (this readme file)

-- src
* run.sh 		 (a shell script that runs all the codes)
* Driver.java 	 (the main java script runs both part1 and part2)
* Part1.java 	 (the main java script for part1)
* Part2.java 	 (the main java script for part2)
* BingSearch.java 	 (a script for querying Bing API)
* GetWordsLynx.java  (a tool script to parse web documents into words)
* SchemeTree.java 	 (a script for building the categorization scheme)
* TreeNode.java 	 (the defition of tree node in the scheme tree)

-- lib
* commons-codec-1.7.jar (the library package used in Bing search)

-- resources (first 4 files downloaded from the course page)
* computers.txt
* health.txt
* root.txt
* sports.txt
* scheme.txt 	(scheme file defining the categorization hierarchy)

-------------------------------------------------------------
c) A clear description of how to compile/run your program

-- Compile:
TODO Akshai

-- Run:
Similar to the reference, run the following from the directory where you put all the scripts (NOTE: you must cd to that directory before running this command):

./run.sh <BING_ACCOUNT_KEY> <t_es> <t_ec> <host>

, where:
<BING_ACCOUNT_KEY> is your Bing account key
<t_es> is the specificity threshold (between 0 and 1)
<t_ec> is the coverage threshold
<host> is the URL of the database to be classified

For example, on a CLIC machine:
cd /home/yd2234/ADB/proj2/code/COMSW6111_P2/src/
./run.sh '016GbojIly0TI22M7M71RWi513jNhJ04ZcYbF71MeYY=' 0.6 100 'fifa.org'

You can run our scripts directly by the commands above, since we have already put our scripts under that directory.

-------------------------------------------------------------
d) A clear description of the internal design of your project

-- Part 1
In this part, we have four java scripts: Part1.java, BingSearch.java, SchemeTree.java and TreeNode.java. The following are detailed descriptions for each of them.

1. BingSearch.java
This is a tool script for querying Bing API and parsing the XML results. The main functions include:
	* getDocNum: search via Bing API and return the number of documents matching the query in the given site
	* getTopFour: search via Bing API and return the top 4 research results of the given query
The first function getDocNum is used in part1 and the second function getTopFour will be used in part2.

2. SchemeTree.java
This is a script used to build the categorization scheme tree and add the queries for each node. The main functions include:
	* SchemeTree (constructor): create the scheme tree by the file scheme.txt (under the folder resources)
	* addQueryList: add the queries recursively into the tree nodes
The first function getDocNum is used in part1 and the second function getTopFour will be used in part2.

3. TreeNode.java
This is a script used to define two classes: 
	* TreeNode: define the class for nodes of the tree and its associated helper classes
	* addQueryList: define the class for storing the content summary - word + its document frequency 

4. Part1.java
This is the main java script for classifying databases in part1. The main functions include:
	* classifyDB: the main procedure of part1.
		This function calls those functions step by step:
		- createTree (create the scheme tree and add the query list)
		- getCategoryResults (get the classification results)
		- printClassification (print the classification for the given site)
	(Detailed information for each of them are below)

	* createTree: build the categorization scheme by calling the functions defined in SchemeTree.java. It returns the root of the scheme tree for traversal.
	* getCategoryResults: implement the algorithm in Figure 4 of the paper. Recursively visit the nodes from current node c to its children, and get the valid categories. 
	In this algorithm, we will check if the any child has large enough coverage and specificity. If so, we can go deeper into that child node.
	* getCoverage: get the coverage of one node in the given site. This is sum up the number of matching documents for all the queries in this node.
	It's called by function getCategoryResults to compute the specificity.

-- Part 2
TODO Akshai

-------------------------------------------------------------
e) Your Bing account key (so we can test your project):
016GbojIly0TI22M7M71RWi513jNhJ04ZcYbF71MeYY=

-------------------------------------------------------------
f) Any additional information that you consider significant.

1. How to deal with multiple-word entries:
TODO Akshai

2. How to deal with pdf:
TODO Akshai