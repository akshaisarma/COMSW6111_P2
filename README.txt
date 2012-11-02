							README (please read in a window with columns >= 120)
-------------------------------------------------------------------------------------------------------------

Project 2 for COMS E6111 Advanced Database Systems
-------------------------------------------------------------
a) Your name and your partner's name and Columbia UNI

Yuan Du (yd2234)
Akshai Sarma (as4107)

-------------------------------------------------------------
b) A list of all the files that you are submitting:
* Makefile		 (to compile and run our code)
* README.txt 	 (this readme file)

-- src
* Driver.java 	 (the main java class that runs both part1 and part2)
* Part1.java 	 (the main java class for part1)
* Part2.java 	 (the main java class for part2)
* BingSearch.java 	 (a class for querying Bing API)
* GetWordsLynx.java  (a tool script to parse web documents into words - slightly modified from the provided one)
* SchemeTree.java 	 (a script for building the categorization scheme)
* TreeNode.java 	 (the definition of tree node in the scheme tree and associated structures)

-- lib
* commons-codec-1.7.jar (the library package used in Bing search for Base64 encoding)

-- resources (first 4 files downloaded from the course page)
* computers.txt
* health.txt
* root.txt
* sports.txt
* scheme.txt 	(our schema file defining the tree categorization hierarchy)

-------------------------------------------------------------
c) A clear description of how to compile/run your program

-- Compile:
Navigate to the base directory where the Makefile is and simply type make.  This will compile and put
the classes in ./bin

-- Run:
Navigate to the base directory where the Makefile is and type:
make run ARG1=<BING_ACCOUNT_KEY> ARG2=<t_es> ARG3=<t_ec> ARG4=<host>

, where:
<BING_ACCOUNT_KEY> is your Bing account key
<t_es> is the specificity threshold (between 0 and 1)
<t_ec> is the coverage threshold
<host> is the URL of the database to be classified

Example: make run ARG1=016GbojIly0TI22M7M71RWi513jNhJ04ZcYbF71MeYY= ARG2=0.5 ARG3=100000 ARG4=acm.org
-------------------------------------------------------------
d) A clear description of the internal design of your project

-- Driver
This is just our main function that calls Part1 and then calls Part2 with the generated tree
from Part1.

-- Part 1
In this part, we have four java files: Part1.java, BingSearch.java, SchemeTree.java and TreeNode.java.
The following are detailed descriptions for each of them.

1. BingSearch.java
This is a helper class for querying Bing API and parsing the XML results. The main functions include:
	* getDocNum: search via Bing API and return the number of documents matching the query in the given site
	* getTopFour: search via Bing API and return the top 4 search results of the given query
The first function getDocNum is used in part1 and the second function getTopFour is used in part2.

2. SchemeTree.java
This is a class used to build the categorization scheme tree and add the queries for each node. The main functions include:
	* SchemeTree (constructor): create the scheme tree by the file scheme.txt (under the folder resources)
	* addQueryList: add the queries in the four query files recursively into the tree nodes. In order to avoid
					building multiple lists for each node (for each of the subcategories), we instead chose to
					put the queries of the node with its children, thereby naturally associating the categories.
					This means that all queries are pushed down 1 level. That is, the root node doesn't store the
					queries in root.txt. Instead, they are split into its children. It is very easy to aggregate
					them and it saves us a lot of ugly code. This also means that leaves will store queries but
					of course, they are part of their parent's queries.

3. TreeNode.java
This is a file used to define two classes:
	* TreeNode: defines the class for nodes of the tree. It stores flags for whether the node is a leaf and
				stores a flag for whether the node was deemed worthy of expansion. This flag is used in Part 2
				to only consider these nodes (as we print the content summaries for them). If this flag is false,
				the content summary information and document sample information is empty.
	* ContentSummary: defined in Part 2 below.

