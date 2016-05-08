HOW TO RUN THE PROGRAM

Load all source code into a Java IDE, each in respective packages.



In bn.main, there are two classes, part1.java and part2.java. 

part1 runs the exact inference program with the enumeration technique. Use the command line arguments from Run Configurations to give arguments.
Here is the specified format:

<filename.xml/bif> <query_variable> <evidence_variable> <value_for_previous_evidence_variable> …

Here is an example of the command line on how to run part1.java

aima-alarm.xml B J true M true



part2 runs the approximate inference program, using REJECTION_SAMPLING. Use the command line arguments from Run Configurations to give arguments.
Here is the specified format:


<number_of_samples> <filename.xml/bif> <query_variable> <evidence_variable> <value_for_previous_evidence_variable> …

Here is an example of the command line on how to run part2.java

1000 aima-alarm.xml B J true M true