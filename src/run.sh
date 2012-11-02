#!/bin/bash

ACCOUNTKEY='MWQrrA8YW+6ciAUTJh56VHz1vi/Mdqu0lSbzms3N7NY='
SITE='yahoo.com'
ES=0.6
EC=100

javac -cp ../lib/commons-codec-1.7.jar *.java
java -cp .:../lib/commons-codec-1.7.jar Driver $ACCOUNTKEY $ES $EC $SITE
# java -cp .:../lib/commons-codec-1.7.jar BingSearch
