#!/bin/bash
# jcz 13-apr-08
##################################
echo " ******************************* "
echo " This script reads all csvs into a single file in this directory"
echo " ******************************* "

for filers in *.csv
do


cat $filers >> largefile.txt

done