#!/bin/bash

# Ensure we're running as root user
if [ "$EUID" -ne 0 ]
  then echo "Please run as root"
  exit
fi

# If Java is not installed, install it
if [ -n `which java` ]; then
  apt-get install openjdk-8-jdk
fi

# Install SBT, which automatically installs Scala
echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
apt-get update
apt-get install sbt

# Install ammonite-scripts, ammonite-ops and ammonite-shell
mkdir ~/.ammonite
cp ./project/predef* ~/.ammonite

# Install Understand for x64 linux
wget http://latest.scitools.com/Understand/Understand-4.0.892-Linux-64bit.tgz
tar -xvzf Understand-4.0.800-Linux-64bit.tgz
mv scitools /usr/bin/
echo "export PATH=$PATH:/usr/bin/scitools/bin/linux64" >> ~/.bashrc
source ~/.bashrc

# Clone the reference repo java-design-patterns
git clone https://github.com/someone/java-design-patterns ./target/java-design-patterns
# geenrate dsm
projectPath=./target/java-design-patterns/
udb=$projectPath"project.udb"
cytoscape=$projectPath"cytoscape.xml"
dsm=$projectPath"project.dsm"
und create -db $udb -language java
und -db $udb add $projectPath
und analyze $udb
und export -dependencies file cytoscape $cytoscape $udb
java -jar ./jars/genSdsm-cmd-jdk1.6.jar -cytoscape -f $cytoscape -o $dsm > /dev/null
