#!/usr/bin/env bash

USER_ME = 'jin'  # $USER on Mac
TARGET='seb:/home/jin/sites/pomo.kanxdoro.com'
LOCAL_DEV=${PWD}
echo "Must run from base directory"
echo "Running publish-master.sh"
echo "Publishing to 'pomo.kanxdoro.com' - ME =======> SERVER (Seb)"
read -p "Are you sure? (y/n) " -n 1 -r
if [[ $REPLY =~ ^[Yy]$ ]]
then
    echo "===============rsync-ing files==================="
    rsync -avz --omit-dir-times --del --no-perms --no-owner --no-group public/ seb:/home/jin/sites/pomo.kanxdoro.com
    # Why below not working?
    # rsync -avz --omit-dir-times --del --no-perms --no-owner --no-group $LOCAL_DEV/public $USER_ME@$TARGET 
    echo "================================="
    echo "Uploaded to " $TARGET
    echo "================================="
fi