4. Part1.java
This is the main java script for classifying databases in part1. The main functions include:
	* classifyDB: the main function for part1.
		This function calls those functions step by step:
		- createTree (create the scheme tree and add the query lists for each)
		- getCategoryResults (classify and get the classification results)
		- printClassification (print the classification for the given site)

	(Detailed information for each of them are below)

	* createTree: build the categorization schema by calling the functions defined in SchemeTree.java. It
				  returns the root of the schema tree for traversal. A straightforward mapping from the
				  scheme.txt file to the tree.

	* getCategoryResults: implement the algorithm in Figure 4 of the QProber paper. Recursively visit the
						  nodes from current node c to its children, and get the valid categories.
						  In this algorithm, we will check if the any child has large enough coverage and
						  specificity above the given thresholds. If so, we can go deeper into that child node.
						  All nodes called on by this function have their visited set to true. Note that this
						  means leaves will also be marked visited. This is okay because we don't have to print
						  content summaries for leaves anyway and conceptually, we mark it as visited because
						  the site is classified under it.

	* getCoverage: get the coverage of one node for the given site. This will sum up the number of matching
				   documents for all the queries in this node by using the BingSearcher function.
				   It's called by function getCategoryResults to compute the specificity.

-- Part 2
In this part, we have five classes in three files, some of which are the same files as Part1 (as they are
conceptually linked together).

1. TreeNode.java
This file defines in addition to what is used in Part1, the class for storing content summaries.
We store the document sample for a node at the node. The document sample of course, contains the sample of
the node UNION sample of the descendents. The content summary works in the same way and is the merging
of the content summaries of the immediate children (who should have merged their children and so on). Merging
simply refers to the addition of the counts in the natural way. Of course, this means that for a node, we must
only retrieve URLs that have not been retrieved by its descendants (i.e. in their document samples). And at the
end, we should merge with the children's summaries. It also has a method for returning the duplicate urls and
the number of times the duplicate occurs among its children. This will be used in Part2.

	* ContentSummary: defines the class for storing the content summary, including word and its document frequency.
					  We store it in a TreeMap rather than a HashMap in order to keep it sorted at the cost of
					  incurring the log(n) insertion costs. We believe that this is a reasonable design decision.
					  It has methods to merge two content summaries together and to add a set of words to it. The
					  former will be used at a parent, when all its child summaries are combined with its own
					  summary generated from URLs that only belong to it. The latter is used per URL to add a set
					  of words to its content summary (incrementing the count by 1 if already present). It also has
					  a method to push the summary to a text file. It also has a method to remove a bunch of words,
					  this was a hotfix we made in order to remove multiple counts in the case of duplicate urls
					  among children. If done properly, we would have changed architecture but we did this hackish
					  way instead but it works. The use of this function will be explained in Part2.java. It
					  basically adjusts the content summary's count of words to account for the duplicate urls among
					  children.

2. GetWordsLynx.java
This file is almost exactly the same as the reference one provided, except we store words in a HashSet. This
allows for fast probing as we insert into our TreeMap in ContentSummary. It helps with the insertion as this
set in created by making it O(1) rather than O(log(n)). Contains the class:
	* GetWordLynx: Contains the reference code that tokenizes and strips characters between [] and removes
				   the References section.

