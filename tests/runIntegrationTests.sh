#!/bin/bash
printf "RUNNING TESTS:\n\n"
DONE=0
ALL=0
for dir in */ ; do
    ((++ALL))
    cd $dir
    status=`sh ./test.sh`
    if [ "$status" = "0" ]; then
        echo "- [OK] $dir"
        ((++DONE))
    else
        echo "- [FAIL] $dir"
        echo "    status = $status"
    fi
    cd ../
done
printf "\nTESTS DONE | Tests: $ALL, Success: $DONE"
