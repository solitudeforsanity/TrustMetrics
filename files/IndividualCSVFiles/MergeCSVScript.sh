OutFileName="/Users/Kaushy/Courses/Project/TrustMetricsTwitter/files/FinalCSV/3.csv"                       # Fix the output name
i=0                                       # Reset a counter
for filename in ./*.csv; do
    if [ "$filename"  != "$OutName" ] ;      # Avoid recursion
        then
        if [[ $i -eq 0 ]] ; then
            head -1  $filename >   $OutFileName # Copy header if it is the first file
        fi
        tail -n +2  $filename >>  $OutFileName # Append from the 2nd line each  file
    fi
    i=$(( $i + 1 ))                          # Increase the counter
done