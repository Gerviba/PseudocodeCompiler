#!/bin/bash
java -Xmx256M -jar ../../target/psre.jar -c --input-file test.pss --output-file test.psc
echo $?
