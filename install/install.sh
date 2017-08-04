#!/bin/bash
echo "Installing PSeudoCode Runtime Environment"
echo "[i] Moving files"
cp psre /bin/psre
mkdir /opt/psre
cp ../target/psre.jar /opt/psre/psre.jar
chmod 775 /bin/psre
chmod 775 /opt/psre/psre.jar

echo "[i] Testing"
psre --after-install
