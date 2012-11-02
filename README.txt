Project 2 for COMS E6111 Advanced Database Systems
-------------------------------------------------------------
a) Your name and your partner's name and Columbia UNI

Yuan Du (yd2234)
Akshai Sarma (as4107)

-------------------------------------------------------------
b) A list of all the files that you are submitting:
* Makefile		 (instructions on how to run the code)
* run.sh 		 (a shell script that runs all the codes)
* UI.py 		 (the main python script for the user interface and ranking)
* README.txt 	 (this readme file)
TODO...

-------------------------------------------------------------
c) A clear description of how to run your program

Similar to the reference, run the following from the directory where you put all the scripts (NOTE: you must cd to that directory before running this command):

./run.sh <BING_ACCOUNT_KEY> <t_es> <t_ec> <host>

, where:
<BING_ACCOUNT_KEY> is your Bing account key
<t_es> is the specificity threshold (between 0 and 1)
<t_ec> is the coverage threshold
<host> is the URL of the database to be classified

For example, on a CLIC machine:
cd /home/yd2234/ADB/proj2/code/COMSW6111_P2/src/run.sh
./run.sh 'MWQrrA8YW+6ciAUTJh56VHz1vi/Mdqu0lSbzms3N7NY=' 0.6 100 'cancer.org'

You can run our scripts directly by the commands above, since we have already put our scripts under that directory.

-------------------------------------------------------------
d) A clear description of the internal design of your project