3. Part2.java
This contains the main body that runs Part2. Please refer to Part 2's discussion about TreeNode.java for the
information relevant to this.

	* NodeInformation: Since we are following the reference output line for line, for a parent, we
					   wanted to print the URLs retrieved by its children for each of the queries of its children.
					   In order to print the x/y lines, where y is the sum of all the queries made by this node
					   and its descendants, we decided to create a class to pass up this information. This also
					   saves us the trouble of walking the tree again and to not perform the set UNIONs of the
					   URLs all over again. This class stores QueryResults (below), in an ArrayList (so in order).
					   These are all the queries made by the children. There could be duplicates among the children
					   but that's ok, since we are not actually retrieving the documents and we are properly merging
					   the content summaries. This is just to print and match the reference implementation.

	* QueryResults: A trivial helper class that wraps the query and the set of URLs retrieved for it. NodeInformation
					stores an ArrayList of this.

	* Part2: The main class that does all the work for Part 2. The interface to this is the outputContentSummaries
			 function that simply calls the postOrder function with the root of the tree. Generating content
			 summaries is essentially a post order traversal of the nodes in the tree where the visited flag
			 is true. We generate the content summaries bottom up (ignoring leaves and not visited nodes). This
			 way, the immediate children of a node will always have the collected content summaries of all
			 nodes below it and the correct document samples. The main functions include (not listing trivial ones):

		* postOrder() : This is just a post order traversal of the visited nodes in recursive fashion.
						The NodeInformation collected so far is passed upward and is passed to the
						generateContentSummary function.
		* generateContentSummary(): This function takes the node to generate the content summary for and
									the NodeInformation collected so far and populates the samples and
									summary fields of the node. It first prints all the URLs in NodeInformation
									(refer to point 6 in section f and NodeInformation above). Then, it gets all
									the samples of the children in a set. For each query, the top 4 URLs are
									obtained. Any that are in the samples of the children are removed. These
									are the URLs that are distinct to this node. The lynx dump for this obtained and
									added to the content summary at this node. Finally, the UNION of the samples of the
									node is made with all the URLs retrieved for this node. We also add the queries
									made for this node to the accumulated NodeInformation (so that its parent can use
									it) Then, the content summaries of the children are merged with the content
									summary generated so far for this node. This is the right behavior if the URLs
									retrieved for the children do not have overlaps between them. For example,
									if nodes A and B of a parent C, have 3 URLs that are in both A and B, simply
									merging the content summaries will cause us to count the words in those
									duplicate URls twice. This is wrong. In order to fix it, we made this hackish
									way rather than change architecture at the last minute. This should be a
									sufficiently rare case because the categories should be distinct to prevent
									this. What we do is get a HashMap of the duplicated URLs and the count
									of the number of times it is duplicated (i.e in the example above, we should
									get 3 URLs with a count of 1). Using this we call the adjustSummary function that
									adjusts the just merged summary.

	* adjustSummary(): This is the hotfix method that has to (unfortunately), retrieve just these overlapping
					   urls again and gets the dump of the words. We then call the removeWordCounts function
					   (a memeber of the ContentSummary class), that takes the set of words from the dumped URL
					   and the number of times it appears. It then goes through the content summary (containing
					   the duplicates) and for each word, it adjusts the document frequency by v-v*count, where
					   v is the original df and count is the number of duplicates.

-------------------------------------------------------------
e) Your Bing account key (so we can test your project):
016GbojIly0TI22M7M71RWi513jNhJ04ZcYbF71MeYY=

Alternative key if that one reaches the limit of 5000 (it shouldn't):
pZPmzsJZw6szLfDZddw6SanWcDXTE/4qCbx8v6XemSY=

-------------------------------------------------------------
f) Any additional information that you consider significant.

1. How we deal with multiple words in content summaries:
As recommended in the project write up, we do not count multiple-word
entries. Since the lynx dumping class only works with words, phrases
do not even get into our content summary. In other words, rather than
printing a 0 for multi-word queries, we just don't print them at all. This
was okayed after discussion with the TAs. Also, we print Integers for
frequency rather than doubles.

2. How we deal with various different types of files:
After discussion with the TA, we decided to not distinguish file types
and dump the text of ALL files, including pdf files etc. This means that
we could get strange words in our summary but we think it is better than
writing ugly code to handle various different file types.

3. We solved the problem in full generality and is in no way tied to the particular
categories for the Project. You can load any tree schema into resources/scheme.txt
and it should work as long as you give the queries for each node as well.

4. We do not cache so apologies if the code takes a while to run!

5. We follow the reference implementation almost line by line. So, it should look
very similar.

6. For each node, all the URLs retrieved by its children are listed. However, if a
child A and B of a parent C, have URLs that were retrieved (by both A and B), it will
be printed. This is only for the printing. The content summaries are generated
properly in set fashion with duplicates removed (see our hotfix in Part2). We just wanted to
match the reference implementation and print the "Getting page" lines for all the URLs that
the children of this node made.
