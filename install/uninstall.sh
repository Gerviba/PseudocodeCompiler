#!/bin/bash
echo "Uninstalling PSeudoCode Runtime Environment"
echo "[i] Removing files"
rm /bin/psre
rm /opt/psre/psre.jar
rmdir /opt/psre

echo "[i] Done"
