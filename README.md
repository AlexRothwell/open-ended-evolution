# open-ended-evolution
An attempt at recreating OEE as my honours project from 2014

If you're interested in this work and would like to see the results and resulting thesis or just discuss it, contact me here as an issue and I'll get back to you!

## Requirements:
- NetLogo 5.1.0
- Java 8 (may work with Java 7)
- Apache Ant

## Setup:
1.	Set an environment variable “NETLOGO_HOME” to the directory containing the NetLogo installation. This folder must include NetLogo.jar
2.	From the directory containing the build.xml file, run “ant run”. This will build the extensions and start the model in NetLogo.
3.	To run the model, press the “setup” button, followed by the “go” button.
4.	To stop again, press the “go” button again.
5.	This will generate two .csv files using the string in the “outfile” box to store the complexity values and the energy shares generated during the run
